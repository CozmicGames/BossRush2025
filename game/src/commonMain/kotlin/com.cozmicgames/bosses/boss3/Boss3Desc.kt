package com.cozmicgames.bosses.boss3

import com.cozmicgames.Game
import com.cozmicgames.bosses.Boss
import com.cozmicgames.bosses.BossDesc
import com.cozmicgames.bosses.SpawnPosition
import com.cozmicgames.utils.Difficulty

class Boss3Desc : BossDesc {
    override val index = 2

    override val name = "Gravicrab"

    override val reward = 400

    override val preview get() = Game.textures.boss3preview

    override val fullHealth get() = Boss3.FULL_HEALTH

    override val unlockedBossIndex = 3

    override val centerSpawnPosition = SpawnPosition.BOTTOM_RIGHT

    override fun createBoss(difficulty: Difficulty): Boss {
        return Boss3(difficulty)
    }
}