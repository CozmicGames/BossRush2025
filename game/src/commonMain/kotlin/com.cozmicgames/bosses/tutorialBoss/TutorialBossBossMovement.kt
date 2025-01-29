package com.cozmicgames.bosses.tutorialBoss

import com.cozmicgames.bosses.Boss
import com.cozmicgames.bosses.BossMovement
import com.cozmicgames.bosses.BossTransform
import com.littlekt.math.geom.degrees
import com.littlekt.util.seconds
import kotlin.math.sin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class IdleTutorialBossBossMovement : BossMovement {
    private var timer = 0.0.seconds

    override fun update(delta: Duration, boss: Boss, transform: BossTransform) {
        timer += delta
        transform.targetRotation = 8.0.degrees * sin(timer.seconds * 2.0)
    }
}