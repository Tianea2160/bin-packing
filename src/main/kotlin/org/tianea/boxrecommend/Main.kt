package org.tianea.boxrecommend


import org.optaplanner.core.api.domain.entity.PlanningEntity
import org.optaplanner.core.api.domain.lookup.PlanningId
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty
import org.optaplanner.core.api.domain.solution.PlanningScore
import org.optaplanner.core.api.domain.solution.PlanningSolution
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider
import org.optaplanner.core.api.domain.variable.PlanningVariable
import org.optaplanner.core.api.score.buildin.bendable.BendableScore
import org.optaplanner.core.api.score.stream.Constraint
import org.optaplanner.core.api.score.stream.ConstraintCollectors.sum
import org.optaplanner.core.api.score.stream.ConstraintFactory
import org.optaplanner.core.api.score.stream.ConstraintProvider
import org.optaplanner.core.api.solver.SolverFactory
import org.optaplanner.core.config.solver.SolverConfig
import org.optaplanner.core.config.solver.termination.TerminationConfig


fun main() {
    val items = listOf(
        Item(1, width = 1, height = 3, length = 3, shape = Shape.BOX),
        Item(2, width = 1, height = 1, length = 3, shape = Shape.BOX),
        Item(3, width = 1, height = 1, length = 3, shape = Shape.BOX),
        Item(4, width = 1, height = 1, length = 3, shape = Shape.BOX),
        Item(5, width = 3, height = 1, length = 1, shape = Shape.BOX),
        Item(6, width = 1, height = 3, length = 1, shape = Shape.BOX),
        Item(7, width = 1, height = 1, length = 3, shape = Shape.BOX),
        Item(8, width = 1, height = 1, length = 3, shape = Shape.BOX),
    )

    val bins = listOf(
        Bin(1, width = 3, height = 3, length = 3, buffer = 0.0),
        Bin(2, width = 3, height = 3, length = 3, buffer = 0.0),
    )
    val assignments = items.mapIndexed { idx, item -> ItemAssignment(id = idx, item = item) }

    val solution = BinPackingSolution(assignments, bins)

    val solverFactory = SolverFactory
        .create<BinPackingSolution>(
            SolverConfig()
                .withSolutionClass(BinPackingSolution::class.java)
                .withEntityClasses(ItemAssignment::class.java)
                .withConstraintProviderClass(BinPackingConstraintProvider::class.java)
                .withTerminationConfig(
                    TerminationConfig().apply {
                        unimprovedSecondsSpentLimit = 3L
                    }
                )
        )

    val solver = solverFactory.buildSolver()
    solver.addEventListener {
        val cur = it.newBestSolution
        println(cur.assignments)
    }
    val result = solver.solve(solution)

    println("=== 결과 ===")
    result.assignments.forEach {
        println("Item ${it.item.id} -> Bin ${it.bin?.id} | Rotation: ${it.rotation} | X: ${it.x}, Y: ${it.y}, Z: ${it.z}")
    }

    val bendableScore = result.score
    println("=== 점수 해석 ===")
    println("Hard Score:")
    bendableScore.hardScores().forEachIndexed { i, score ->
        val purpose = ConstraintPurpose.fromIndex(i, true)
        println(" - ${purpose?.description ?: "Hard[$i]"}: $score")
    }

    println("Soft Score:")
    bendableScore.softScores().forEachIndexed { i, score ->
        val purpose = ConstraintPurpose.fromIndex(i, false)
        println(" - ${purpose?.description ?: "Soft[$i]"}: $score")
    }

    println("Score: ${result.score}")
    result.assignments.groupBy { it.bin }
        .forEach { bin, assignments -> printXYProjection(assignments, bin!!) }
}

data class Item(
    val id: Int,
    val width: Long,
    val height: Long,
    val length: Long,
    val shape: Shape = Shape.BOX,
    val collapsible: Boolean = false,
    val weight: Long = 0
)

data class Bin(
    val id: Int,
    val width: Long = 10,
    val height: Long = 10,
    val length: Long = 10,
    val buffer: Double = 0.0,  // percent, e.g., 0.1 for 10%
    val maxWeight: Long = 1
)

@PlanningEntity
data class ItemAssignment(
    @PlanningId
    val id: Int = 0,

    val item: Item,

    @PlanningVariable(valueRangeProviderRefs = ["binRange"])
    var bin: Bin? = null,

    @PlanningVariable(valueRangeProviderRefs = ["xRange"])
    var x: Long? = null,

    @PlanningVariable(valueRangeProviderRefs = ["yRange"])
    var y: Long? = null,

    @PlanningVariable(valueRangeProviderRefs = ["zRange"])
    var z: Long? = null,

    @PlanningVariable(valueRangeProviderRefs = ["rotationRange"])
    var rotation: Rotation? = null
) {
    fun rotatedDimensions(): Triple<Long, Long, Long> {
        val (w, h, l) = Triple(item.width, item.height, item.length)
        return if (item.shape != Shape.BOX) {
            Triple(w, h, l)
        } else when (rotation) {
            Rotation.XYZ -> Triple(w, h, l)
            Rotation.XZY -> Triple(w, l, h)
            Rotation.YXZ -> Triple(h, w, l)
            Rotation.YZX -> Triple(h, l, w)
            Rotation.ZXY -> Triple(l, w, h)
            Rotation.ZYX -> Triple(l, h, w)
            null -> Triple(w, h, l)
        }
    }
}

