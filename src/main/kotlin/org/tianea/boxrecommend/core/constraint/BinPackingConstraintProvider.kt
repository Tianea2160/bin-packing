package org.tianea.boxrecommend.core.constraint

import org.optaplanner.core.api.score.stream.Constraint
import org.optaplanner.core.api.score.stream.ConstraintCollectors.sum
import org.optaplanner.core.api.score.stream.ConstraintFactory
import org.optaplanner.core.api.score.stream.ConstraintProvider
import org.tianea.boxrecommend.config.ConstraintPurpose
import org.tianea.boxrecommend.core.vo.ItemAssignment

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
            .groupBy { it.bin } // one entry per used bin
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