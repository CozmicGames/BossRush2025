package com.cozmicgames.bosses.boss1

import com.cozmicgames.Game
import com.littlekt.util.seconds
import kotlin.math.sqrt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

abstract class BossStage {
    abstract val nextStage: BossStage?

    abstract fun update(delta: Duration, controller: Boss1MovementController): BossStage
}

class TransitionStage(override val nextStage: BossStage) : BossStage() {
    private var timer = 0.0.seconds
    private var isFirstUpdate = true

    override fun update(delta: Duration, controller: Boss1MovementController): BossStage {
        if (isFirstUpdate) {
            controller.tentacleMovement = StretchOutTentacleMovement(0.3f)
            controller.bossMovement = ShakeBossMovement { 1.0f - timer.seconds / 2.0f }
            controller.beakMovement = ScreamBeakMovement()
        }

        timer += delta

        if (timer >= 2.0.seconds)
            return nextStage

        return this
    }
}

abstract class FightStage : BossStage() {
    protected class StageAttack(val attack: Attack, val probability: Float, val timeToNextAttack: Duration)

    protected abstract val stageAttacks: List<StageAttack>

    var nextBossMovementDecisionTime = 1.0.seconds
    private var nextAttackDecisionTime = 2.0.seconds

    protected abstract fun decideBossMovement(boss: Boss1, controller: Boss1MovementController)

    private fun decideAttack(boss: Boss1, controller: Boss1MovementController) {
        if (controller.isAttacking)
            return

        val probability = Game.random.nextFloat()
        val totalProbability = stageAttacks.sumOf { it.probability.toDouble() }.toFloat()

        var tries = 0
        while (tries++ < 10) {
            val attack = stageAttacks.random()
            val attackProbability = attack.probability / totalProbability

            if (probability < attackProbability) {
                controller.performAttack(attack.attack)
                nextAttackDecisionTime = attack.timeToNextAttack * boss.difficulty.bossAttackSpeedModifier.toDouble()
            }
        }
    }

    override fun update(delta: Duration, controller: Boss1MovementController): BossStage {
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

class FightStage1 : FightStage() {
    override val nextStage = TransitionStage(FightStage2())

    override val stageAttacks = listOf(
        StageAttack(StretchAttack(1.0.seconds), 0.7f, 1.5.seconds),
        StageAttack(DefendAttack(2.0.seconds), 0.5f, 2.0.seconds),
        StageAttack(FlyAttack(), 0.2f, 1.0.seconds)
    )

    override fun decideBossMovement(boss: Boss1, controller: Boss1MovementController) {
        if (controller.isAttacking)
            return

        val targetedPlayer = Game.players.players.random().ship

        val dx = targetedPlayer.x - boss.x
        val dy = targetedPlayer.y - boss.y
        val distance = sqrt(dx * dx + dy * dy)

        if (distance > 500.0f) {
            if (controller.bossMovement !is FollowPlayerBossMovement)
                controller.bossMovement = FollowPlayerBossMovement(targetedPlayer) {
                    nextBossMovementDecisionTime = 1.0.seconds
                }
        } else {
            if (Game.random.nextBoolean()) {
                controller.bossMovement = IdleBossMovement()
                nextBossMovementDecisionTime = 2.0.seconds
            } else {
                controller.bossMovement = AimBossMovement(targetedPlayer.x, targetedPlayer.y)
                nextBossMovementDecisionTime = 3.0.seconds
            }
        }
    }
}

class FightStage2 : FightStage() {
    override val nextStage = TransitionStage(FightStage3())

    override val stageAttacks = listOf(
        StageAttack(StretchAttack(1.0.seconds), 0.7f, 1.5.seconds),
        StageAttack(DefendAttack(2.0.seconds), 0.5f, 2.0.seconds),
        StageAttack(FlyAttack(), 0.3f, 1.0.seconds),
        StageAttack(ScreamAttack(), 0.6f, 4.0.seconds)
    )

    override fun decideBossMovement(boss: Boss1, controller: Boss1MovementController) {
        if (controller.isAttacking)
            return

        val targetedPlayer = Game.players.players.random().ship

        val dx = targetedPlayer.x - boss.x
        val dy = targetedPlayer.y - boss.y
        val distance = sqrt(dx * dx + dy * dy)

        if (distance > 500.0f) {
            if (controller.bossMovement !is FollowPlayerBossMovement)
                controller.bossMovement = FollowPlayerBossMovement(targetedPlayer) {
                    nextBossMovementDecisionTime = 1.0.seconds
                }
        } else {
            if (Game.random.nextFloat() > 0.7f) {
                controller.bossMovement = IdleBossMovement()
                nextBossMovementDecisionTime = 2.0.seconds
            } else {
                controller.bossMovement = AimBossMovement(targetedPlayer.x, targetedPlayer.y)
                nextBossMovementDecisionTime = 3.0.seconds
            }
        }
    }
}

class FightStage3 : FightStage() {
    override val nextStage = TransitionStage(EndStage())

    override val stageAttacks = listOf(
        StageAttack(StretchAttack(1.0.seconds), 0.5f, 1.5.seconds),
        StageAttack(DefendAttack(2.0.seconds), 0.4f, 2.0.seconds),
        StageAttack(FlyAttack(), 0.4f, 1.0.seconds),
        StageAttack(ScreamAttack(), 0.5f, 4.0.seconds),
        StageAttack(SpinAttack(), 0.2f, 4.0.seconds)
    )

    override fun decideBossMovement(boss: Boss1, controller: Boss1MovementController) {
        if (controller.isAttacking)
            return

        val targetedPlayer = Game.players.players.random().ship

        val dx = targetedPlayer.x - boss.x
        val dy = targetedPlayer.y - boss.y
        val distance = sqrt(dx * dx + dy * dy)

        if (distance > 500.0f) {
            if (controller.bossMovement !is FollowPlayerBossMovement)
                controller.bossMovement = FollowPlayerBossMovement(targetedPlayer) {
                    nextBossMovementDecisionTime = 1.0.seconds
                }
        } else {
            if (Game.random.nextFloat() > 0.8f) {
                controller.bossMovement = IdleBossMovement()
                nextBossMovementDecisionTime = 2.0.seconds
            } else {
                controller.bossMovement = AimBossMovement(targetedPlayer.x, targetedPlayer.y)
                nextBossMovementDecisionTime = 3.0.seconds
            }
        }
    }
}

class EndStage : BossStage() {
    override val nextStage: BossStage? = null

    override fun update(delta: Duration, controller: Boss1MovementController): BossStage {
        return this
    }
}
