package com.cozmicgames.bosses

import com.cozmicgames.Game
import com.littlekt.math.geom.Angle
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.radians
import com.littlekt.util.seconds
import kotlin.math.atan2
import kotlin.math.sin
import kotlin.math.sqrt
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

class DeadBossMovement(val currentAngle: Angle = 0.0.degrees) : BossMovement {
    private var timer = 0.0.seconds

    override fun update(delta: Duration, boss: Boss, transform: BossTransform) {
        timer += delta
        transform.targetRotation = currentAngle + 10.0.degrees * timer.seconds * 0.5f
        transform.targetX = boss.x
        transform.targetY = boss.y
    }
}

class DestinationBossMovement(private val targetX: Float, private val targetY: Float, private val onReached: () -> Unit = {}) : BossMovement {
    override fun update(delta: Duration, boss: Boss, transform: BossTransform) {
        val dx = targetX - boss.x
        val dy = targetY - boss.y
        val distance = sqrt(dx * dx + dy * dy)

        if (distance < 20.0f)
            onReached()

        transform.targetX = targetX
        transform.targetY = targetY
    }
}

class AimBossMovement(private val targetX: Float, private val targetY: Float) : BossMovement {
    override fun update(delta: Duration, boss: Boss, transform: BossTransform) {
        val dx = targetX - boss.x
        val dy = targetY - boss.y
        transform.targetRotation = atan2(dy, dx).radians - 90.0.degrees
    }
}

class SpinBossMovement(private val speed: Float = 90.0f) : BossMovement {
    override fun update(delta: Duration, boss: Boss, transform: BossTransform) {
        transform.targetRotation += speed.degrees * delta.seconds
    }
}

open class CompoundBodyMovement(movements: List<BossMovement> = emptyList()) : BossMovement {
    private val movements = movements.toMutableList()

    fun addMovement(movement: BossMovement) {
        movements.add(movement)
    }

    fun removeMovement(movement: BossMovement) {
        movements.remove(movement)
    }

    override fun update(delta: Duration, boss: Boss, transform: BossTransform) {
        movements.forEach { it.update(delta, boss, transform) }
    }
}

open class SequenceBossMovement(private val durationPerMovement: Duration, private val movements: List<BossMovement>) : BossMovement {
    private var timer = 0.0.seconds
    private var currentMovementIndex = 0

    override fun update(delta: Duration, boss: Boss, transform: BossTransform) {
        timer += delta

        if (timer >= durationPerMovement) {
            timer = 0.0.seconds
            currentMovementIndex = (currentMovementIndex + 1) % movements.size
        }

        movements[currentMovementIndex].update(delta, boss, transform)
    }
}
