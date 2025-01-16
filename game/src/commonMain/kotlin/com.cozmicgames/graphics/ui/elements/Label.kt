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

open class Label(text: String, size: Float = Game.resources.font.fontSize, private val color: Color = Color.WHITE) : GUIElement() {
    open var alwaysUpdateLayoutAndCache = true

    var text = text
        set(value) {
            field = value
            updateLayoutAndCache()
        }

    var fontSize = size
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

    var shadowOffsetX = 0.0f
        set(value) {
            field = value
            updateLayoutAndCache()
        }

    var shadowOffsetY = 0.0f
        set(value) {
            field = value
            updateLayoutAndCache()
        }

    var shadowColor = Color.BLACK
        set(value) {
            field = value
            updateLayoutAndCache()
        }

    private val layout = GlyphLayout()
    private val cache = BitmapFontCache(Game.resources.font)
    private val shadowLayout = GlyphLayout()
    private val shadowCache = BitmapFontCache(Game.resources.font)
    private var textX = 0.0f
    private var textY = 0.0f

    init {
        updateLayoutAndCache()
    }

    override fun updateTransform() {
        updateLayoutAndCache()
    }

    private fun updateLayoutAndCache() {
        if (fontSize == 0.0f)
            return

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

        cache.setText(layout, textX, textY, scaleX = fontSize / Game.resources.font.fontSize, scaleY = fontSize / Game.resources.font.fontSize, color = color)

        if (shadowOffsetX != 0.0f || shadowOffsetY != 0.0f) {
            shadowLayout.setText(Game.resources.font, text, scaleX = fontSize / Game.resources.font.fontSize, scaleY = fontSize / Game.resources.font.fontSize)
            shadowCache.setText(shadowLayout, textX + shadowOffsetX, textY + shadowOffsetY, scaleX = fontSize / Game.resources.font.fontSize, scaleY = fontSize / Game.resources.font.fontSize, color = shadowColor)
        }
    }

    override fun renderElement(delta: Duration, renderer: Renderer) {
        if (alwaysUpdateLayoutAndCache)
            updateLayoutAndCache()

        if (fontSize == 0.0f)
            return

        renderer.submit(layer) {
            if (shadowOffsetX != 0.0f || shadowOffsetY != 0.0f)
                shadowCache.draw(it)

            cache.draw(it)
        }
    }
}