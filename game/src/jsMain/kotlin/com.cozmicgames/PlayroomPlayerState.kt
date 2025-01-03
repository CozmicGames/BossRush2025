package com.cozmicgames

import com.cozmicgames.multiplayer.PlayerState
import com.littlekt.graphics.Color
import com.littlekt.graphics.MutableColor

class PlayroomPlayerState(private val wrappedState: dynamic) : PlayerState {
    override val color: Color = MutableColor(1.0f, 1.0f, 1.0f, 1.0f)
        get() {
            val state = wrappedState
            val color = js("state.getProfile().color")
            with(field as MutableColor) {
                r = (js("color.r") as? Float) ?: 1.0f
                g = (js("color.g") as? Float) ?: 1.0f
                b = (js("color.b") as? Float) ?: 1.0f
            }
            return field
        }

    override fun <T : Any> getState(name: String): T? {
        val state = wrappedState
        return js("state.getState(name)") as? T
    }

    override fun <T : Any> setState(name: String, value: T) {
        val state = wrappedState
        js("state.setState(name, value)")
    }

    override fun onQuit(callback: () -> Unit) {
        val state = wrappedState
        js("state.onQuit(callback)")
    }
}