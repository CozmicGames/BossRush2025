package com.cozmicgames.graphics.ui

import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.elements.Label
import com.littlekt.graphics.HAlign
import com.littlekt.graphics.VAlign
import kotlin.time.Duration

class ResultLabel(labelText: String, resultText: String) : GUIElement() {
    private val label = Label(labelText, 40.0f)
    private val result = Label(resultText, 40.0f)

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