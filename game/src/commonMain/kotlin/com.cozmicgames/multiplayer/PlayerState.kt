package com.cozmicgames.multiplayer

import com.littlekt.graphics.Color

interface PlayerState {
    val color: Color

    fun <T : Any> getState(name: String): T?

    fun <T : Any> setState(name: String, value: T)

    fun onQuit(callback: () -> Unit)
}