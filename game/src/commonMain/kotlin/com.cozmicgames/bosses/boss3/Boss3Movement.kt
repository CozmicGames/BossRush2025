package com.cozmicgames.bosses.boss3

import com.cozmicgames.bosses.*

class Boss3Movement : Movement {
    override var bossMovement: BossMovement = IdleBoss3BossMovement()
    var beakMovement: BeakMovement = IdleBeakMovement()
    var legMovement: LegMovement = DefendLegMovement()
    var armMovement: ArmMovement = IdleArmMovement()

    override fun set(movement: Movement) {
        movement as? Boss3Movement ?: throw IllegalStateException("Invalid movement type")
        bossMovement = movement.bossMovement
        beakMovement = movement.beakMovement
        legMovement = movement.legMovement
        armMovement = movement.armMovement
    }

    override fun resetAfterAttack(boss: Boss, attack: Attack) {
        if (attack is ThrowAttack) {
            bossMovement = IdleBoss3BossMovement()
            beakMovement = IdleBeakMovement()
            legMovement = DefendLegMovement()
            armMovement = IdleArmMovement()
        }
    }

    override fun setToParalyzed(boss: Boss) {
        bossMovement = ParalyzedBossMovement(boss.rotation)
        beakMovement = ParalyzedBeakMovement()
    }
}