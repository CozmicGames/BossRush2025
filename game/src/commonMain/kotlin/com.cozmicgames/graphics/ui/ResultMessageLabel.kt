package com.cozmicgames.graphics.ui

import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.elements.Label
import com.littlekt.util.seconds
import kotlin.math.sin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

open class ResultMessageLabel(text: String) : Label(text, 0.0f) {
    private var animationTime = 0.0.seconds
    private var isAnimationStarted = false

    init {
        shadowOffsetX = 2.0f
        shadowOffsetY = -2.0f
    }

    fun startAnimation() {
        isAnimationStarted = true
    }

    override fun renderElement(delta: Duration, renderer: Renderer) {
        if (isAnimationStarted) {
            fontSize = 40.0f + sin(animationTime.seconds * 4.0f) * 2.0f
            animationTime += delta
        }

        super.renderElement(delta, renderer)
    }
}