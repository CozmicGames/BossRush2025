package com.cozmicgames.bosses.boss4

import com.cozmicgames.Constants
import com.cozmicgames.Game
import com.cozmicgames.bosses.Boss
import com.cozmicgames.bosses.BossDesc
import com.cozmicgames.bosses.SpawnPosition
import com.cozmicgames.utils.Difficulty

class Boss4Desc : BossDesc {
    override val index = 3

    override val name = "Voidray"

    override val reward = 500

    override val preview get() = Game.textures.boss4preview

    override val fullHealth get() = Boss4.FULL_HEALTH

    override val unlockedBossIndex = Constants.FINAL_FIGHT_INDEX

    override val centerSpawnPosition = SpawnPosition.TOP_RIGHT

    override fun createBoss(difficulty: Difficulty): Boss {
        return Boss4(difficulty)
    }
}