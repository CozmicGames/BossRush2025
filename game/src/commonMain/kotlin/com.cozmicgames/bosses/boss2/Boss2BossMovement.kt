package com.cozmicgames.bosses.boss2

import com.cozmicgames.bosses.*
import com.cozmicgames.utils.lerp
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.radians
import com.littlekt.util.seconds
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class IdleBoss2BossMovement : BossMovement {
    private var timer = 0.0.seconds

    override fun update(delta: Duration, boss: Boss, transform: BossTransform) {
        timer += delta

        transform.targetRotation = 5.0.degrees * sin(timer.seconds * 2.0)
    }
}

class AimBoss2BossMovement(private val targetX: Float, private val targetY: Float, private val flipped: Boolean) : BossMovement {
    override fun update(delta: Duration, boss: Boss, transform: BossTransform) {
        boss as? Boss2 ?: throw IllegalArgumentException("Boss must be a Boss2")

        if (flipped) {
            if (!boss.isFlipped && boss.x < targetX)
                boss.flip()
            else if (boss.isFlipped && boss.x > targetX)
                boss.flip()
        } else {
            if (!boss.isFlipped && boss.x > targetX)
                boss.flip()
            else if (boss.isFlipped && boss.x < targetX)
                boss.flip()
        }

        val dx = targetX - boss.x
        val dy = targetY - boss.y
        transform.targetRotation = atan(dy / dx).radians
    }
}

class FollowPlayerBoss2BossMovement(private val target: BossTarget, private val onReached: () -> Unit = {}) : BossMovement {
    companion object {
        private const val TARGET_DISTANCE = 500.0f
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
            transform.targetRotation = atan2(dy, dx).degrees
        }

        timer += delta
        //transform.targetRotation = 6.0.degrees * sin(timer.seconds * 0.5)
    }
}

open class FlyAttackBoss2BossMovement(private val playerX: Float, private val playerY: Float, private val aimTime: Duration = 3.0.seconds, private val onReached: () -> Unit = {}) : BossMovement {
    private val aimMovement = AimBoss2BossMovement(playerX, playerY, false)
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

class PierceAttackBoss2BossMovement(playerX: Float, playerY: Float, aimTime: Duration = 3.0.seconds) : BossMovement {
    private val flyAttackMovement = FlyAttackBoss2BossMovement(playerX, playerY, aimTime) {
        reached = true
    }
    private lateinit var destinationMovement: DestinationBossMovement

    private var reached = false
    private var isFirstUpdate = true

    override fun update(delta: Duration, boss: Boss, transform: BossTransform) {
        if (isFirstUpdate) {
            destinationMovement = DestinationBossMovement(boss.x, boss.y)
            isFirstUpdate = false
        }

        if (!reached)
            flyAttackMovement.update(delta, boss, transform)
        else
            destinationMovement.update(delta, boss, transform)
    }
}