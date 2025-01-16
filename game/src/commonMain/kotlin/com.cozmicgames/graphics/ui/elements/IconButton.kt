package com.cozmicgames.graphics.ui.elements

import com.cozmicgames.Game
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.GUIElement
import com.littlekt.graphics.Color
import com.littlekt.graphics.Texture
import kotlin.time.Duration

open class IconButton(val texture: Texture, val color: Color, var scale: Float = 1.0f, var onClick: () -> Unit = {}) : GUIElement() {
    var isEnabled = true

    override fun renderElement(delta: Duration, renderer: Renderer) {
        val minX = x
        val minY = y
        val maxX = x + width
        val maxY = y + height

        val isHovered = isEnabled && Game.input.x.toFloat() in minX..maxX && (Game.graphics.height - Game.input.y - 1).toFloat() in minY..maxY
        val isClicked = isEnabled && Game.input.justTouched && isHovered

        if (isClicked)
            onClick()

        val ninePatch = when {
            isClicked -> Game.resources.buttonPressedNinePatch
            isHovered -> Game.resources.buttonHoveredNinePatch
            else -> Game.resources.buttonNormalNinePatch
        }

        val textureWidth = width * scale
        val textureHeight = height * scale

        renderer.submit(layer) {
            ninePatch.draw(it, x, y, width = width, height = height, color = color)
            it.draw(texture, x + (width - textureWidth) * 0.5f, y + (height - textureHeight) * 0.5f, width = textureWidth, height = textureHeight)
        }
    }
}