package com.cozmicgames.bosses.boss2

import com.cozmicgames.bosses.*

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

    override fun setToFail(boss: Boss) {
        bossMovement = IdleBoss2BossMovement()
        bodyMovement = IdleBodyMovement()
        shieldMovement = IdleShieldMovement()
    }

    override fun setToParalyzed(boss: Boss) {
        bossMovement = ParalyzedBossMovement(boss.rotation)
        bodyMovement = ParalyzedBodyMovement()
        shieldMovement = ParalyzedShieldMovement()
    }

    override fun setToDead(boss: Boss) {
        bossMovement = DeadBossMovement(boss.rotation)
        bodyMovement = DeadBodyMovement()
        shieldMovement = DeadShieldMovement()
    }
}