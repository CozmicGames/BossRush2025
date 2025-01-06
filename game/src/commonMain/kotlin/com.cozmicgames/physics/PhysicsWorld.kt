package com.cozmicgames.physics

import com.cozmicgames.Constants

class PhysicsWorld(var width: Float, var height: Float) {
    val colliders get() = collidersInternal as List<Collider>

    private val collidersInternal = arrayListOf<Collider>()

    val minX get() = -width * 0.5f
    val minY get() = -height * 0.5f
    val maxX get() = width * 0.5f
    val maxY get() = height * 0.5f

    fun addCollider(collider: Collider) {
        collidersInternal.add(collider)
    }

    fun removeCollider(collider: Collider) {
        collidersInternal.remove(collider)
    }

    fun scaleSpeedX(collider: Collider, speed: Float): Float {
        var scale = 1.0f

        if (collider.boundsMinX < minX + Constants.WORLD_DECELERATION_BORDER && speed < 0.0f)
            scale = (collider.boundsMinX - minX) / Constants.WORLD_DECELERATION_BORDER

        if (collider.boundsMaxX > maxX - Constants.WORLD_DECELERATION_BORDER && speed > 0.0f)
            scale = (maxX - collider.boundsMaxX) / Constants.WORLD_DECELERATION_BORDER

        return speed * scale
    }

    fun scaleSpeedY(collider: Collider, speed: Float): Float {
        var scale = 1.0f

        if (collider.boundsMinY < minY + Constants.WORLD_DECELERATION_BORDER && speed < 0.0f)
            scale = (collider.boundsMinY - minY) / Constants.WORLD_DECELERATION_BORDER

        if (collider.boundsMaxY > maxY - Constants.WORLD_DECELERATION_BORDER && speed > 0.0f)
            scale = (maxY - collider.boundsMaxY) / Constants.WORLD_DECELERATION_BORDER

        return speed * scale
    }

    fun updatePlayerCollider(collider: Collider) {
        if (collider.boundsMinX < minX)
            collider.x = minX + collider.boundsWidth * 0.5f

        if (collider.boundsMaxX > maxX)
            collider.x = maxX - collider.boundsWidth * 0.5f

        if (collider.boundsMinY < minY)
            collider.y = minY + collider.boundsHeight * 0.5f

        if (collider.boundsMaxY > maxY)
            collider.y = maxY - collider.boundsHeight * 0.5f

        collider.update()
    }

    fun checkCollision(collider: Collider, filter: (Collider) -> Boolean = { true }, callback: (Collider) -> Unit) {
        collidersInternal.forEach {
            if (it != collider && filter(it) && collider.collidesWith(it))
                callback(it)
        }
    }

    fun getNearestCollision(collider: Collider, filter: (Collider) -> Boolean = { true }): Collider? {
        var nearest: Collider? = null
        var nearestDistance = Float.MAX_VALUE

        checkCollision(collider, filter) {
            val dx = it.x - collider.x
            val dy = it.y - collider.y
            val distance = dx * dx + dy * dy

            if (distance < nearestDistance) {
                nearest = it
                nearestDistance = distance
            }
        }

        return nearest
    }

    fun checkLineCollision(x1: Float, y1: Float, x2: Float, y2: Float, filter: (Collider) -> Boolean = { true }, callback: (Collider, Float) -> Unit) {
        collidersInternal.forEach {
            if (filter(it))
                it.collidesWithLine(x1, y1, x2, y2) { t ->
                    callback(it, t)
                }
        }
    }

    fun getNearestLineCollision(x1: Float, y1: Float, x2: Float, y2: Float, filter: (Collider) -> Boolean = { true }, callback: (Float) -> Unit = {}): Collider? {
        var nearest: Collider? = null
        var nearestDistance = Float.MAX_VALUE

        checkLineCollision(x1, y1, x2, y2, filter) { collider, t ->
            val dx = x1 + (x2 - x1) * t - collider.x
            val dy = y1 + (y2 - y1) * t - collider.y
            val distance = dx * dx + dy * dy

            if (distance < nearestDistance) {
                nearest = collider
                nearestDistance = distance
            }
        }

        if (nearest != null)
            callback(nearestDistance)

        return nearest
    }
}