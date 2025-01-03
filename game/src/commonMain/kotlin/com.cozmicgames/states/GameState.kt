package com.cozmicgames.states

import kotlin.time.Duration

interface GameState {
    fun begin() {}
    fun resize(width: Int, height: Int) {}
    fun render(delta: Duration): () -> GameState
    fun end() {}
}
