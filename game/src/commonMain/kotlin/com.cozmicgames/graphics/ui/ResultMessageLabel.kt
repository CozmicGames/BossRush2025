package com.cozmicgames.graphics.ui

import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.elements.Label
import com.littlekt.util.seconds
import kotlin.math.sin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class ResultMessageLabel(text: String) : Label(text, 0.0f) {
    private var animationTime = 0.0.seconds
    private var isAnimationStarted = false

    fun startAnimation() {
        isAnimationStarted = true
    }

    override fun renderElement(delta: Duration, renderer: Renderer) {
        if (isAnimationStarted) {
            fontSize = 32.0f + sin(animationTime.seconds * 4.0f) * 2.0f
            animationTime += delta
        }

        super.renderElement(delta, renderer)
    }
}