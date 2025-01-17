package com.cozmicgames.bosses

import com.cozmicgames.utils.Difficulty
import com.littlekt.graphics.g2d.shape.ShapeRenderer
import com.littlekt.math.geom.Angle
import kotlin.time.Duration

interface Boss {
    val difficulty: Difficulty

    val isDead get() = health <= 0
    val isParalyzed: Boolean

    val health: Int
    var x: Float
    var y: Float
    var rotation: Angle

    val movementController: BossMovementController

    fun addToWorld()
    fun removeFromWorld()
    fun addToPhysics()
    fun removeFromPhysics()
    fun update(delta: Duration)

    fun drawDebug(renderer: ShapeRenderer)
}