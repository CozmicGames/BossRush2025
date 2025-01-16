package com.cozmicgames.graphics.ui.elements

import com.cozmicgames.Game
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.GUIElement
import com.littlekt.graphics.Color
import com.littlekt.graphics.HAlign
import com.littlekt.graphics.VAlign
import com.littlekt.graphics.g2d.font.BitmapFontCache
import com.littlekt.graphics.g2d.font.GlyphLayout
import kotlin.time.Duration

open class TextButton(text: String, val color: Color, fontSize: Float = Game.resources.font.fontSize, private val fontColor: Color = Color.WHITE, var onClick: () -> Unit = {}) : GUIElement() {
    open var alwaysUpdateLayoutAndCache = true

    var text = text
        set(value) {
            field = value
            updateLayoutAndCache()
        }

    var fontSize = fontSize
        set(value) {
            field = value
            updateLayoutAndCache()
        }

    var hAlign = HAlign.CENTER
        set(value) {
            field = value
            updateLayoutAndCache()
        }

    var vAlign = VAlign.CENTER
        set(value) {
            field = value
            updateLayoutAndCache()
        }

    private val layout = GlyphLayout()
    private val cache = BitmapFontCache(Game.resources.font)
    private var textX = 0.0f
    private var textY = 0.0f

    init {
        updateLayoutAndCache()
    }

    override fun updateTransform() {
        updateLayoutAndCache()
    }

    private fun updateLayoutAndCache() {
        layout.setText(Game.resources.font, text, scaleX = fontSize / Game.resources.font.fontSize, scaleY = fontSize / Game.resources.font.fontSize)

        textX = when (hAlign) {
            HAlign.LEFT -> this.x
            HAlign.CENTER -> this.x + (width - layout.width) * 0.5f
            HAlign.RIGHT -> this.x + width - layout.width
        }

        textY = when (vAlign) {
            VAlign.BOTTOM -> this.y
            VAlign.CENTER -> this.y + (height - layout.height) * 0.5f
            VAlign.TOP -> this.y + height - layout.height
        } + layout.height * 0.2f

        cache.setText(layout, textX, textY, scaleX = fontSize / Game.resources.font.fontSize, scaleY = fontSize / Game.resources.font.fontSize, color = fontColor)
    }

    override fun renderElement(delta: Duration, renderer: Renderer) {
        if (alwaysUpdateLayoutAndCache)
            updateLayoutAndCache()

        val minX = x
        val minY = y
        val maxX = x + width
        val maxY = y + height

        val isHovered = Game.input.x.toFloat() in minX..maxX && (Game.graphics.height - Game.input.y - 1).toFloat() in minY..maxY
        val isClicked = Game.input.justTouched && isHovered

        if (isClicked)
            onClick()

        val ninePatch = when {
            isClicked -> Game.resources.buttonPressedNinePatch
            isHovered -> Game.resources.buttonHoveredNinePatch
            else -> Game.resources.buttonNormalNinePatch
        }

        renderer.submit(layer) {
            ninePatch.draw(it, x, y, width = width, height = height, color = color)
            cache.draw(it)
        }
    }
}