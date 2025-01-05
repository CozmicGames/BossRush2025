package com.cozmicgames.physics

import com.littlekt.math.geom.Angle
import com.littlekt.math.geom.cosine

sealed interface CollisionShape {
    val minX: Float
    val minY: Float
    val maxX: Float
    val maxY: Float

    fun update() {}
}

class CircleCollisionShape(var radius: Float) : CollisionShape {
    override val minX get() = -radius
    override val minY get() = -radius
    override val maxX get() = radius
    override val maxY get() = radius
}

class RectangleCollisionShape(var width: Float, var height: Float, var angle: Angle) : CollisionShape {
    override var minX = 0.0f
        private set

    override var minY = 0.0f
        private set

    override var maxX = 0.0f
        private set

    override var maxY = 0.0f
        private set

    override fun update() {
        val cos = angle.cosine
        val sin = angle.cosine

        val x1 = -width * 0.5f
        val y1 = -height * 0.5f
        val x2 = width * 0.5f
        val y2 = height * 0.5f

        val x1r = cos * x1 - sin * y1
        val y1r = sin * x1 + cos * y1
        val x2r = cos * x2 - sin * y1
        val y2r = sin * x2 + cos * y1
        val x3r = cos * x2 - sin * y2
        val y3r = sin * x2 + cos * y2
        val x4r = cos * x1 - sin * y2
        val y4r = sin * x1 + cos * y2

        minX = minOf(x1r, x2r, x3r, x4r)
        minY = minOf(y1r, y2r, y3r, y4r)
        maxX = maxOf(x1r, x2r, x3r, x4r)
        maxY = maxOf(y1r, y2r, y3r, y4r)
    }
}
