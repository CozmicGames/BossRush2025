package com.cozmicgames.graphics.ui

import com.cozmicgames.Game
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.elements.Label
import com.littlekt.graphics.Color
import com.littlekt.graphics.HAlign
import kotlin.time.Duration

open class CurrencyLabel(val getAmount: () -> Int, val size: Float) : GUIElement() {
    constructor(amount: Int, size: Float) : this({ amount }, size)

    private val label = object : Label("${getAmount()}", size * 1.5f) {
        override var layer: Int
            get() = this@CurrencyLabel.layer
            set(value) {}
    }

    init {
        label.getX = { x }
        label.getY = { y + size * 0.5f }
        label.shadowOffsetX = 1.0f
        label.shadowOffsetY = -1.0f
        label.shadowOffsetX = 1.0f
        label.shadowOffsetY = -1.0f
        label.hAlign = HAlign.RIGHT

        getWidth = { (label.text.length + 0.5f) * size }
        getHeight = { size }
    }

    override fun renderElement(delta: Duration, renderer: Renderer) {
        label.text = "${getAmount()}"

        label.render(delta, renderer)

        renderer.submit(layer) {
            it.draw(Game.resources.currencyIcon, x + size * 0.45f + 1.5f, y - 1.5f, width = size, height = size, color = Color.BLACK)
            it.draw(Game.resources.currencyIcon, x + size * 0.45f, y, width = size, height = size)
        }
    }
}