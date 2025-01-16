package com.cozmicgames.graphics.ui

import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.elements.Label
import com.littlekt.graphics.HAlign
import com.littlekt.graphics.VAlign
import kotlin.time.Duration

class DurationLabel(duration: Duration) : GUIElement() {
    companion object {
        private fun getTimeString(duration: Duration): String {
            return buildString {
                if (duration.inWholeHours > 0)
                    append("${duration.inWholeHours}:")

                if (duration.inWholeMinutes > 0)
                    append("${duration.inWholeMinutes % 60}:")

                append("${duration.inWholeSeconds % 60},")

                if (duration.inWholeMilliseconds % 1000 < 10)
                    append("00${duration.inWholeMilliseconds % 1000}")
                else if (duration.inWholeMilliseconds % 1000 < 100)
                    append("0${duration.inWholeMilliseconds % 1000}")
                else
                    append("${duration.inWholeMilliseconds % 1000}")
            }
        }
    }

    private val label = Label("Duration", 40.0f)
    private val result = Label(getTimeString(duration), 40.0f)

    init {
        label.getX = { x }
        label.getY = { y }
        label.getWidth = { width }
        label.getHeight = { height }
        label.hAlign = HAlign.LEFT
        label.vAlign = VAlign.CENTER

        result.getX = { x }
        result.getY = { y }
        result.getWidth = { width }
        result.getHeight = { height }
        result.hAlign = HAlign.RIGHT
        result.vAlign = VAlign.CENTER
    }

    override fun renderElement(delta: Duration, renderer: Renderer) {
        label.render(delta, renderer)
        result.render(delta, renderer)
    }
}