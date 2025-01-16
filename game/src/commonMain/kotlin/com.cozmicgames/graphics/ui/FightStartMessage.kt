package com.cozmicgames.graphics.ui

import com.cozmicgames.Game
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.elements.Label
import com.cozmicgames.utils.Easing
import com.littlekt.graphics.Color
import com.littlekt.graphics.HAlign
import com.littlekt.graphics.MutableColor
import com.littlekt.graphics.VAlign
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class FightStartMessage : GUIElement() {
    companion object {
        private val DURATION_0 = 1.5.seconds
        private val DURATION_1 = 0.4.seconds
        private val DURATION_2 = 0.8.seconds
        private val DURATION_3 = 0.3.seconds
    }

    private val label = Label("Ready?", 0.1f, Color(1.0f, 1.0f, 1.0f, 0.7f))
    private var timer = 0.0.seconds
    private var onFinish: () -> Unit = {}
    private var isAnimationStarted = false
    private var isAnimationFinished = false

    init {
        label.getX = { 0.0f }
        label.getY = { 0.0f }
        label.getWidth = { Game.graphics.width.toFloat() }
        label.getHeight = { Game.graphics.height.toFloat() }
        label.hAlign = HAlign.CENTER
        label.vAlign = VAlign.CENTER
    }

    fun startAnimation(onFinish: () -> Unit) {
        if (!isAnimationStarted) {
            isAnimationStarted = true
            this.onFinish = onFinish
        }
    }

    override fun renderElement(delta: Duration, renderer: Renderer) {
        if (isAnimationStarted && !isAnimationFinished) {
            timer += delta

            if (timer < DURATION_0) {
                val factor = (timer / DURATION_0).toFloat()
                label.fontSize = 128.0f * Easing.QUAD_IN(factor)
            }

            if (timer >= DURATION_0 && timer < DURATION_0 + DURATION_1) {
                label.fontSize = 0.0f
                label.text = "Catch!"
            }

            if (timer >= DURATION_1 && timer < DURATION_0 + DURATION_1 + DURATION_2) {
                val factor = ((timer - (DURATION_1 + DURATION_0)) / DURATION_2).toFloat()
                label.fontSize = 128.0f * Easing.QUAD_IN(factor)
            }

            if (timer > DURATION_0 + DURATION_1 + DURATION_2 + DURATION_3) {
                onFinish()
                isAnimationFinished = true
            }
        }

        if (label.fontSize > 0.0f)
            label.render(delta, renderer)
    }
}