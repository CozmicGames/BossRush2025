package com.cozmicgames.graphics.ui.elements

import com.cozmicgames.Game
import com.cozmicgames.graphics.RenderLayers
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.GUIElement
import com.littlekt.graphics.Color
import com.littlekt.graphics.HAlign
import com.littlekt.graphics.VAlign
import com.littlekt.resources.Textures
import kotlin.time.Duration

class Tooltip(text: String) : GUIElement() {
    companion object {
        private val BACKGROUND_COLOR = Color(0.3f, 0.3f, 0.3f, 0.7f)
    }

    override var layer: Int
        get() = RenderLayers.UI + 5000
        set(value) {}

    private var label = object : Label(text, 18.0f) {
        override var layer: Int
            get() = this@Tooltip.layer + 1
            set(value) {}
    }

    private val toRight get() = x + width <= Game.graphics.width
    private val newLineCount = text.count { it == '\n' }

    init {
        getWidth = { label.textWidth + 20.0f }
        getHeight = { (label.textHeight * newLineCount) + 20.0f }

        getX = { Game.input.x.toFloat() }
        getY = { Game.graphics.height - Game.input.y - 1.0f }

        label.getX = { if (toRight) x + width * 0.5f else x - width * 0.5f }
        label.getY = { if (newLineCount == 0) y + height * 0.5f else y + newLineCount * label.textHeight }
        label.hAlign = HAlign.CENTER
        label.vAlign = VAlign.CENTER
    }

    override fun renderElement(delta: Duration, renderer: Renderer) {
        renderer.submit(layer) {
            val x = if (toRight)
                x
            else
                x - width

            it.draw(Textures.white, x, y, width = width, height = height, color = BACKGROUND_COLOR)
        }

        label.render(delta, renderer)

        com.littlekt.graph.node.ui.Label
    }
}