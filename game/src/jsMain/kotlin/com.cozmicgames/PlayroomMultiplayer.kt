package com.cozmicgames

import com.cozmicgames.multiplayer.Multiplayer
import com.cozmicgames.multiplayer.PlayerState

class PlayroomMultiplayer : Multiplayer {
    override val isHost: Boolean
        get() = js("isHost()") as Boolean

    override val roomCode: String
        get() = js("getRoomCode()") as String

    override fun onPlayerJoin(callback: (playerState: PlayerState) -> Unit) {
        val wrappedCallback = { wrappedState: dynamic ->
            callback(PlayroomPlayerState(wrappedState))
        }

        js("onPlayerJoin(wrappedCallback)")
    }

    override fun getMyPlayerState(): PlayerState {
        return PlayroomPlayerState(js("myPlayer()"))
    }

    override fun <T : Any> getState(name: String): T? {
        return js("getState(name)") as? T
    }

    override fun <T : Any> setState(name: String, value: T) {
        js("setState(name, value)")
    }
}