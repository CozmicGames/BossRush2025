package com.cozmicgames.physics

class Collider {
    var x = 0.0f
    var y = 0.0f
    var radius = 0.0f

    val minX get() = x - radius
    val minY get() = y - radius
    val maxX get() = x + radius
    val maxY get() = y + radius

    fun collidesWith(other: Collider): Boolean {
        val dx = x - other.x
        val dy = y - other.y
        val radius = radius + other.radius
        val radiusSquared = radius * radius
        return dx * dx + dy * dy < radiusSquared
    }
}