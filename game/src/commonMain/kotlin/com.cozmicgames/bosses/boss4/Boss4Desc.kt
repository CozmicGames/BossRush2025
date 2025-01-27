package com.cozmicgames.bosses.boss4

import com.cozmicgames.Game
import com.cozmicgames.bosses.Boss
import com.cozmicgames.bosses.BossDesc
import com.cozmicgames.bosses.SpawnPosition
import com.cozmicgames.utils.Difficulty

class Boss4Desc : BossDesc {
    override val name = "Voidray"

    override val reward = 600

    override val preview = Game.resources.boss4preview

    override val fullHealth get() = Boss4.FULL_HEALTH

    override val unlockedBossIndex = 4

    override val centerSpawnPosition = SpawnPosition.TOP_RIGHT

    override fun createBoss(difficulty: Difficulty): Boss {
        return Boss4(difficulty)
    }
}