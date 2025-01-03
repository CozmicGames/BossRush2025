package com.cozmicgames.states

interface SuspendableGameState: GameState {
    fun suspend()
    fun resumeFromSuspension()
}