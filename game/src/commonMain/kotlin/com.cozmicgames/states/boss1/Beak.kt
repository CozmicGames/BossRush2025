package com.cozmicgames.states.boss1

import com.littlekt.math.geom.degrees
import kotlin.time.Duration

class Beak(layer: Int) {
    val leftBeak = BeakPart(this, true, layer)
    val rightBeak = BeakPart(this, false, layer)

    var x = 0.0f
    var y = 0.0f

    var beakAngle = 0.0.degrees

    fun update(delta: Duration, movement: BeakMovement) {
        movement.updateBeak(delta, this)

        leftBeak.rotation = -beakAngle * 0.5f
        rightBeak.rotation = beakAngle * 0.5f
    }
}