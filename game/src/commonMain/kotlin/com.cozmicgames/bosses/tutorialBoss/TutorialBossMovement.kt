package com.cozmicgames.bosses.tutorialBoss

import com.cozmicgames.bosses.Boss
import com.cozmicgames.bosses.BossMovement
import com.cozmicgames.bosses.Movement
import com.cozmicgames.bosses.ParalyzedBossMovement

class TutorialBossMovement : Movement {
    override var bossMovement: BossMovement = IdleTutorialBossBossMovement()
    var bodyMovement: BodyMovement = IdleBodyMovement()
    var mouthMovement: MouthMovement = IdleMouthMovement()

    override fun set(movement: Movement) {
        movement as? TutorialBossMovement ?: throw IllegalStateException("Invalid movement type")
        bossMovement = movement.bossMovement
        bodyMovement = movement.bodyMovement
        mouthMovement = movement.mouthMovement
    }

    override fun setToParalyzed(boss: Boss) {
        bossMovement = ParalyzedBossMovement(boss.rotation)
        bodyMovement = ParalyzedBodyMovement()
        mouthMovement = ParalyzedMouthMovement()
    }

    override fun setToDead(boss: Boss) {
        bossMovement = ParalyzedBossMovement(boss.rotation)
        bodyMovement = ParalyzedBodyMovement()
        mouthMovement = ParalyzedMouthMovement()
    }

    override fun setToFail(boss: Boss) {
        bossMovement = IdleTutorialBossBossMovement()
        bodyMovement = IdleBodyMovement()
        mouthMovement = IdleMouthMovement()
    }
}