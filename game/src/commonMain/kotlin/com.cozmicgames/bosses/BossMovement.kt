package com.cozmicgames.bosses

import com.cozmicgames.Game
import com.littlekt.math.geom.Angle
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.radians
import com.littlekt.util.seconds
import kotlin.math.atan
import kotlin.math.sin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

interface BossMovement {
    fun update(delta: Duration, boss: Boss, transform: BossTransform)
}

class ShakeBossMovement(val getStrength: () -> Float = { 1.0f }) : BossMovement {
    override fun update(delta: Duration, boss: Boss, transform: BossTransform) {
        val amplitude = getStrength() * 10.0f
        boss.x += Game.random.nextFloat() * amplitude - amplitude * 0.5f
        boss.y += Game.random.nextFloat() * amplitude - amplitude * 0.5f
        transform.targetX = boss.x
        transform.targetY = boss.y
    }
}

class ParalyzedBossMovement(val currentAngle: Angle = 0.0.degrees) : BossMovement {
    private var timer = 0.0.seconds

    override fun update(delta: Duration, boss: Boss, transform: BossTransform) {
        timer += delta
        transform.targetRotation = currentAngle + 2.0.degrees * sin(timer.seconds * 2.0)
    }
}

class AimBossMovement(private val targetX: Float, private val targetY: Float) : BossMovement {
    override fun update(delta: Duration, boss: Boss, transform: BossTransform) {
        val dx = targetX - boss.x
        val dy = targetY - boss.y
        transform.targetRotation = atan(dy / dx).radians - 90.0.degrees
    }
}
