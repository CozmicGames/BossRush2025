package com.cozmicgames.bosses.boss2

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
            val movement = controller.movement as? Boss2Movement ?: throw IllegalStateException("Invalid movement type")
            movement.bossMovement = ShakeBossMovement { 1.0f - timer.seconds / 2.0f }
        }

        timer += delta

        if (timer >= 2.0.seconds)
            return nextStage

        return this
    }
}

abstract class Boss2FightStage : FightStage() {
    abstract val maxFollowDistance: Float

    override fun decideBossMovement(boss: Boss, controller: BossMovementController) {
        val movement = controller.movement as? Boss2Movement ?: throw IllegalStateException("Invalid movement type")

        if (controller.isAttacking)
            return

        val targetedPlayer = Game.players.players.random().ship

        val dx = targetedPlayer.x - boss.x
        val dy = targetedPlayer.y - boss.y
        val distance = sqrt(dx * dx + dy * dy)

        if (distance > maxFollowDistance) {
            if (movement.bossMovement !is FollowPlayerBoss2BossMovement)
                movement.bossMovement = FollowPlayerBoss2BossMovement(targetedPlayer) {
                    nextBossMovementDecisionTime = 1.0.seconds
                }
        } else {
            if (Game.random.nextBoolean()) {
                movement.bossMovement = IdleBoss2BossMovement()
                nextBossMovementDecisionTime = 2.0.seconds
            } else {
                movement.bossMovement = AimBossMovement(targetedPlayer.x, targetedPlayer.y)
                nextBossMovementDecisionTime = 3.0.seconds
            }
        }
    }
}

class Boss2FightStage1 : Boss2FightStage() {
    override val nextStage = TransitionStage(EndStage())

    override val maxFollowDistance = 600.0f

    override val stageAttacks = emptyList<StageAttack>()
}