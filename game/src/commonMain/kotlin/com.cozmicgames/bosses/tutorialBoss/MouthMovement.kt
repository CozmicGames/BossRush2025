package com.cozmicgames.bosses.tutorialBoss

import com.cozmicgames.Game
import com.cozmicgames.utils.lerpAngle
import com.littlekt.math.geom.degrees
import com.littlekt.util.seconds
import kotlin.math.sin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

interface MouthMovement {
    fun updateMouth(delta: Duration, mouth: Mouth)
}

class IdleMouthMovement : MouthMovement {
    private var time = Game.random.nextDouble().seconds

    override fun updateMouth(delta: Duration, mouth: Mouth) {
        time += delta
        val targetAngle = 25.0.degrees * (sin(time.seconds * 0.5f) * 0.5f + 0.5f)
        mouth.mouthAngle = lerpAngle(mouth.mouthAngle, targetAngle, 0.5f)
    }
}

class ParalyzedMouthMovement : MouthMovement {
    override fun updateMouth(delta: Duration, mouth: Mouth) {
        mouth.mouthAngle = lerpAngle(mouth.mouthAngle, 50.0.degrees, 0.5f)
    }
}
