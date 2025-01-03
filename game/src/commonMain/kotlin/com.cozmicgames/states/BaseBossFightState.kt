package com.cozmicgames.states

import kotlin.time.Duration

class BaseBossFightState : GameState {
    override fun render(delta: Duration): () -> GameState {


        return { this }
    }
}