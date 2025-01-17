package com.cozmicgames.graphics.ui.elements

import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.GUIElement
import com.littlekt.graphics.Color
import com.littlekt.graphics.g2d.NinePatch
import kotlin.time.Duration

open class NinepatchImage(var ninePatch: NinePatch, val color: Color = Color.WHITE) : GUIElement() {
    override fun renderElement(delta: Duration, renderer: Renderer) {
        renderer.submit(layer) {
            ninePatch.draw(it, x, y, width, height, color = color)
        }
    }
}