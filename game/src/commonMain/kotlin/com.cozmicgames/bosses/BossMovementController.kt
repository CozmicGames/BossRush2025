package com.cozmicgames.bosses

import com.littlekt.math.geom.shortDistanceTo
import com.littlekt.util.seconds
import kotlin.time.Duration

abstract class BossMovementController(val boss: Boss, startStage: BossStage) {
    abstract val movementSpeed: Float

    abstract val rotationSpeed: Float

    abstract val movement: Movement

    protected abstract val previousMovement: Movement

    val isAttacking get() = currentAttack != null

    private var currentAttack: Attack? = null
    private val transform = BossTransform()
    private var currentStage: BossStage = startStage

    fun performAttack(attack: Attack, onDone: () -> Unit = {}) {
        previousMovement.set(movement)
        attack.applyToMovement(movement)

        attack.onStart(boss)

        attack.afterAttack {
            movement.set(previousMovement)
            movement.resetAfterAttack(boss)
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
        movement.setToParalyzed(boss)
    }

    fun onHit() {
        cancelAttack(false)
        currentStage = currentStage.nextStage ?: EndStage()
    }

    open fun update(delta: Duration) {
        currentStage = currentStage.update(delta, this)

        if (currentAttack?.isDone(delta) == true)
            currentAttack = null

        movement.update(delta, boss, transform)

        val dx = transform.targetX - boss.x
        val dy = transform.targetY - boss.y
        val dr = boss.rotation.shortDistanceTo(transform.targetRotation)

        boss.x += dx * movementSpeed * delta.seconds * transform.moveSpeedModifier
        boss.y += dy * movementSpeed * delta.seconds * transform.moveSpeedModifier
        boss.rotation += dr * rotationSpeed * delta.seconds * transform.rotationSpeedModifier
    }

    fun onFailFight() {
        currentStage = EndStage()
        movement.setToFail(boss)
    }
}