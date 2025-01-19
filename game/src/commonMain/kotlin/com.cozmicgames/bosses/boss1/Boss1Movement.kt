package com.cozmicgames.bosses.boss1

import com.cozmicgames.bosses.Boss
import com.cozmicgames.bosses.BossMovement
import com.cozmicgames.bosses.Movement
import com.cozmicgames.bosses.ParalyzedBossMovement

class Boss1Movement : Movement {
    override var bossMovement: BossMovement = IdleBoss1BossMovement()
    var tentacleMovement: TentacleMovement = IdleTentacleMovement()
    var beakMovement: BeakMovement = IdleBeakMovement()

    override fun set(movement: Movement) {
        if (movement is Boss1Movement) {
            bossMovement = movement.bossMovement
            tentacleMovement = movement.tentacleMovement
            beakMovement = movement.beakMovement
        }
    }

    override fun resetAfterAttack(boss: Boss) {
        tentacleMovement.reset()
    }

    override fun setToParalyzed(boss: Boss) {
        bossMovement = ParalyzedBossMovement(boss.rotation)
        beakMovement = ParalyzedBeakMovement()
    }

    override fun setToFail(boss: Boss) {
        tentacleMovement = IdleTentacleMovement()
        beakMovement = IdleBeakMovement()
        bossMovement = IdleBoss1BossMovement()
    }
}