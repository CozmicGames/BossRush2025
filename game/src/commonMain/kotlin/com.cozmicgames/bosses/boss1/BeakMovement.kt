package com.cozmicgames.bosses.boss1

import com.cozmicgames.Game
import com.cozmicgames.utils.lerpAngle
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
        beak.beakAngle = lerpAngle(beak.beakAngle, 8.0.degrees * sin(time.seconds * 3.0), 0.5f)
    }
}

class ClosedBeakMovement : BeakMovement {
    override fun updateBeak(delta: Duration, beak: Beak) {
        beak.beakAngle = lerpAngle(beak.beakAngle, 5.0.degrees, 0.5f)
    }
}

class ScreamBeakMovement : BeakMovement {
    override fun updateBeak(delta: Duration, beak: Beak) {
        beak.beakAngle = lerpAngle(beak.beakAngle, 12.0.degrees, 0.5f)
    }
}

class ParalyzedBeakMovement : BeakMovement {
    override fun updateBeak(delta: Duration, beak: Beak) {
        beak.beakAngle = lerpAngle(beak.beakAngle, 70.0.degrees, 0.5f)
    }
}