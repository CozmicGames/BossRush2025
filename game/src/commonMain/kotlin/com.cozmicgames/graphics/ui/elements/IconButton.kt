package com.cozmicgames.graphics.ui.elements

import com.cozmicgames.Game
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.GUIElement
import com.littlekt.graphics.Color
import com.littlekt.graphics.MutableColor
import com.littlekt.graphics.g2d.TextureSlice
import kotlin.time.Duration

open class IconButton(val texture: TextureSlice, val color: Color, var scale: Float = 1.0f, var onClick: () -> Unit = {}) : GUIElement() {
    var isEnabled = true

    private var wasHovered = false
    var isHovered = false
        private set

    override fun renderElement(delta: Duration, renderer: Renderer) {
        val minX = x
        val minY = y
        val maxX = x + width
        val maxY = y + height

        isHovered = isEnabled && Game.input.x.toFloat() in minX..maxX && (Game.graphics.height - Game.input.y - 1).toFloat() in minY..maxY
        val isClicked = isEnabled && Game.input.justTouched && isHovered

        if (!wasHovered && isHovered)
            Game.audio.hoverSound.play(0.1f)

        wasHovered = isHovered

        if (isClicked) {
            onClick()
            Game.audio.clickSound.play()
        }

        val ninePatch = when {
            isClicked -> Game.textures.buttonPressedNinePatch
            isHovered -> Game.textures.buttonHoveredNinePatch
            else -> Game.textures.buttonNormalNinePatch
        }

        val textureWidth = width * scale
        val textureHeight = height * scale

        renderer.submit(layer) {
            if (isEnabled)
                ninePatch.draw(it, x, y, width = width, height = height, color = color)
            else
                ninePatch.draw(it, x, y, width = width, height = height, color = MutableColor(color).mix(Color(0.9f, 0.9f, 0.9f, 0.5f), 0.5f))
            it.draw(texture, x + (width - textureWidth) * 0.5f, y + (height - textureHeight) * 0.5f, width = textureWidth, height = textureHeight)
        }
    }
}