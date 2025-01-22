package com.cozmicgames.bosses.boss3

import com.cozmicgames.bosses.BossMovement
import com.cozmicgames.bosses.Movement

class Boss3Movement : Movement {
    override var bossMovement: BossMovement = IdleBoss3BossMovement()
    var beakMovement: BeakMovement = IdleBeakMovement()
    var legMovement: LegMovement = IdleLegMovement()
    var armMovement: ArmMovement = IdleArmMovement()

    override fun set(movement: Movement) {
        movement as? Boss3Movement ?: throw IllegalStateException("Invalid movement type")
        bossMovement = movement.bossMovement
        beakMovement = movement.beakMovement
        legMovement = movement.legMovement
        armMovement = movement.armMovement
    }
}