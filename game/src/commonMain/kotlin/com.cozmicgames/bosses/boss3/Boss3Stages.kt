package com.cozmicgames.bosses.boss3

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
            val movement = controller.movement as? Boss3Movement ?: throw IllegalStateException("Invalid movement type")
            movement.bossMovement = ShakeBossMovement { 1.0f - timer.seconds / 2.0f }
            movement.beakMovement = ScreamBeakMovement()
        }

        timer += delta

        if (timer >= 2.0.seconds)
            return nextStage

        return this
    }
}

abstract class Boss3FightStage : FightStage() {
    abstract val maxFollowDistance: Float

    override fun decideBossMovement(boss: Boss, controller: BossMovementController) {
        val movement = controller.movement as? Boss3Movement ?: throw IllegalStateException("Invalid movement type")

        if (controller.isAttacking)
            return

        val targetedPlayer = Game.players.players.random().ship

        val dx = targetedPlayer.x - boss.x
        val dy = targetedPlayer.y - boss.y
        val distance = sqrt(dx * dx + dy * dy)

        if (distance > maxFollowDistance) {
            if (movement.bossMovement !is FollowPlayerBoss3BossMovement)
                movement.bossMovement = FollowPlayerBoss3BossMovement(targetedPlayer) {
                    nextBossMovementDecisionTime = 1.0.seconds
                }
        } else {
            if (Game.random.nextBoolean()) {
                movement.bossMovement = IdleBoss3BossMovement()
                nextBossMovementDecisionTime = 2.0.seconds
            } else {
                movement.bossMovement = AimBossMovement(targetedPlayer.x, targetedPlayer.y)
                nextBossMovementDecisionTime = 3.0.seconds
            }
        }
    }
}

class Boss3FightStage1 : Boss3FightStage() {
    override val nextStage = TransitionStage(Boss3FightStage2())

    override val maxFollowDistance = 600.0f

    override val stageAttacks = listOf(
        StageAttack(0.3f, 1.5.seconds) { StretchAttack() },
        StageAttack(0.2f, 3.0.seconds) { SpinAttack() },
        StageAttack(0.4f, 8.0.seconds) { GrabAttack() },
        StageAttack(0.3f, 6.0.seconds) { ShootAttack0() },
    )
}

class Boss3FightStage2 : Boss3FightStage() {
    override val nextStage = TransitionStage(Boss3FightStage3())

    override val maxFollowDistance = 500.0f

    override val stageAttacks = listOf(
        StageAttack(0.2f, 3.0.seconds) { SpinAttack() },
        StageAttack(0.4f, 8.0.seconds) { GrabAttack() },
        StageAttack(0.3f, 6.0.seconds) { ShootAttack0() },
        StageAttack(0.2f, 6.0.seconds) { ShootAttack1() },
        StageAttack(0.3f, 5.0.seconds) { SpinShootAttack0() },
    )
}

class Boss3FightStage3 : Boss3FightStage() {
    override val nextStage = TransitionStage(Boss3FightStage4())

    override val maxFollowDistance = 500.0f

    override val stageAttacks = listOf(
        StageAttack(0.4f, 8.0.seconds) { GrabAttack() },
        StageAttack(0.2f, 6.0.seconds) { ShootAttack1() },
        StageAttack(0.15f, 6.0.seconds) { ShootAttack2() },
        StageAttack(0.3f, 5.0.seconds) { SpinShootAttack0() },
        StageAttack(0.15f, 5.0.seconds) { SpinShootAttack1() },
    )
}

class Boss3FightStage4 : Boss3FightStage() {
    override val nextStage = TransitionStage(EndStage())

    override val maxFollowDistance = 500.0f

    override val stageAttacks = listOf(
        StageAttack(0.4f, 8.0.seconds) { GrabAttack() },
        StageAttack(0.1f, 6.0.seconds) { ShootAttack1() },
        StageAttack(0.3f, 6.0.seconds) { ShootAttack2() },
        StageAttack(0.1f, 5.0.seconds) { SpinShootAttack1() },
        StageAttack(0.3f, 5.0.seconds) { SpinShootAttack2() },
    )
}