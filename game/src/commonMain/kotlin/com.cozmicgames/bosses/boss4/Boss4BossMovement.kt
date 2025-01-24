package com.cozmicgames.bosses.boss4

import com.cozmicgames.bosses.*
import com.cozmicgames.entities.worldObjects.PlayerShip
import com.cozmicgames.utils.lerp
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.radians
import com.littlekt.util.seconds
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class IdleBoss4BossMovement : BossMovement {
    private var timer = 0.0.seconds

    private var isFirstUpdate = true
    private var centerX = 0.0f
    private var centerY = 0.0f

    override fun update(delta: Duration, boss: Boss, transform: BossTransform) {
        if (isFirstUpdate) {
            centerX = boss.x
            centerY = boss.y
            isFirstUpdate = false
        }

        timer += delta
        transform.targetRotation = 10.0.degrees * sin(timer.seconds * 2.0)
        transform.targetX = centerX + 100.0f * cos(timer.seconds)
        transform.targetY = centerY + 100.0f * sin(timer.seconds)
    }
}
