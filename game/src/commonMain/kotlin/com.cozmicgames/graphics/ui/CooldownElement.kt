package com.cozmicgames.graphics.ui

import com.cozmicgames.graphics.Renderer
import com.littlekt.graphics.Color
import com.littlekt.resources.Textures
import kotlin.time.Duration

open class CooldownElement(private val color: Color, val isVertical: Boolean) : GUIElement() {
    companion object {
        private val BACKGROUND_COLOR = Color.fromHex("272727")
    }

    var currentValue = 1.0f

    override fun renderElement(delta: Duration, renderer: Renderer) {
        renderer.submit(layer) {
            it.draw(Textures.white, x, y, width = width, height = height, color = BACKGROUND_COLOR)

            if (isVertical)
                it.draw(Textures.white, x, y, width = width, height = height * currentValue, color = color)
            else
                it.draw(Textures.white, x, y, width = width * currentValue, height = height, color = color)
        }
    }
}