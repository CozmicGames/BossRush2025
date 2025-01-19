package com.cozmicgames.bosses.boss2

import com.cozmicgames.bosses.Boss
import com.cozmicgames.bosses.BossMovement
import com.cozmicgames.bosses.BossTransform
import com.cozmicgames.entities.worldObjects.PlayerShip
import com.cozmicgames.utils.lerp
import com.littlekt.math.geom.degrees
import com.littlekt.util.seconds
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

class FollowPlayerBoss2BossMovement(private val ship: PlayerShip, private val onReached: () -> Unit = {}) : BossMovement {
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
            transform.targetRotation = atan2(dy, dx).degrees
        }

        timer += delta
        transform.targetRotation = 6.0.degrees * sin(timer.seconds * 0.5)
    }
}