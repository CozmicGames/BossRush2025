package com.cozmicgames.bosses.boss4

import com.cozmicgames.bosses.*
import com.cozmicgames.entities.worldObjects.PlayerShip
import com.cozmicgames.utils.lerp
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.radians
import com.littlekt.util.seconds
import kotlin.math.*
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
        transform.targetX = centerX + 600.0f * cos(timer.seconds)
        transform.targetY = centerY + 600.0f * sin(timer.seconds)
        transform.targetRotation = timer.seconds.radians - 30.0.degrees * (sin(timer.seconds * 3.0f) * 0.5f + 0.5f)


        //transform.targetRotation = atan2((transform.targetY - boss.y), (transform.targetX - boss.x)).radians
    }
}
