package com.cozmicgames.bosses.boss2

import com.cozmicgames.bosses.BossMovement
import com.cozmicgames.bosses.Movement

class Boss2Movement : Movement {
    override var bossMovement: BossMovement = IdleBoss2BossMovement()
    var bodyMovement: BodyMovement = IdleBodyMovement()
    var shieldMovement: ShieldMovement = IdleShieldMovement()

    override fun set(movement: Movement) {
        movement as? Boss2Movement ?: throw IllegalStateException("Invalid movement type")
        bossMovement = movement.bossMovement
        bodyMovement = movement.bodyMovement
        shieldMovement = movement.shieldMovement
    }
}