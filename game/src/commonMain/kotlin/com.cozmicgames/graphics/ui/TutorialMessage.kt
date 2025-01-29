package com.cozmicgames.graphics.ui

import com.cozmicgames.Game
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.elements.Label
import com.littlekt.graphics.HAlign
import com.littlekt.graphics.MutableColor
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

abstract class TutorialMessage(message: String) : GUIElement() {
    private val color = MutableColor(1.0f, 1.0f, 1.0f, 0.8f)
    private val shadowColor = MutableColor(0.0f, 0.0f, 0.0f, 0.5f)
    private var fadeOutTime = 0.0.seconds
    private var fadeOutTimer = 0.0.seconds
    private var fadeOutCallback: () -> Unit = {}
    private var isFadingOut = false
    private val label = Label(message, 32.0f, color)

    private var fadeInTime = 0.0.seconds
    private var fadeInTimer = 0.0.seconds
    private var isFadingIn = false
    private var isFadedIn = false

    init {
        label.getX = { Game.graphics.width * 0.5f }
        label.getY = { Game.graphics.height - 100.0f }
        label.shadowColor = shadowColor
        label.shadowOffsetX = 2.0f
        label.shadowOffsetY = 2.0f
        label.hAlign = HAlign.CENTER
    }

    override fun renderElement(delta: Duration, renderer: Renderer) {
        if (isFadingIn) {
            val factor = 1.0f - (fadeInTimer / fadeInTime).toFloat()
            color.a = 0.8f * factor
            shadowColor.a = 0.5f * factor

            if (fadeInTimer <= 0.0.seconds) {
                fadeInTime = 0.0.seconds
                isFadedIn = true
                isFadingIn = false
            }

            fadeInTimer -= delta
        } else if (isFadedIn) {
            if (shouldFadeOut() && !isFadingOut) {
                fadeOutTime = 1.0.seconds
                fadeOutTimer = 1.0.seconds
                fadeOutCallback = { onFadeOut() }
                isFadingOut = true
            }

            if (isFadingOut) {
                val factor = (fadeOutTimer / fadeOutTime).toFloat()
                color.a = 0.8f * factor
                shadowColor.a = 0.5f * factor

                if (fadeOutTimer <= 0.0.seconds) {
                    fadeOutCallback()
                    fadeOutCallback = {}
                    fadeOutTime = 0.0.seconds
                }

                fadeOutTimer -= delta
            }
        }

        label.render(delta, renderer)
    }

    fun fadeIn() {
        fadeInTime = 0.5.seconds
        fadeInTimer = 0.5.seconds
        isFadingIn = true
    }

    abstract fun shouldFadeOut(): Boolean

    abstract fun onFadeOut()
}