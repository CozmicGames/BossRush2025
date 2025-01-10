package com.cozmicgames.states.boss1

import com.cozmicgames.entities.PlayerShip
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.radians
import com.littlekt.util.seconds
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

interface BossMovement {
    fun update(delta: Duration, boss: Boss1, transform: BossTransform)
}

class ParalyzedBossMovement : BossMovement {
    private var timer = 0.0.seconds

    override fun update(delta: Duration, boss: Boss1, transform: BossTransform) {
        timer += delta
        transform.targetRotation = 5.0.degrees * sin(timer.seconds * 2.0)
    }
}

class IdleBossMovement : BossMovement {
    private var timer = 0.0.seconds

    override fun update(delta: Duration, boss: Boss1, transform: BossTransform) {
        timer += delta
        transform.targetRotation = 10.0.degrees * sin(timer.seconds * 2.0)
        transform.targetX = 100.0f * cos(timer.seconds)
        transform.targetY = 100.0f * sin(timer.seconds)
    }
}

class SpinAttackBossMovement : BossMovement {
    private var timer = 0.0.seconds

    override fun update(delta: Duration, boss: Boss1, transform: BossTransform) {
        timer += delta
        transform.targetRotation = 180.0.degrees * timer.seconds
    }
}

class GrabAttackMovement(private val ship: PlayerShip) : BossMovement {
    private var timer = 0.0.seconds

    override fun update(delta: Duration, boss: Boss1, transform: BossTransform) {
        timer += delta
        transform.targetX = ship.x
        transform.targetY = ship.y + 200.0f // Above the ship
        transform.targetRotation = atan((transform.targetY - boss.y) / (transform.targetX - boss.x)).radians
    }
}

class FollowPlayerMovement(private val ship: PlayerShip) : BossMovement {
    override fun update(delta: Duration, boss: Boss1, transform: BossTransform) {
        transform.targetX = ship.x
        transform.targetY = ship.y
        transform.targetRotation = atan((transform.targetY - boss.y) / (transform.targetX - boss.x)).radians
    }
}
