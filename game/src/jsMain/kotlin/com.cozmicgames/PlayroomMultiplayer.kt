package com.cozmicgames

import com.cozmicgames.multiplayer.Multiplayer
import com.cozmicgames.multiplayer.PlayerState

class PlayroomMultiplayer : Multiplayer {
    override val isHost: Boolean
        get() = js("isHost()") as Boolean

    override fun onPlayerJoin(callback: (playerState: PlayerState) -> Unit) {
        val wrappedCallback = { wrappedState: dynamic ->
            callback(PlayroomPlayerState(wrappedState))
        }

        js("onPlayerJoin(wrappedCallback)")
    }

    override fun getMyPlayerState(): PlayerState {
        return PlayroomPlayerState(js("myPlayer()"))
    }
}