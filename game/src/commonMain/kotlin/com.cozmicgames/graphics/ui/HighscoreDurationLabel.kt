package com.cozmicgames.graphics.ui

import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.elements.Label
import com.littlekt.graphics.HAlign
import com.littlekt.graphics.VAlign
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

open class HighscoreDurationLabel(size: Float) : GUIElement() {
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

    var duration = 0.0.seconds

    private val label = object: Label(getTimeString(duration), size) {
        override var layer: Int
            get() = this@HighscoreDurationLabel.layer
            set(value) {}
    }

    init {
        label.getX = { x }
        label.getY = { y }
        label.getWidth = { width }
        label.getHeight = { height }
        label.hAlign = HAlign.CENTER
    }

    override fun renderElement(delta: Duration, renderer: Renderer) {
        label.text = getTimeString(duration)
        label.render(delta, renderer)
    }
}