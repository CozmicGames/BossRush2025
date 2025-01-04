package com.cozmicgames.physics

import com.cozmicgames.Constants

class PhysicsWorld(var width: Float, var height: Float) {
    private val colliders = arrayListOf<Collider>()

    private val minX get() = -width * 0.5f
    private val minY get() = -height * 0.5f
    private val maxX get() = width * 0.5f
    private val maxY get() = height * 0.5f

    fun addCollider(collider: Collider) {
        colliders.add(collider)
    }

    fun scaleSpeedX(collider: Collider, speed: Float): Float {
        var scale = 1.0f

        if (collider.minX < minX + Constants.WORLD_DECELERATION_BORDER && speed < 0.0f)
            scale = (collider.minX - minX) / Constants.WORLD_DECELERATION_BORDER

        if (collider.maxX > maxX - Constants.WORLD_DECELERATION_BORDER && speed > 0.0f)
            scale = (maxX - collider.maxX) / Constants.WORLD_DECELERATION_BORDER

        return speed * scale
    }

    fun scaleSpeedY(collider: Collider, speed: Float): Float {
        var scale = 1.0f

        if (collider.minY < minY + Constants.WORLD_DECELERATION_BORDER && speed < 0.0f)
            scale = (collider.minY - minY) / Constants.WORLD_DECELERATION_BORDER

        if (collider.maxY > maxY - Constants.WORLD_DECELERATION_BORDER && speed > 0.0f)
            scale = (maxY - collider.maxY) / Constants.WORLD_DECELERATION_BORDER

        return speed * scale
    }

    fun updateCollider(collider: Collider) {
        if (collider.minX < minX)
            collider.x = minX + collider.radius

        if (collider.maxX > maxX)
            collider.x = maxX - collider.radius

        if (collider.minY < minY)
            collider.y = minY + collider.radius

        if (collider.maxY > maxY)
            collider.y = maxY - collider.radius
    }
}