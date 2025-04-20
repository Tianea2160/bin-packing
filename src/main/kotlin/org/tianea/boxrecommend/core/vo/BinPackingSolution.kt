package org.tianea.boxrecommend.core.vo

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty
import org.optaplanner.core.api.domain.solution.PlanningScore
import org.optaplanner.core.api.domain.solution.PlanningSolution
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider
import org.optaplanner.core.api.score.buildin.bendable.BendableScore
import org.tianea.boxrecommend.core.vo.ConstraintPurpose

@PlanningSolution
class BinPackingSolution(
    val id: Long,
    @PlanningEntityCollectionProperty
    val assignments: List<ItemAssignment>,

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "binRange")
    val bins: List<Bin>,

    @ValueRangeProvider(id = "xRange")
    private val xRange: List<Long> = (0L until (bins.maxOfOrNull { it.width } ?: 10L)).toList(),

    @ValueRangeProvider(id = "yRange")
    private val yRange: List<Long> = (0L until (bins.maxOfOrNull { it.height } ?: 10L)).toList(),

    @ValueRangeProvider(id = "zRange")
    private val zRange: List<Long> = (0L until (bins.maxOfOrNull { it.length } ?: 10L)).toList(),

    @ValueRangeProvider(id = "rotationRange")
    private val rotationRange: List<Rotation> = Rotation.entries,

    @PlanningScore(bendableHardLevelsSize = 2, bendableSoftLevelsSize = 3)
    var score: BendableScore = BendableScore.zero(
        ConstraintPurpose.Companion.HARD_LEVELS,
        ConstraintPurpose.Companion.SOFT_LEVELS
    )
) {
    fun isFeasible(): Boolean = this.score.isFeasible

    fun isNotFeasible(): Boolean = !isFeasible()
}