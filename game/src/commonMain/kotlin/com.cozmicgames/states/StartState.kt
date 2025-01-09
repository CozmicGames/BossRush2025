package com.cozmicgames.states

import com.cozmicgames.states.boss1.Boss1State
import kotlin.time.Duration

class StartState : GameState {
    override fun render(delta: Duration): () -> GameState {
        return { Boss1State() }
    }
}