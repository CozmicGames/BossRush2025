package com.cozmicgames.states

import com.cozmicgames.utils.Difficulty
import kotlin.time.Duration

abstract class BaseBossFightState(val difficulty: Difficulty) : GameState {


    override fun render(delta: Duration): () -> GameState {
        return { this }
    }
}