fun printXYProjection(assignments: List<ItemAssignment>, bin: Bin) {
    val maxZ = assignments.maxOfOrNull { (it.z ?: 0) + it.rotatedDimensions().third } ?: bin.length
    for (z in 0 until maxZ) {
        val grid = Array(bin.height.toInt()) { Array<String?>(bin.width.toInt()) { null } }

        for (a in assignments) {
            if (a.bin?.id != bin.id || a.x == null || a.y == null || a.z == null) continue
            val (w, h, l) = a.rotatedDimensions()
            for (dz in 0 until l) {
                if (a.z!! + dz != z.toLong()) continue
                for (dy in 0 until h) {
                    for (dx in 0 until w) {
                        val y = (a.y!! + dy).toInt()
                        val x = (a.x!! + dx).toInt()
                        if (y in grid.indices && x in grid[0].indices) {
                            grid[y][x] = a.item.id.toString()
                        }
                    }
                }
            }
        }

        println("Bin ${bin.id} [XY 평면 @ Z=$z]")
        for (row in grid.reversed()) {
            println(row.joinToString(" | ", prefix = "| ", postfix = " |") { it ?: " " })
        }
        println()
    }
}

@PlanningSolution
class BinPackingSolution(
    @PlanningEntityCollectionProperty
    val assignments: List<ItemAssignment>,

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "binRange")
    val bins: List<Bin>,

    @ValueRangeProvider(id = "xRange")
    val xRange: List<Long> = (0L until (bins.maxOfOrNull { it.width } ?: 10L)).toList(),

    @ValueRangeProvider(id = "yRange")
    val yRange: List<Long> = (0L until (bins.maxOfOrNull { it.height } ?: 10L)).toList(),

    @ValueRangeProvider(id = "zRange")
    val zRange: List<Long> = (0L until (bins.maxOfOrNull { it.length } ?: 10L)).toList(),

    @ValueRangeProvider(id = "rotationRange")
    val rotationRange: List<Rotation> = Rotation.entries,

    @PlanningScore(bendableHardLevelsSize = 2, bendableSoftLevelsSize = 3)
    var score: BendableScore = BendableScore.zero(2, 3)
)

class BinPackingConstraintProvider : ConstraintProvider {
    override fun defineConstraints(factory: ConstraintFactory): Array<Constraint> {
        return arrayOf(
            binCapacityExceeded(factory),
            binWeightLimitExceeded(factory),
            noOverlap(factory),
            itemMustFitInBin(factory),
            minimizeBinUsage(factory)
        )
    }

    fun minimizeBinUsage(factory: ConstraintFactory): Constraint {
        return factory.forEach(ItemAssignment::class.java)
            .filter { it.bin != null }
            .groupBy({ it.bin }) // one entry per used bin
            .penalize(ConstraintPurpose.BIN_COUNT.score(1))
            .asConstraint("Minimize number of bins used")
    }

    fun binCapacityExceeded(factory: ConstraintFactory): Constraint {
        return factory.forEach(ItemAssignment::class.java)
            .groupBy({ it.bin }, sum { it ->
                val (w, h, l) = it.rotatedDimensions()
                it.item.shape.volume(w, h, l).toInt()
            })
            .filter { bin, totalSize ->
                val effectiveCapacity = (bin!!.width * bin.height * bin.length * (1.0 - bin.buffer)).toLong()
                totalSize > effectiveCapacity
            }
            .penalize(ConstraintPurpose.PHYSICAL_CONSTRAINT.score(1))
            .asConstraint("Bin capacity exceeded")
    }

    fun binWeightLimitExceeded(factory: ConstraintFactory): Constraint {
        return factory.forEach(ItemAssignment::class.java)
            .groupBy({ it.bin }, sum { it -> it.item.weight.toInt() })
            .filter { bin, totalWeight ->
                totalWeight > (bin?.maxWeight ?: 0)
            }
            .penalize(ConstraintPurpose.WEIGHT_CONSTRAINT.score(1))
            .asConstraint("Bin weight limit exceeded")
    }

