package com.cozmicgames.graphics.ui

import com.cozmicgames.graphics.Renderer
import com.littlekt.graphics.Color
import com.littlekt.resources.Textures
import kotlin.time.Duration

open class CooldownElement(val isVertical: Boolean = true) : GUIElement() {
    companion object {
        private val BACKGROUND_COLOR = Color.fromHex("272727")
        private val FOREGROUND_COLOR = Color.fromHex("94fdff")
    }

    var currentValue = 1.0f

    override fun renderElement(delta: Duration, renderer: Renderer) {
        renderer.submit(layer) {
            it.draw(Textures.white, x, y, width = width, height = height, color = BACKGROUND_COLOR)

            if (isVertical)
                it.draw(Textures.white, x, y, width = width, height = height * currentValue, color = FOREGROUND_COLOR)
            else
                it.draw(Textures.white, x, y, width = width * currentValue, height = height, color = FOREGROUND_COLOR)
        }
    }
}