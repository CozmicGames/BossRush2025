package com.cozmicgames.states.boss1

import com.cozmicgames.Game
import com.littlekt.math.geom.degrees
import com.littlekt.util.seconds
import kotlin.math.sin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

interface BeakMovement {
    fun updateBeak(delta: Duration, beak: Beak)
}

class IdleBeakMovement : BeakMovement {
    private var time = Game.random.nextDouble().seconds

    override fun updateBeak(delta: Duration, beak: Beak) {
        time += delta

        beak.beakAngle = 8.0.degrees * sin(time.seconds * 3.0)
    }
}
