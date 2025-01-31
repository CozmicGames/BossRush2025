package com.cozmicgames.bosses.boss1

import com.cozmicgames.Game
import com.cozmicgames.bosses.Boss
import com.cozmicgames.bosses.BossDesc
import com.cozmicgames.bosses.SpawnPosition
import com.cozmicgames.utils.Difficulty

class Boss1Desc : BossDesc {
    override val index = 0

    override val name = "Tentacula"

    override val reward = 300

    override val preview get() = Game.textures.boss1preview

    override val fullHealth get() = Boss1.FULL_HEALTH

    override val unlockedBossIndex = 1

    override val centerSpawnPosition = SpawnPosition.TOP_LEFT

    override fun createBoss(difficulty: Difficulty): Boss {
        return Boss1(difficulty)
    }
}