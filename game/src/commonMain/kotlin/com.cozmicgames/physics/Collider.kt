package com.cozmicgames.physics

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

        return when {
            a is CircleCollisionShape && b is CircleCollisionShape -> CollisionUtils.collideCircleCircle(x, y, a.radius, other.x, other.y, b.radius)
            a is CircleCollisionShape && b is RectangleCollisionShape -> CollisionUtils.collideCircleRectangle(x, y, a.radius, other.x, other.y, b.width, b.width, b.angle)
            a is RectangleCollisionShape && b is CircleCollisionShape -> CollisionUtils.collideCircleRectangle(other.x, other.y, b.radius, x, y, a.width, a.height, a.angle)
            a is RectangleCollisionShape && b is RectangleCollisionShape -> CollisionUtils.collideRectangleRectangle(x, y, a.width, a.height, a.angle, other.x, other.y, b.width, b.height, b.angle)
            else -> false
        }
    }
}