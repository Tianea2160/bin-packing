package org.tianea.boxrecommend.core.vo

data class Bin(
    val id: Int,
    val width: Long = 10,
    val height: Long = 10,
    val length: Long = 10,
    val buffer: Double = 0.0,  // percent, e.g., 0.1 for 10%
    val maxWeight: Long = 1
)