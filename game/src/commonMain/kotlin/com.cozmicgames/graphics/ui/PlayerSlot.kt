package com.cozmicgames.graphics.ui

import com.cozmicgames.Game
import com.cozmicgames.graphics.Renderer
import com.littlekt.graphics.Color
import kotlin.time.Duration

open class PlayerSlot(val index: Int) : GUIElement() {
    companion object {
        private val EMPTY_COLOR = Color(1.0f, 1.0f, 1.0f, 0.7f)
    }

    override fun renderElement(delta: Duration, renderer: Renderer) {
        renderer.submit(layer) {
            val player = Game.players.players.getOrNull(index)

            if (player == null)
                it.draw(Game.resources.playerSlotEmpty, x, y, width = width, height = height, color = EMPTY_COLOR)
            else {
                it.draw(Game.resources.playerSlotBackground, x, y, width = width, height = height)
                it.draw(Game.resources.playerSlot, x, y, width = width, height = height, color = player.color)

                val avatarTexture = player.avatarTexture
                if (avatarTexture != null)
                    it.draw(avatarTexture, x + 10.0f, y + 10.0f, width = width - 20.0f, height = height - 20.0f)
            }
        }
    }
}