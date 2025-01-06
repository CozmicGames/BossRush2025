package com.cozmicgames.states.boss1

import com.littlekt.math.geom.degrees
import kotlin.time.Duration

class MovementController {
    var movement: Movement = WaveMovement(5.0.degrees, 3.0f, 0.2f)

    fun update(delta: Duration) {
    }
}