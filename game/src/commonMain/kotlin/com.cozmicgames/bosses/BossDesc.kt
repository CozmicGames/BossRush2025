package com.cozmicgames.bosses

import com.cozmicgames.states.BossFightState
import com.cozmicgames.utils.Difficulty
import com.littlekt.graphics.Texture

interface BossDesc {
    val name: String
    val reward: Int
    val preview: Texture
    val fullHealth: Int

    fun createFightGameState(difficulty: Difficulty): BossFightState = BossFightState(this, difficulty)

    fun createBoss(difficulty: Difficulty): Boss
}