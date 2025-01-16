package com.cozmicgames.bosses.boss1

import com.cozmicgames.Game
import com.cozmicgames.bosses.Boss
import com.cozmicgames.bosses.BossDesc
import com.cozmicgames.utils.Difficulty

class Boss1Desc : BossDesc {
    override val name = "Tentacula"

    override val reward = 300

    override val preview = Game.resources.boss1preview

    override val fullHealth get() = Boss1.FULL_HEALTH

    override fun createBoss(difficulty: Difficulty): Boss {
        return Boss1(difficulty)
    }
}