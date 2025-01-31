package com.cozmicgames.bosses.boss4

import com.cozmicgames.Game
import com.cozmicgames.bosses.*
import com.cozmicgames.bosses.boss2.HitAttack
import com.littlekt.util.seconds
import kotlin.math.sqrt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class TransitionStage(override val nextStage: BossStage) : BossStage() {
    private var timer = 0.0.seconds
    private var isFirstUpdate = true

    override fun update(delta: Duration, controller: BossMovementController): BossStage {
        if (isFirstUpdate) {
            val movement = controller.movement as? Boss4Movement ?: throw IllegalStateException("Invalid movement type")
            movement.bossMovement = ShakeBossMovement { 1.0f - timer.seconds / 2.0f }
            movement.tailMovement = ShakeTailMovement { 1.0f - timer.seconds / 2.0f }
            movement.beakMovement = ScreamBeakMovement()
        }

        timer += delta

        if (timer >= 2.0.seconds) {
            controller.performAttack(TeleportAttack())
            return nextStage
        }

        return this
    }
}

abstract class Boss4FightStage : FightStage() {
    abstract val maxFollowDistance: Float
    private var isFirstAttack = true

    override fun decideBossMovement(boss: Boss, controller: BossMovementController) {
        val movement = controller.movement as? Boss4Movement ?: throw IllegalStateException("Invalid movement type")

        if (controller.isAttacking)
            return

        val target = Game.world.decideOnTarget() ?: return

        val dx = target.x - boss.x
        val dy = target.y - boss.y
        val distance = sqrt(dx * dx + dy * dy)

        if (distance > maxFollowDistance) {
            if (movement.bossMovement !is FollowPlayerBoss4BossMovement)
                movement.bossMovement = FollowPlayerBoss4BossMovement(target) {
                    nextBossMovementDecisionTime = 1.0.seconds
                }
        } else {
            if (Game.random.nextBoolean()) {
                movement.bossMovement = IdleBoss4BossMovement()
                nextBossMovementDecisionTime = 2.0.seconds
            } else {
                movement.bossMovement = AimBossMovement(target.x, target.y)
                nextBossMovementDecisionTime = 3.0.seconds
            }
        }
    }

    override fun decideAttack(boss: Boss, controller: BossMovementController) {
        if (controller.isAttacking)
            return

        val probability = Game.random.nextFloat()
        val totalProbability = stageAttacks.sumOf { it.probability.toDouble() }.toFloat()

        var tries = 0
        while (tries++ < 10) {
            val attack = stageAttacks.randomOrNull() ?: return

            if (isFirstAttack && "teleport" in attack.tags)
                continue

            val attackProbability = attack.probability / totalProbability

            if (probability < attackProbability) {
                isFirstAttack = false

                attack.performAttack(controller) {
                    nextAttackDecisionTime = attack.timeToNextAttack * boss.difficulty.bossAttackSpeedModifier.toDouble()
                }

                break
            }
        }
    }
}

class Boss4FightStage1 : Boss4FightStage() {
    override val nextStage = TransitionStage(Boss4FightStage2())

    override val maxFollowDistance = 600.0f

    override val stageAttacks = listOf(
        StageAttack(0.2f, 0.0.seconds) { controller, onDone -> controller.performAttack(CamouflageAttack(), onDone) },
        StageAttack(0.4f, 1.0.seconds) { controller, onDone -> controller.performAttack(PierceAttack(), onDone) },
        StageAttack(0.3f, 1.0.seconds) { controller, onDone -> controller.performAttack(FlyAttack(false), onDone) },
        StageAttack(0.5f, 2.0.seconds) { controller, onDone -> controller.performAttack(HuntingAttack(false, 7.0.seconds), onDone) }
    )
}

class Boss4FightStage2 : Boss4FightStage() {
    override val nextStage = TransitionStage(Boss4FightStage3())

    override val maxFollowDistance = 600.0f

    override val stageAttacks = listOf(
        StageAttack(0.2f, 0.0.seconds) { controller, onDone -> controller.performAttack(CamouflageAttack(), onDone) },
        StageAttack(0.4f, 1.0.seconds) { controller, onDone -> controller.performAttack(PierceAttack(), onDone) },
        StageAttack(0.4f, 1.0.seconds) { controller, onDone -> controller.performAttack(TeleportAttack(), onDone) },
        StageAttack(0.5f, 1.0.seconds) { controller, onDone -> controller.performAttack(FlyAttack(Game.random.nextBoolean()), onDone) },
        StageAttack(0.6f, 2.0.seconds) { controller, onDone -> controller.performAttack(HuntingAttack(false, 10.0.seconds), onDone) }
    )
}

class Boss4FightStage3 : Boss4FightStage() {
    override val nextStage = TransitionStage(EndStage())

    override val maxFollowDistance = 600.0f

    override val stageAttacks = listOf(
        StageAttack(0.3f, 0.0.seconds) { controller, onDone -> controller.performAttack(CamouflageAttack(), onDone) },
        StageAttack(0.4f, 1.0.seconds) { controller, onDone -> controller.performAttack(TeleportAttack(), onDone) },
        StageAttack(0.4f, 2.0.seconds) { controller, onDone -> controller.performAttack(HuntingAttack(true, 10.0.seconds), onDone) },
        StageAttack(0.3f, 1.0.seconds) { controller, onDone ->
            controller.performComboAttack(
                listOf(
                    PierceAttack(),
                    TeleportAttack()
                ), onDone
            )
        },

        StageAttack(0.3f, 2.0.seconds) { controller, onDone ->
            controller.performComboAttack(
                listOf(
                    TeleportAttack(),
                    HuntingAttack(false, 10.0.seconds)
                ), onDone
            )
        },

        StageAttack(0.5f, 3.0.seconds) { controller, onDone ->
            controller.performComboAttack(
                listOf(
                    TeleportAttack(),
                    FlyAttack(false)
                ), onDone
            )
        },
    )
}
