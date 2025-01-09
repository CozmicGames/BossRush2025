package com.cozmicgames.states.boss1

import com.littlekt.math.geom.degrees
import com.littlekt.util.seconds
import kotlin.time.Duration

class MovementController(private val boss: Boss1) {
    companion object {
        private const val MOVEMENT_SPEED = 0.5f
        private const val ROTATION_SPEED = 3.0f
    }

    var tentacleMovement: TentacleMovement = CompoundTentacleMovement()
    var beakMovement: BeakMovement = IdleBeakMovement()
    var bossMovement: BossMovement = IdleBossMovement()

    private var currentAttack: Attack? = null

    private val transform = BossTransform()

    init {
        with(tentacleMovement as CompoundTentacleMovement) {
            addMovement(SwayTentacleMovement(15.0.degrees, 0.1f, 0.2f))
            addMovement(HangTentacleMovement())
            addMovement(WaveTentacleMovement(10.0.degrees, 0.3f, 0.2f))
        }
    }

    fun performAttack(attack: Attack, onDone: () -> Unit = {}) {
        val previousTentacleMovement = tentacleMovement
        val previousBeakMovement = beakMovement
        val previousBossMovement = bossMovement

        tentacleMovement = attack.tentacleMovement
        beakMovement = attack.beakMovement
        bossMovement = attack.bossMovement

        attack.afterAttack {
            tentacleMovement = previousTentacleMovement
            beakMovement = previousBeakMovement
            bossMovement = previousBossMovement
            onDone()
        }

        currentAttack = attack
    }

    fun update(delta: Duration) {
        if (currentAttack?.isDone(delta) == true)
            currentAttack = null

        bossMovement.update(delta, boss, transform)

        val dx = transform.targetX - boss.x
        val dy = transform.targetY - boss.y
        val dr = transform.targetRotation - boss.rotation

        boss.x += dx * MOVEMENT_SPEED * delta.seconds
        boss.y += dy * MOVEMENT_SPEED * delta.seconds
        boss.rotation += dr * ROTATION_SPEED * delta.seconds
    }
}