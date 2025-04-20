package org.tianea.boxrecommend.core.vo

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty
import org.optaplanner.core.api.domain.solution.PlanningScore
import org.optaplanner.core.api.domain.solution.PlanningSolution
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider
import org.optaplanner.core.api.score.buildin.bendable.BendableScore
import org.tianea.boxrecommend.config.ConstraintPurpose

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
    var score: BendableScore = BendableScore.zero(
        ConstraintPurpose.Companion.HARD_LEVELS,
        ConstraintPurpose.Companion.SOFT_LEVELS
    )
)