package com.cozmicgames.bosses.boss1

import com.cozmicgames.Game
import com.cozmicgames.bosses.*
import com.littlekt.util.seconds
import kotlin.math.sqrt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class TransitionStage(override val nextStage: BossStage) : BossStage() {
    private var timer = 0.0.seconds
    private var isFirstUpdate = true

    override fun update(delta: Duration, controller: BossMovementController): BossStage {
        if (isFirstUpdate) {
            val movement = controller.movement as? Boss1Movement ?: throw IllegalStateException("Invalid movement type")
            movement.tentacleMovement = StretchOutTentacleMovement(0.3f)
            movement.bossMovement = ShakeBossMovement { 1.0f - timer.seconds / 2.0f }
            movement.beakMovement = ScreamBeakMovement()
        }

        timer += delta

        if (timer >= 2.0.seconds)
            return nextStage

        return this
    }
}

abstract class Boss1FightStage : FightStage() {
    abstract val maxFollowDistance: Float

    override fun decideBossMovement(boss: Boss, controller: BossMovementController) {
        val movement = controller.movement as? Boss1Movement ?: throw IllegalStateException("Invalid movement type")

        if (controller.isAttacking)
            return

        val target = Game.world.decideOnTarget() ?: return

        val dx = target.x - boss.x
        val dy = target.y - boss.y
        val distance = sqrt(dx * dx + dy * dy)

        if (distance > maxFollowDistance) {
            if (movement.bossMovement !is FollowPlayerBoss1BossMovement)
                movement.bossMovement = FollowPlayerBoss1BossMovement(target) {
                    nextBossMovementDecisionTime = 1.0.seconds
                }
        } else {
            if (Game.random.nextBoolean()) {
                movement.bossMovement = IdleBoss1BossMovement()
                nextBossMovementDecisionTime = 2.0.seconds
            } else {
                movement.bossMovement = AimBossMovement(target.x, target.y)
                nextBossMovementDecisionTime = 3.0.seconds
            }
        }
    }
}

class Boss1FightStage1 : Boss1FightStage() {
    override val nextStage = TransitionStage(Boss1FightStage2())

    override val maxFollowDistance = 600.0f

    override val stageAttacks = listOf(
        StageAttack(0.7f, 1.5.seconds) { StretchAttack(1.0.seconds) },
        StageAttack(0.5f, 2.0.seconds) { DefendAttack(2.0.seconds) },
        StageAttack(0.2f, 1.0.seconds) { FlyAttack() }
    )
}

class Boss1FightStage2 : Boss1FightStage() {
    override val nextStage = TransitionStage(Boss1FightStage3())

    override val maxFollowDistance = 500.0f

    override val stageAttacks = listOf(
        StageAttack(0.7f, 1.5.seconds) { StretchAttack(1.0.seconds) },
        StageAttack(0.5f, 2.0.seconds) { DefendAttack(2.0.seconds) },
        StageAttack(0.3f, 1.0.seconds) { FlyAttack() },
        StageAttack(0.6f, 4.0.seconds) { ScreamAttack() }
    )
}

class Boss1FightStage3 : Boss1FightStage() {
    override val nextStage = TransitionStage(EndStage())

    override val maxFollowDistance = 400.0f

    override val stageAttacks = listOf(
        StageAttack(0.5f, 1.5.seconds) { StretchAttack(1.0.seconds) },
        StageAttack(0.4f, 2.0.seconds) { DefendAttack(2.0.seconds) },
        StageAttack(0.4f, 1.0.seconds) { FlyAttack() },
        StageAttack(0.5f, 4.0.seconds) { ScreamAttack() },
        StageAttack(0.2f, 4.0.seconds) { SpinAttack() }
    )
}
