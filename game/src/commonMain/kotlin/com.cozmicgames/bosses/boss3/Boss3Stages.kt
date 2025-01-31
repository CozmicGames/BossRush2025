package com.cozmicgames.bosses.boss3

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
            val movement = controller.movement as? Boss3Movement ?: throw IllegalStateException("Invalid movement type")
            movement.bossMovement = ShakeBossMovement { 1.0f - timer.seconds / 2.0f }
            movement.beakMovement = ScreamBeakMovement()
        }

        timer += delta

        if (timer >= 2.0.seconds) {
            controller.performAttack(GrabAttack())
            return nextStage
        }

        return this
    }
}

abstract class Boss3FightStage : FightStage() {
    abstract val maxFollowDistance: Float

    override fun decideBossMovement(boss: Boss, controller: BossMovementController) {
        val movement = controller.movement as? Boss3Movement ?: throw IllegalStateException("Invalid movement type")

        if (controller.isAttacking)
            return

        val target = Game.world.decideOnTarget() ?: return

        val dx = target.x - boss.x
        val dy = target.y - boss.y
        val distance = sqrt(dx * dx + dy * dy)

        if (distance > maxFollowDistance) {
            if (movement.bossMovement !is FollowPlayerBoss3BossMovement)
                movement.bossMovement = FollowPlayerBoss3BossMovement(target) {
                    nextBossMovementDecisionTime = 1.0.seconds
                }
        } else {
            if (Game.random.nextBoolean()) {
                movement.bossMovement = IdleBoss3BossMovement()
                nextBossMovementDecisionTime = 2.0.seconds
            } else {
                movement.bossMovement = AimBossMovement(target.x, target.y)
                nextBossMovementDecisionTime = 3.0.seconds
            }
        }
    }
}

class Boss3FightStage1 : Boss3FightStage() {
    override val nextStage = TransitionStage(Boss3FightStage2())

    override val maxFollowDistance = 600.0f

    override val stageAttacks = listOf(
        StageAttack(0.3f, 1.5.seconds) { controller, onDone -> controller.performAttack(StretchAttack(), onDone) },
        StageAttack(0.2f, 3.0.seconds) { controller, onDone -> controller.performAttack(SpinAttack(), onDone) },
        StageAttack(0.4f, 5.0.seconds) { controller, onDone -> controller.performAttack(GrabAttack(), onDone) },
        StageAttack(0.3f, 2.0.seconds) { controller, onDone -> controller.performAttack(ShootAttack0(), onDone) },
        StageAttack(0.3f, 5.0.seconds) { controller, onDone -> controller.performAttack(GravityScreamAttack(), onDone) },
    )
}

class Boss3FightStage2 : Boss3FightStage() {
    override val nextStage = TransitionStage(Boss3FightStage3())

    override val maxFollowDistance = 500.0f

    override val stageAttacks = listOf(
        StageAttack(0.2f, 2.0.seconds) { controller, onDone -> controller.performAttack(SpinAttack(), onDone) },
        StageAttack(0.5f, 5.0.seconds) { controller, onDone -> controller.performAttack(GrabAttack(), onDone) },
        StageAttack(0.2f, 2.0.seconds) { controller, onDone -> controller.performAttack(ShootAttack1(), onDone) },
        StageAttack(0.1f, 4.0.seconds) { controller, onDone -> controller.performAttack(SpinShootAttack(), onDone) },
        StageAttack(0.3f, 5.0.seconds) { controller, onDone -> controller.performAttack(GravityScreamAttack(), onDone) },
    )
}

class Boss3FightStage3 : Boss3FightStage() {
    override val nextStage = TransitionStage(EndStage())

    override val maxFollowDistance = 500.0f

    override val stageAttacks = listOf(
        StageAttack(0.4f, 5.0.seconds) { controller, onDone -> controller.performAttack(GrabAttack(), onDone) },
        StageAttack(0.3f, 4.0.seconds) { controller, onDone -> controller.performAttack(SpinShootAttack(), onDone) },
        StageAttack(0.3f, 5.0.seconds) { controller, onDone -> controller.performAttack(GravityScreamAttack(), onDone) },

        StageAttack(0.5f, 5.0.seconds) { controller, onDone ->
            controller.performComboAttack(
                listOf(
                    GrabAttack(),
                    ShootAttack1()
                ),
                onDone
            )
        },

        StageAttack(0.4f, 5.0.seconds) { controller, onDone ->
            controller.performComboAttack(
                listOf(
                    GrabAttack(),
                    SpinShootAttack()
                ),
                onDone
            )
        },

        StageAttack(0.3f, 5.0.seconds) { controller, onDone ->
            controller.performComboAttack(
                listOf(
                    GravityScreamAttack(),
                    GrabAttack(),
                    ShootAttack1()
                ),
                onDone
            )
        },
    )
}
