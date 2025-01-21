package com.cozmicgames.bosses.boss2

import com.cozmicgames.Game
import com.cozmicgames.bosses.Boss
import com.cozmicgames.bosses.BossDesc
import com.cozmicgames.utils.Difficulty

class Boss2Desc : BossDesc {
    override val name = "Nebulancer"

    override val reward = 400

    override val preview = Game.resources.boss1preview //TODO: Change to boss2preview

    override val fullHealth get() = Boss2.FULL_HEALTH

    override val unlockedBossIndex = 2

    override fun createBoss(difficulty: Difficulty): Boss {
        return Boss2(difficulty)
    }
}