package com.cozmicgames.graphics.ui

import com.cozmicgames.Game
import com.cozmicgames.graphics.RenderLayers
import com.cozmicgames.graphics.Renderer
import com.littlekt.graphics.MutableColor
import kotlin.time.Duration

class BorderIndicator : GUIElement() {
    val color = MutableColor(0.0f, 0.0f, 0.0f, 0.0f)

    override fun renderElement(delta: Duration, renderer: Renderer) {
        if (color.a > 0.0f)
            renderer.submit(RenderLayers.BORDER_INDICATOR) {
                Game.resources.borderIndicatorNinePatch.draw(it, 0.0f, 0.0f, Game.graphics.width.toFloat(), Game.graphics.height.toFloat(), color = color)
            }
    }
}