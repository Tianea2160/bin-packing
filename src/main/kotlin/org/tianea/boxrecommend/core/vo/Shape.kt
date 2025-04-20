package org.tianea.boxrecommend.core.vo

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