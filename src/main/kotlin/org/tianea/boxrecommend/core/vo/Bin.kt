package org.tianea.boxrecommend.core.vo

import org.tianea.boxrecommend.domain.box.entity.Box

data class Bin(
    val id: Long,
    val width: Long = 10,
    val height: Long = 10,
    val length: Long = 10,
    val buffer: Double = 0.0,
    val maxWeight: Long = 10
) {
    companion object {
        fun from(box: Box): Bin = Bin(
            id = box.id,
            width = box.width,
            height = box.height,
            length = box.length,
            maxWeight = box.maxWeight
        )
    }
}