package org.tianea.boxrecommend.core.vo

import org.optaplanner.core.api.domain.entity.PlanningEntity
import org.optaplanner.core.api.domain.lookup.PlanningId
import org.optaplanner.core.api.domain.variable.PlanningVariable
import org.tianea.boxrecommend.core.vo.Shape

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