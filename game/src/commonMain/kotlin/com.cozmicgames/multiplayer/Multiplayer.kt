package com.cozmicgames.multiplayer

interface Multiplayer {
    val isHost: Boolean

    fun onPlayerJoin(callback: (playerState: PlayerState) -> Unit)

    fun getMyPlayerState(): PlayerState

    fun <T : Any> getState(name: String): T?

    fun <T : Any> setState(name: String, value: T)
}