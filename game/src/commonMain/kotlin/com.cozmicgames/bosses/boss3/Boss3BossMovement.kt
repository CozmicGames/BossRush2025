package com.cozmicgames.bosses.boss3

import com.cozmicgames.bosses.*
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

class IdleBoss3BossMovement : BossMovement {
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

class AimBoss3BossMovement(private val targetX: Float, private val targetY: Float) : BossMovement {
    override fun update(delta: Duration, boss: Boss, transform: BossTransform) {
        boss as? Boss3 ?: throw IllegalArgumentException("Boss must be a Boss3")

        val dx = targetX - boss.x
        val dy = targetY - boss.y
        transform.targetRotation = atan(dy / dx).radians
    }
}

class WiggleBoss3BossMovement : BossMovement {
    private var timer = 0.0.seconds

    override fun update(delta: Duration, boss: Boss, transform: BossTransform) {
        timer += delta
        transform.targetRotation = 10.0.degrees * sin(timer.seconds * 2.0)
        transform.targetX = boss.x
        transform.targetY = boss.y
    }
}

class SpinAttackBoss3BossMovement : BossMovement {
    private var timer = 0.0.seconds

    override fun update(delta: Duration, boss: Boss, transform: BossTransform) {
        timer += delta
        transform.targetRotation = 250.0.degrees * timer.seconds
    }
}

class GrabAttackBoss3BossMovement(private val target: BossTarget) : BossMovement {
    override fun update(delta: Duration, boss: Boss, transform: BossTransform) {
        boss as? Boss3 ?: throw IllegalArgumentException("Boss must be a Boss3")

        val dx0 = boss.arms[0].claw.x - target.x
        val dy0 = boss.arms[0].claw.y - target.y
        val distance0 = sqrt(dx0 * dx0 + dy0 * dy0)

        val dx1 = boss.arms[1].claw.x - target.x
        val dy1 = boss.arms[1].claw.y - target.y
        val distance1 = sqrt(dx1 * dx1 + dy1 * dy1)

        if (distance0 < distance1) {
            transform.targetX = target.x - (boss.arms[0].claw.x - boss.x)
            transform.targetY = target.y - (boss.arms[0].claw.y - boss.y)
        } else {
            transform.targetX = target.x - (boss.arms[1].claw.x - boss.x)
            transform.targetY = target.y - (boss.arms[1].claw.y - boss.y)
        }
    }
}

class FollowPlayerBoss3BossMovement(private val target: BossTarget, private val onReached: () -> Unit = {}) : BossMovement {
    companion object {
        private const val TARGET_DISTANCE = 400.0f
    }

    private var timer = 0.0.seconds

    override fun update(delta: Duration, boss: Boss, transform: BossTransform) {
        val dx = target.x - boss.x
        val dy = target.y - boss.y
        val distance = sqrt(dx * dx + dy * dy)

        if (distance < TARGET_DISTANCE) {
            transform.targetX = boss.x
            transform.targetY = boss.y
            onReached()
        } else {
            transform.targetX = lerp(boss.x, boss.x + dx / distance * TARGET_DISTANCE, 0.7f)
            transform.targetY = lerp(boss.y, boss.y + dy / distance * TARGET_DISTANCE, 0.7f)
        }

        timer += delta
        transform.targetRotation = 10.0.degrees * sin(timer.seconds * 2.0)
    }
}

class FlyAttackBoss3BossMovement(private val playerX: Float, private val playerY: Float, private val aimTime: Duration = 3.0.seconds, private val onReached: () -> Unit = {}) : BossMovement {
    private val aimMovement = AimBossMovement(playerX, playerY)
    private lateinit var destinationMovement: DestinationBossMovement

    private var speedModifier = 3.0f
    private var timer = 0.0.seconds
    private var isFirstUpdate = true

    override fun update(delta: Duration, boss: Boss, transform: BossTransform) {
        if (isFirstUpdate) {
            val dx = playerX - boss.x
            val dy = playerY - boss.y

            val targetX = playerX + dx * 2.0f
            val targetY = playerY + dy * 2.0f

            destinationMovement = DestinationBossMovement(targetX, targetY) {
                speedModifier = 1.0f
                onReached()
            }

            isFirstUpdate = false
        }

        timer += delta

        transform.moveSpeedModifier = speedModifier

        if (timer < aimTime)
            aimMovement.update(delta, boss, transform)
        else
            destinationMovement.update(delta, boss, transform)
    }
}
