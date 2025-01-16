package com.cozmicgames.states

import kotlin.time.Duration

class StartState : GameState {
    override fun render(delta: Duration): () -> GameState {
        return { BossSelectionState() }
    }
}