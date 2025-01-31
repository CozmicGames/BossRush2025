package com.cozmicgames.bosses.boss2

import com.cozmicgames.Game
import com.cozmicgames.bosses.Boss
import com.cozmicgames.bosses.BossDesc
import com.cozmicgames.bosses.SpawnPosition
import com.cozmicgames.utils.Difficulty

class Boss2Desc : BossDesc {
    override val index = 1

    override val name = "Nebulancer"

    override val reward = 300

    override val preview get() = Game.textures.boss2preview

    override val fullHealth get() = Boss2.FULL_HEALTH

    override val unlockedBossIndex = 2

    override val centerSpawnPosition = SpawnPosition.BOTTOM_LEFT

    override fun createBoss(difficulty: Difficulty): Boss {
        return Boss2(difficulty)
    }
}