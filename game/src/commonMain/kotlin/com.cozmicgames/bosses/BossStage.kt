package com.cozmicgames.bosses

import com.cozmicgames.Constants
import com.cozmicgames.Game
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

abstract class BossStage {
    abstract val nextStage: BossStage?

    abstract fun update(delta: Duration, controller: BossMovementController): BossStage
}

abstract class FightStage : BossStage() {
    protected class StageAttack(val probability: Float, val timeToNextAttack: Duration, vararg val tags: String, val performAttack: (controller: BossMovementController, onDone: () -> Unit) -> Unit)

    protected abstract val stageAttacks: List<StageAttack>

    var nextBossMovementDecisionTime = 1.0.seconds
    var nextAttackDecisionTime = 2.0.seconds

    protected abstract fun decideBossMovement(boss: Boss, controller: BossMovementController)

    protected open fun decideAttack(boss: Boss, controller: BossMovementController) {
        if (controller.isAttacking)
            return

        val probability = Game.random.nextFloat()
        val totalProbability = stageAttacks.sumOf { it.probability.toDouble() }.toFloat()

        var tries = 0
        while (tries++ < 10) {
            val attack = stageAttacks.randomOrNull() ?: return
            val attackProbability = attack.probability / totalProbability

            if (probability > attackProbability) {
                attack.performAttack(controller) {
                    nextAttackDecisionTime = attack.timeToNextAttack * boss.difficulty.bossAttackSpeedModifier.toDouble()
                }

                break
            }
        }
    }

    override fun update(delta: Duration, controller: BossMovementController): BossStage {
        if (controller.boss.isParalyzed)
            return this

        nextBossMovementDecisionTime -= delta
        nextAttackDecisionTime -= delta

        if (nextBossMovementDecisionTime <= 0.0.seconds)
            decideBossMovement(controller.boss, controller)

        if (nextAttackDecisionTime <= 0.0.seconds)
            decideAttack(controller.boss, controller)

        return this
    }
}

class EndStage : BossStage() {
    override val nextStage: BossStage? = null

    private var isFirstUpdate = true

    override fun update(delta: Duration, controller: BossMovementController): BossStage {
        if (isFirstUpdate) {
            if (controller.boss.isDead)
                if (Game.player.currentFightIndex != Constants.FINAL_FIGHT_INDEX)
                    controller.movement.setToParalyzed(controller.boss)
                else
                    controller.movement.setToDead(controller.boss)
            else
                controller.movement.setToFail(controller.boss)
            isFirstUpdate = false
        }

        return this
    }
}
