package org.tianea.boxrecommend.domain.logging

import org.tianea.boxrecommend.core.vo.Bin
import org.tianea.boxrecommend.core.vo.ItemAssignment

class RecommendLogging

fun buildXYProjectionLog(assignments: List<ItemAssignment>, bin: Bin) = buildString {
    val maxZ = assignments.maxOfOrNull { (it.z ?: 0) + it.rotatedDimensions().third } ?: bin.length
    for (z in 0 until maxZ) {
        val grid = Array(bin.height.toInt()) { Array<String?>(bin.width.toInt()) { null } }

        for (a in assignments) {
            if (a.bin?.id != bin.id || a.x == null || a.y == null || a.z == null) continue
            val (w, h, l) = a.rotatedDimensions()
            for (dz in 0 until l) {
                if (a.z!! + dz != z) continue
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

        appendLine("Bin ${bin.id} [XY 평면 @ Z=$z]")
        for (row in grid.reversed()) {
            appendLine(row.joinToString(" | ", prefix = "| ", postfix = " |") { it ?: " " })
        }
        appendLine()
    }
}