package com.cozmicgames.bosses.boss3

import com.cozmicgames.bosses.BossMovement
import com.cozmicgames.bosses.Movement

class Boss3Movement : Movement {
    override var bossMovement: BossMovement = IdleBoss3BossMovement()

    override fun set(movement: Movement) {
        movement as? Boss3Movement ?: throw IllegalStateException("Invalid movement type")
        bossMovement = movement.bossMovement
    }
}