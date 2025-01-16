package com.cozmicgames.bosses

import com.littlekt.math.geom.Angle
import kotlin.time.Duration

interface Boss {
    val isDead get() = health <= 0

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
}