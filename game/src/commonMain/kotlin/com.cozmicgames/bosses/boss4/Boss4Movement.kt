package com.cozmicgames.bosses.boss4

import com.cozmicgames.bosses.*

class Boss4Movement : Movement {
    override var bossMovement: BossMovement = IdleBoss4BossMovement()
    var tailMovement: TailMovement = IdleTailMovement()
    var beakMovement: BeakMovement = IdleBeakMovement()

    override fun set(movement: Movement) {
        if (movement is Boss4Movement) {
            bossMovement = movement.bossMovement
            tailMovement = movement.tailMovement
            beakMovement = movement.beakMovement
        }
    }

    override fun resetAfterAttack(boss: Boss, attack: Attack) {
    }

    override fun setToParalyzed(boss: Boss) {
        bossMovement = ParalyzedBossMovement(boss.rotation)
        tailMovement = ParalyzedTailMovement()
        beakMovement = ParalyzedBeakMovement()
    }

    override fun setToFail(boss: Boss) {
        beakMovement = IdleBeakMovement()
        bossMovement = IdleBoss4BossMovement()
        tailMovement = IdleTailMovement()
    }

    override fun setToDead(boss: Boss) {
        beakMovement = OpenBeakMovement()
        bossMovement = DeadBossMovement(boss.rotation)
        tailMovement = DeadTailMovement()
    }
}