    fun noOverlap(factory: ConstraintFactory): Constraint {
        return factory
            .forEachUniquePair(ItemAssignment::class.java)
            .filter { a, b ->
                if (a.bin != b.bin || a.bin == null) return@filter false

                val ax = a.x ?: return@filter false
                val ay = a.y ?: return@filter false
                val az = a.z ?: return@filter false
                val bx = b.x ?: return@filter false
                val by = b.y ?: return@filter false
                val bz = b.z ?: return@filter false

                val (aw, ah, al) = a.rotatedDimensions()
                val (bw, bh, bl) = b.rotatedDimensions()

                val shapeFactorA = if (a.item.collapsible) Triple(1.0, 1.0, 1.0) else a.item.shape.bufferFor(a.rotation)
                val shapeFactorB = if (b.item.collapsible) Triple(1.0, 1.0, 1.0) else b.item.shape.bufferFor(b.rotation)

                val scaledAw = (aw * shapeFactorA.first).toLong()
                val scaledAh = (ah * shapeFactorA.second).toLong()
                val scaledAl = (al * shapeFactorA.third).toLong()

                val scaledBw = (bw * shapeFactorB.first).toLong()
                val scaledBh = (bh * shapeFactorB.second).toLong()
                val scaledBl = (bl * shapeFactorB.third).toLong()

                (ax < bx + scaledBw) && (ax + scaledAw > bx) &&
                        (ay < by + scaledBh) && (ay + scaledAh > by) &&
                        (az < bz + scaledBl) && (az + scaledAl > bz)
            }
            .penalize(ConstraintPurpose.PHYSICAL_CONSTRAINT.score(1))
            .asConstraint("Items must not overlap")
    }

    fun itemMustFitInBin(factory: ConstraintFactory): Constraint {
        return factory.forEach(ItemAssignment::class.java)
            .filter {
                if (it.item.collapsible) {
                    return@filter false // skip dimension check for collapsible items
                }

                val (w, h, l) = it.rotatedDimensions()
                val x = it.x ?: return@filter false
                val y = it.y ?: return@filter false
                val z = it.z ?: return@filter false
                val bin = it.bin ?: return@filter false

                val shapeBuffer = it.item.shape.bufferFor(it.rotation)

                val scaledW = (w * shapeBuffer.first).toLong()
                val scaledH = (h * shapeBuffer.second).toLong()
                val scaledL = (l * shapeBuffer.third).toLong()

                x + scaledW > (bin.width * (1.0 - bin.buffer)).toLong() ||
                        y + scaledH > (bin.height * (1.0 - bin.buffer)).toLong() ||
                        z + scaledL > (bin.length * (1.0 - bin.buffer)).toLong()
            }
            .penalize(ConstraintPurpose.PHYSICAL_CONSTRAINT.score(1))
            .asConstraint("Item must fit within bin bounds")
    }
}

enum class Rotation {
    XYZ, XZY, YXZ, YZX, ZXY, ZYX
}


enum class Shape {
    BOX, CYLINDER, CONE, POUCH;

    fun bufferFor(rotation: Rotation?): Triple<Double, Double, Double> {
        return when (this) {
            BOX -> Triple(1.0, 1.0, 1.0)
            CYLINDER -> when (rotation) {
                Rotation.XYZ, Rotation.XZY -> Triple(1.1, 1.1, 1.0)
                Rotation.YXZ, Rotation.YZX -> Triple(1.1, 1.0, 1.1)
                Rotation.ZXY, Rotation.ZYX -> Triple(1.0, 1.1, 1.1)
                else -> Triple(1.1, 1.1, 1.0)
            }

            CONE -> Triple(1.15, 1.15, 1.15)
            POUCH -> Triple(1.05, 1.05, 1.05)
        }
    }

    fun volume(width: Long, height: Long, length: Long): Double {
        return when (this) {
            BOX -> width * height * length.toDouble()
            CYLINDER -> {
                val radius = width.coerceAtMost(length) / 2.0
                Math.PI * radius * radius * height
            }

            CONE -> {
                val radius = width.coerceAtMost(length) / 2.0
                (1.0 / 3.0) * Math.PI * radius * radius * height
            }

            POUCH -> width * height * length * 0.8
        }
    }
}

enum class ConstraintPurpose(
    val level: Int,
    val isHard: Boolean = false,
    val description: String,
) {
    PHYSICAL_CONSTRAINT(0, true, "물리 제약 위반 (겹침, 공간 초과 등)"),
    WEIGHT_CONSTRAINT(1, true, "무게 제한 초과"),
    BIN_COUNT(0, false, "사용된 박스 수 최소화"),
    WASTED_VOLUME(1, false, "빈 공간 최소화"),
    STACK_STABILITY(2, false, "무거운 물건은 아래에"),
    ;

    val isSoft = isHard.not()

    companion object {
        val HARD_LEVELS = entries.count { it.isHard }
        val SOFT_LEVELS = entries.count { it.isSoft }

        fun fromIndex(index: Int, isHard: Boolean): ConstraintPurpose =
            entries.firstOrNull { it.level == index && it.isHard == isHard }
                ?: error("No ConstraintPurpose found for index=$index and isHard=$isHard")
    }


    fun score(value: Int): BendableScore {
        return if (isHard) {
            BendableScore.ofHard(HARD_LEVELS, SOFT_LEVELS, level, value)
        } else {
            BendableScore.ofSoft(HARD_LEVELS, SOFT_LEVELS, level, value)
        }
    }
}