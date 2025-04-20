package org.tianea.boxrecommend.core.vo

import org.tianea.boxrecommend.core.vo.Shape

data class Item(
    val id: Int,
    val width: Long,
    val height: Long,
    val length: Long,
    val shape: Shape = Shape.BOX,
    val collapsible: Boolean = false,
    val weight: Long = 0
)