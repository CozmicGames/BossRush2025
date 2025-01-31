package com.cozmicgames.bosses.boss2

import com.cozmicgames.Game
import com.cozmicgames.bosses.*
import com.littlekt.math.geom.degrees
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
            movement.bossMovement = CompoundBodyMovement(
                listOf(
                    ShakeBossMovement { 1.0f - timer.seconds / 2.0f },
                    SpinBossMovement()
                )
            )
            movement.bodyMovement = CurlBodyMovement(4.0.degrees, 0.4f)
            movement.shieldMovement = IdleShieldMovement()
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

        val target = Game.world.decideOnTarget() ?: return

        val dx = target.x - boss.x
        val dy = target.y - boss.y
        val distance = sqrt(dx * dx + dy * dy)

        if (distance > maxFollowDistance) {
            if (movement.bossMovement !is FollowPlayerBoss2BossMovement)
                movement.bossMovement = FollowPlayerBoss2BossMovement(target) {
                    nextBossMovementDecisionTime = 1.0.seconds
                }
        } else {
            if (Game.random.nextBoolean()) {
                movement.bossMovement = IdleBoss2BossMovement()
                nextBossMovementDecisionTime = 2.0.seconds
            } else {
                movement.bossMovement = AimBossMovement(target.x, target.y)
                nextBossMovementDecisionTime = 3.0.seconds
            }
        }
    }
}

class Boss2FightStage1 : Boss2FightStage() {
    override val nextStage = TransitionStage(Boss2FightStage2())

    override val maxFollowDistance = 600.0f

    override val stageAttacks = listOf(
        StageAttack(0.4f, 1.0.seconds) { controller, onDone -> controller.performAttack(HitAttack(), onDone) },
        StageAttack(0.5f, 2.0.seconds) { controller, onDone -> controller.performAttack(SpinAttack(), onDone) },
        StageAttack(0.6f, 3.0.seconds) { controller, onDone -> controller.performAttack(FlyAttack(), onDone) }
    )
}

class Boss2FightStage2 : Boss2FightStage() {
    override val nextStage = TransitionStage(Boss2FightStage3())

    override val maxFollowDistance = 500.0f

    override val stageAttacks = listOf(
        StageAttack(0.5f, 2.0.seconds) { controller, onDone -> controller.performAttack(SpinAttack(), onDone) },
        StageAttack(0.4f, 3.0.seconds) { controller, onDone -> controller.performAttack(FlyAttack(), onDone) },
        StageAttack(0.4f, 2.0.seconds) { controller, onDone -> controller.performAttack(PierceAttack(), onDone) },
        StageAttack(0.2f, 3.0.seconds) { controller, onDone -> controller.performAttack(ShootAttack(), onDone) },
        StageAttack(0.2f, 3.0.seconds) { controller, onDone -> controller.performAttack(BeamAttack(), onDone) },
    )
}

class Boss2FightStage3 : Boss2FightStage() {
    override val nextStage = TransitionStage(EndStage())

    override val maxFollowDistance = 400.0f

    override val stageAttacks = listOf(
        StageAttack(0.4f, 3.0.seconds) { controller, onDone -> controller.performAttack(FlyAttack(), onDone) },
        StageAttack(0.4f, 2.0.seconds) { controller, onDone -> controller.performAttack(PierceAttack(), onDone) },
        StageAttack(0.2f, 2.0.seconds) { controller, onDone -> controller.performAttack(ShootAttack(), onDone) },
        StageAttack(0.2f, 2.0.seconds) { controller, onDone -> controller.performAttack(BeamAttack(), onDone) },

        StageAttack(0.3f, 3.0.seconds) { controller, onDone ->
            controller.performComboAttack(
                listOf(
                    FlyAttack(),
                    PierceAttack()
                ), onDone
            )
        },

        StageAttack(0.4f, 2.0.seconds) { controller, onDone ->
            controller.performComboAttack(
                listOf(
                    SpinAttack(),
                    ShootAttack()
                ), onDone
            )
        },

        StageAttack(0.3f, 2.0.seconds) { controller, onDone ->
            controller.performComboAttack(
                listOf(
                    SpinAttack(),
                    BeamAttack()
                ), onDone
            )
        }
    )
}
