package org.tianea.boxrecommend.core.vo

import org.tianea.boxrecommend.core.vo.Shape
import org.tianea.boxrecommend.domain.sku.entity.Sku

data class Item(
    val id: Long,
    val width: Long,
    val height: Long,
    val length: Long,
    val shape: Shape = Shape.BOX,
    val collapsible: Boolean = false,
    val weight: Long = 0
) {
    companion object {
        fun from(sku: Sku): Item {
            return Item(
                id = sku.id,
                width = sku.width,
                height = sku.height,
                length = sku.length,
                weight = sku.weight,
                shape = sku.shape
            )
        }
    }
}