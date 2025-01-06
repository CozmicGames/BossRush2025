package com.cozmicgames.physics

import com.littlekt.graphics.Color
import com.littlekt.graphics.g2d.shape.ShapeRenderer

class Collider(var shape: CollisionShape, val userData: Any? = null) {
    var x = 0.0f
    var y = 0.0f

    val boundsMinX get() = x + shape.minX
    val boundsMinY get() = y + shape.minY
    val boundsMaxX get() = x + shape.maxX
    val boundsMaxY get() = y + shape.maxY
    val boundsWidth get() = boundsMaxX - boundsMinX
    val boundsHeight get() = boundsMaxY - boundsMinY

    fun update() {
        shape.update()
    }

    fun collidesWith(other: Collider): Boolean {
        val a = shape
        val b = other.shape

        if (boundsMaxX < other.boundsMinX || boundsMinX > other.boundsMaxX || boundsMaxY < other.boundsMinY || boundsMinY > other.boundsMaxY)
            return false

        return when {
            a is CircleCollisionShape && b is CircleCollisionShape -> CollisionUtils.collideCircleCircle(x, y, a.radius, other.x, other.y, b.radius)
            a is CircleCollisionShape && b is RectangleCollisionShape -> CollisionUtils.collideCircleRectangle(x, y, a.radius, other.x, other.y, b.width, b.width, b.angle)
            a is RectangleCollisionShape && b is CircleCollisionShape -> CollisionUtils.collideCircleRectangle(other.x, other.y, b.radius, x, y, a.width, a.height, a.angle)
            a is RectangleCollisionShape && b is RectangleCollisionShape -> CollisionUtils.collideRectangleRectangle(x, y, a.width, a.height, a.angle, other.x, other.y, b.width, b.height, b.angle)
            else -> false
        }
    }

    fun collidesWithLine(x0: Float, y0: Float, x1: Float, y1: Float, callback: (Float) -> Unit = {}): Boolean {
        return when (val shape = shape) {
            is CircleCollisionShape -> CollisionUtils.collideLineCircle(x0, y0, x1, y1, x, y, shape.radius)
            is RectangleCollisionShape -> CollisionUtils.collideLineRectangle(x0, y0, x1, y1, x, y, shape.width, shape.height, shape.angle, callback)
        }
    }

    fun drawDebug(renderer: ShapeRenderer) {
        when (val shape = shape) {
            is CircleCollisionShape -> renderer.circle(x, y, shape.radius, color = Color.RED)
            is RectangleCollisionShape -> renderer.rectangle(x - shape.width * 0.5f, y - shape.height * 0.5f, shape.width, shape.height, shape.angle, color = Color.RED)
        }

        renderer.rectangle(boundsMinX, boundsMinY, boundsWidth, boundsHeight, color = Color.GREEN)
    }
}