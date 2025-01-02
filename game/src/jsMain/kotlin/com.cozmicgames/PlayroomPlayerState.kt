package com.cozmicgames

import com.cozmicgames.multiplayer.PlayerState
import com.littlekt.graphics.Color
import com.littlekt.graphics.MutableColor

class PlayroomPlayerState(private val wrappedState: dynamic) : PlayerState {
    override val color: Color = MutableColor(1.0f, 1.0f, 1.0f, 1.0f)
        get() {
            val color = wrappedState.color
            with(field as MutableColor) {
                r = color.r as Float
                g = color.g as Float
                b = color.b as Float
            }
            return field
        }

    override fun <T : Any> getState(name: String): T? {
        return wrappedState.getState(name) as? T
    }

    override fun <T : Any> setState(name: String, value: T) {
        wrappedState.setState(name, value)
    }

    override fun onQuit(callback: () -> Unit) {
        wrappedState.onQuit(callback)
    }
}