package com.cozmicgames.states

import kotlin.time.Duration

class MenuState : GameState {
    private var returnState: GameState = this

    override fun render(delta: Duration): () -> GameState {
        return { returnState }
    }
}