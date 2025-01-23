package com.cozmicgames.bosses.boss3

import com.cozmicgames.Game
import com.cozmicgames.bosses.Boss
import com.cozmicgames.bosses.BossDesc
import com.cozmicgames.utils.Difficulty

class Boss3Desc : BossDesc {
    override val name = "Gravicrab"

    override val reward = 400

    override val preview = Game.resources.boss3preview

    override val fullHealth get() = Boss3.FULL_HEALTH

    override val unlockedBossIndex = 3

    override fun createBoss(difficulty: Difficulty): Boss {
        return Boss3(difficulty)
    }
}