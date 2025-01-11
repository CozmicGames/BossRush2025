package com.cozmicgames.states.boss1

import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.shortDistanceTo
import com.littlekt.util.seconds
import kotlin.time.Duration

class MovementController(val boss: Boss1) {
    companion object {
        private const val MOVEMENT_SPEED = 0.5f
        private const val ROTATION_SPEED = 3.0f
    }

    var tentacleMovement: TentacleMovement = CompoundTentacleMovement()
    var beakMovement: BeakMovement = IdleBeakMovement()
    var bossMovement: BossMovement = IdleBossMovement()

    val isAttacking get() = currentAttack != null

    private var currentAttack: Attack? = null
    private val transform = BossTransform()
    private var currentStage: BossStage = FightStage1()

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

        attack.onStart(boss)

        attack.afterAttack {
            tentacleMovement = previousTentacleMovement
            beakMovement = previousBeakMovement
            bossMovement = previousBossMovement

            tentacleMovement.reset()

            onDone()
        }

        currentAttack = attack
    }

    fun cancelAttack(runAfterAttackListeners: Boolean) {
        currentAttack?.cancel(runAfterAttackListeners)
        currentAttack = null
    }

    fun onParalyze() {
        cancelAttack(false)

        bossMovement = ParalyzedBossMovement(boss.rotation)
        beakMovement = ParalyzedBeakMovement()
    }

    fun onHit() {
        cancelAttack(false)

        currentStage = currentStage.nextStage ?: EndStage()
    }

    fun update(delta: Duration) {
        currentStage = currentStage.update(delta, this)

        if (currentAttack?.isDone(delta) == true)
            currentAttack = null

        bossMovement.update(delta, boss, transform)

        val dx = transform.targetX - boss.x
        val dy = transform.targetY - boss.y
        val dr = boss.rotation.shortDistanceTo(transform.targetRotation)

        boss.x += dx * MOVEMENT_SPEED * delta.seconds * transform.moveSpeedModifier
        boss.y += dy * MOVEMENT_SPEED * delta.seconds * transform.moveSpeedModifier
        boss.rotation += dr * ROTATION_SPEED * delta.seconds * transform.rotationSpeedModifier
    }

    fun onFailFight() {
        currentStage = EndStage()

        tentacleMovement = CompoundTentacleMovement().also {
            it.addMovement(SwayTentacleMovement(15.0.degrees, 0.1f, 0.2f))
            it.addMovement(HangTentacleMovement())
            it.addMovement(WaveTentacleMovement(10.0.degrees, 0.3f, 0.2f))
        }
        beakMovement = IdleBeakMovement()
        bossMovement = IdleBossMovement()
    }
}