package com.cozmicgames.graphics.ui

import com.cozmicgames.Game
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.elements.Label
import com.littlekt.graphics.HAlign
import kotlin.time.Duration

open class CrewUI : GUIElement() {
    private val crewLabel = object : Label("Crew", 48.0f) {
        override var layer: Int
            get() = this@CrewUI.layer + 1
            set(value) {}
    }
    private val playerSlots: List<CrewPlayerSlot>

    init {
        crewLabel.getX = { Game.graphics.width - 380.0f + 360.0f * 0.5f }
        crewLabel.getY = { 180.0f }
        crewLabel.shadowOffsetX = 3.0f
        crewLabel.shadowOffsetY = -3.0f
        crewLabel.hAlign = HAlign.CENTER

        val numPlayers = Game.players.players.size
        val playerSlotSize = 70.0f
        val playerSlotSpacing = 8.0f

        val startX = Game.graphics.width - 380.0f + (360.0f - (numPlayers * playerSlotSize + (numPlayers - 1) * playerSlotSpacing)) * 0.5f
        val startY = 25.0f

        val playerSlots = arrayListOf<CrewPlayerSlot>()
        repeat(numPlayers) {
            val slot = object : CrewPlayerSlot(0) {
                override var layer: Int
                    get() = this@CrewUI.layer + 1
                    set(value) {}
            }
            slot.getX = { startX + it * (playerSlotSize + playerSlotSpacing) }
            slot.getY = { startY }
            slot.getWidth = { playerSlotSize }

            playerSlots += slot
        }
        this.playerSlots = playerSlots
    }

    override fun renderElement(delta: Duration, renderer: Renderer) {
        renderer.submit(layer) {
            Game.resources.crewBackgroundNinePatch.draw(it, Game.graphics.width - 380.0f, 0.0f, 360.0f, 220.0f)
        }

        playerSlots.forEach {
            it.render(delta, renderer)
        }

        crewLabel.render(delta, renderer)
    }
}