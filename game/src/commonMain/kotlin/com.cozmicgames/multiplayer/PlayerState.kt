package com.cozmicgames.multiplayer

interface PlayerState {
    val id: String

    fun <T : Any> getState(name: String): T?

    fun <T : Any> setState(name: String, value: T?)

    fun onQuit(callback: () -> Unit)
}