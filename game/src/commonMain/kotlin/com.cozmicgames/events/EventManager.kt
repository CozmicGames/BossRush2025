package com.cozmicgames.events

import com.cozmicgames.Game
import com.cozmicgames.multiplayer.PlayerState

class EventManager {
    private val eventsToSend = arrayListOf<String>()
    private val eventsToProcess = arrayListOf<String>()

    fun addSendEvent(desc: String) {
        eventsToSend += desc
    }

    fun sendEvents() {
        val state = Game.players.getMyPlayerState()
        state.setState("sendEventCount", eventsToSend.size)

        for (i in eventsToSend.indices)
            state.setState("sendEvent$i", eventsToSend[i])

        eventsToSend.clear()
    }

    fun addProcessEvent(desc: String) {
        if (!Game.players.isHost)
            return

        eventsToProcess += desc
    }

    fun sendProcessEvents(state: PlayerState) {
        if (!Game.players.isHost)
            return

        state.setState("processEventCount", eventsToProcess.size)

        for (i in eventsToProcess.indices)
            state.setState("processEvent$i", eventsToProcess[i])
    }

    fun clearProcessEvents() {
        if (!Game.players.isHost)
            return

        eventsToProcess.clear()
    }

    fun processEvents() {
        val state = Game.players.getMyPlayerState()
        val count = state.getState("processEventCount") ?: 0

        for (i in 0 until count) {
            val event = state.getState<String>("processEvent$i") ?: continue
            Events.process(event)
        }
    }
}