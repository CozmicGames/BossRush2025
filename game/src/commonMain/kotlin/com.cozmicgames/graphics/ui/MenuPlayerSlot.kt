package com.cozmicgames.graphics.ui

import com.cozmicgames.Game
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.elements.Label
import com.littlekt.graphics.Color
import com.littlekt.graphics.HAlign
import kotlin.time.Duration

open class MenuPlayerSlot(val index: Int) : GUIElement() {
    private val playerSlot = object : PlayerSlot(index) {
        override var layer: Int
            get() = this@MenuPlayerSlot.layer
            set(value) {}
    }

    private val notReadyLabel = object : Label("Not ready", 16.0f, Color.fromHex("f68187")) {
        override var layer: Int
            get() = this@MenuPlayerSlot.layer + 1
            set(value) {}
    }

    init {
        playerSlot.getX = { x }
        playerSlot.getY = { y + (width - 5.0f) * 0.5f + 5.0f }
        playerSlot.getWidth = { width }
        playerSlot.getHeight = { width }

        notReadyLabel.getX = { x + playerSlot.width * 0.5f }
        notReadyLabel.getY = { y + playerSlot.height + 10.0f }
        notReadyLabel.hAlign = HAlign.CENTER
    }

    override fun renderElement(delta: Duration, renderer: Renderer) {
        playerSlot.render(delta, renderer)

        if (Game.players.players.getOrNull(index)?.isReadyToStart == false)
            notReadyLabel.render(delta, renderer)

        if (index == 0) {
            renderer.submit(layer + 4) {
                it.draw(Game.resources.captain, x + (width - width * 0.6f) * 0.5f, y + width * 1.4f, width = width * 0.6f, height = width * 0.3f)
            }
        }
    }
}