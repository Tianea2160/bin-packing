package org.tianea.boxrecommend.image

import javafx.application.Application
import javafx.scene.AmbientLight
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.PerspectiveCamera
import javafx.scene.Scene
import javafx.scene.paint.Color
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape.Box
import javafx.scene.shape.CullFace
import javafx.scene.shape.DrawMode
import javafx.scene.shape.Sphere
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.scene.transform.Rotate
import javafx.stage.Stage
import org.tianea.boxrecommend.core.vo.BinPackingSolution

class Box3DViewer : Application() {
    override fun start(stage: Stage) {
        lateinit var solution: BinPackingSolution

        val root = Group()
        // Add axes for reference
        val axisLength = 600.0
        val xAxis = Box(axisLength, 1.0, 1.0).apply {
            material = PhongMaterial(Color.RED)
            translateX = axisLength / 2
        }
        val yAxis = Box(1.0, axisLength, 1.0).apply {
            material = PhongMaterial(Color.GREEN)
            translateY = axisLength / 2
        }
        val zAxis = Box(1.0, 1.0, axisLength).apply {
            material = PhongMaterial(Color.BLUE)
            translateZ = axisLength / 2
        }
        root.children.addAll(xAxis, yAxis, zAxis)

        fun labeledSphere(label: String, x: Double, y: Double, z: Double, color: Color): Node {
            val sphere = Sphere(5.0).apply {
                translateX = x
                translateY = y
                translateZ = z
                material = PhongMaterial(color)
            }

            val text = Text(label).apply {
                fill = color
                font = Font.font(14.0)
                translateX = x + 10
                translateY = y
                translateZ = z
            }

            return Group(sphere, text)
        }

        root.children.addAll(
            labeledSphere("X", axisLength, 0.0, 0.0, Color.RED),
            labeledSphere("Y", 0.0, axisLength, 0.0, Color.GREEN),
            labeledSphere("Z", 0.0, 0.0, axisLength, Color.BLUE)
        )

        solution.bins.forEachIndexed { index, bin ->
            val binOffsetX = index * 300.0
            val binOffsetY = 0.0
            val binOffsetZ = 0.0

            val binBox = Box(bin.width * 20.0, bin.height * 20.0, bin.length * 20.0).apply {
                translateX = binOffsetX + bin.width * 10.0 - 150
                translateY = binOffsetY + bin.height * 10.0 - 150
                translateZ = binOffsetZ + bin.length * 10.0 - 150
                drawMode = DrawMode.LINE
                material = PhongMaterial(Color.BLACK)
                cullFace = CullFace.NONE
            }

            root.children.add(binBox)
        }

        solution.assignments
            .groupBy { it.bin?.id }
            .values
            .forEachIndexed { index, value ->
                val binOffsetX = index * 300.0
                val binOffsetY = 0.0
                val binOffsetZ = 0.0

                value.forEach { a ->
                    val (w, h, l) = a.rotatedDimensions()
                    val x = a.x ?: 0
                    val y = a.y ?: 0
                    val z = a.z ?: 0
                    val width = w.toDouble() * 20
                    val height = h.toDouble() * 20
                    val length = l.toDouble() * 20

                    val tx = x.toDouble() * 20 + (w * 10) + binOffsetX - 150
                    val ty = y.toDouble() * 20 + (h * 10) + binOffsetY - 150
                    val tz = z.toDouble() * 20 + (l * 10) + binOffsetZ - 150

                    val box = Box(width, height, length).apply {
                        translateX = tx
                        translateY = ty
                        translateZ = tz
                        drawMode = DrawMode.FILL
                        cullFace = CullFace.BACK
                        val goldenAngle = 137.508
                        val hue = (a.item.id * goldenAngle) % 360
                        material = PhongMaterial(Color.hsb(hue, 0.7, 0.9, 0.5))
                    }

                    val border = Box(width, height, length).apply {
                        translateX = tx
                        translateY = ty
                        translateZ = tz
                        drawMode = DrawMode.LINE
                        cullFace = CullFace.NONE
                        material = PhongMaterial(Color.BLACK)
                    }

                    root.children.addAll(box, border)
                }
            }

        val light = AmbientLight(Color.color(0.9, 0.9, 0.9))
        root.children.add(light)

        val scene = Scene(root, 800.0, 600.0, true)
        scene.fill = Color.LIGHTGRAY

        val camera = PerspectiveCamera(true).apply {
            translateX = 0.0
            translateY = 300.0  // slightly higher elevation
            translateZ = -500.0
            rotationAxis = Rotate.X_AXIS
            rotate = 45.0      // reduce downward tilt
            nearClip = 0.1
            farClip = 1000.0
            fieldOfView = 45.0
        }
        scene.camera = camera

        stage.title = "3D Packing View"
        stage.scene = scene
        stage.show()
    }
}
