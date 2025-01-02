package com.cozmicgames.multiplayer

interface Multiplayer {
    val isHost: Boolean

    fun onPlayerJoin(callback: (playerState: PlayerState) -> Unit)

    fun getMyPlayerState(): PlayerState
}