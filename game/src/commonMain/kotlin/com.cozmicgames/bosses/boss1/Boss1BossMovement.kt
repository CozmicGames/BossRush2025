package com.cozmicgames.bosses.boss1

import com.cozmicgames.bosses.AimBossMovement
import com.cozmicgames.bosses.Boss
import com.cozmicgames.bosses.BossMovement
import com.cozmicgames.bosses.BossTransform
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

class IdleBoss1BossMovement : BossMovement {
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

class WiggleBoss1BossMovement : BossMovement {
    private var timer = 0.0.seconds

    override fun update(delta: Duration, boss: Boss, transform: BossTransform) {
        timer += delta
        transform.targetRotation = 10.0.degrees * sin(timer.seconds * 2.0)
        transform.targetX = boss.x
        transform.targetY = boss.y
    }
}

class SpinAttackBoss1BossMovement : BossMovement {
    private var timer = 0.0.seconds

    override fun update(delta: Duration, boss: Boss, transform: BossTransform) {
        timer += delta
        transform.targetRotation = 250.0.degrees * timer.seconds
    }
}

class GrabAttackBoss1BossMovement(private val ship: PlayerShip) : BossMovement {
    private var timer = 0.0.seconds

    override fun update(delta: Duration, boss: Boss, transform: BossTransform) {
        timer += delta
        transform.targetX = ship.x
        transform.targetY = ship.y + 200.0f // Above the ship
        transform.targetRotation = atan((transform.targetY - boss.y) / (transform.targetX - boss.x)).radians - 90.0.degrees
    }
}

class FollowPlayerBoss1BossMovement(private val ship: PlayerShip, private val onReached: () -> Unit = {}) : BossMovement {
    companion object {
        private const val TARGET_DISTANCE = 500.0f
    }

    private var timer = 0.0.seconds

    override fun update(delta: Duration, boss: Boss, transform: BossTransform) {
        val dx = ship.x - boss.x
        val dy = ship.y - boss.y
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

class DestinationBoss1BossMovement(private val targetX: Float, private val targetY: Float, private val onReached: () -> Unit = {}) : BossMovement {
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

class FlyAttackBoss1BossMovement(private val playerX: Float, private val playerY: Float, private val aimTime: Duration = 3.0.seconds, private val onReached: () -> Unit = {}) : BossMovement {
    private val aimMovement = AimBossMovement(playerX, playerY)
    private lateinit var destinationMovement: DestinationBoss1BossMovement

    private var speedModifier = 3.0f
    private var timer = 0.0.seconds
    private var isFirstUpdate = true

    override fun update(delta: Duration, boss: Boss, transform: BossTransform) {
        if (isFirstUpdate) {
            val dx = playerX - boss.x
            val dy = playerY - boss.y

            val targetX = playerX + dx * 2.0f
            val targetY = playerY + dy * 2.0f

            destinationMovement = DestinationBoss1BossMovement(targetX, targetY) {
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
