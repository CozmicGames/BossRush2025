package com.cozmicgames.states.boss1

import com.littlekt.math.geom.degrees
import kotlin.time.Duration

class MovementController {
    var tentacleMovement: TentacleMovement = CompoundTentacleMovement()
    var beakMovement: BeakMovement = IdleBeakMovement()

    init {
        with(tentacleMovement as CompoundTentacleMovement) {
            addMovement(SwayTentacleMovement(15.0.degrees, 0.1f, 0.2f))
            addMovement(HangTentacleMovement())
            addMovement(WaveTentacleMovement(10.0.degrees, 0.3f, 0.2f))
        }
    }

    fun update(delta: Duration) {
    }
}