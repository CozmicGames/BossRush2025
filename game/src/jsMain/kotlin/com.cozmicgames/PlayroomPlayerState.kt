package com.cozmicgames

import com.cozmicgames.multiplayer.PlayerState
import com.littlekt.graphics.Color
import com.littlekt.graphics.MutableColor

class PlayroomPlayerState(private val wrappedState: dynamic) : PlayerState {
    override val id: String
        get() {
            val state = wrappedState
            return js("state.id") as String
        }

    override fun <T : Any> getState(name: String): T? {
        val state = wrappedState
        return js("state.getState(name)") as? T
    }

    override fun <T : Any> setState(name: String, value: T?) {
        val state = wrappedState
        js("state.setState(name, value)")
    }

    override fun onQuit(callback: () -> Unit) {
        val state = wrappedState
        js("state.onQuit(callback)")
    }
}