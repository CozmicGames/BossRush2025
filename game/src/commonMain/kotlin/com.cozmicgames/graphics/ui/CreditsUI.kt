package com.cozmicgames.graphics.ui

import com.cozmicgames.Game
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.elements.Label
import com.cozmicgames.graphics.ui.elements.TextButton
import com.littlekt.graphics.Color
import com.littlekt.graphics.MutableColor
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class CreditsUI(onFreePlayPressed: () -> Unit) : GUIElement() {
    private val color = MutableColor()
    private var fadeInAmount = 0.0f
    private var fadeInTimer = 0.0.seconds
    private var fadeInStarted = false

    private val upperMessageLabel = Label("The monsters are gone!", 60.0f)
    private val lowerMessageLabel = Label("But who knows what else lurks out there?", 40.0f)
    private val thanksLabel = Label("Thank you for playing!", 32.0f)
    private val creditsLabel = Label("Made by cozmicgames for Boss Rush Jam 2025", 26.0f)
    private val freePlayButton = TextButton("Free Play", Color.fromHex("33984b"), fontSize = 28.0f) {
        onFreePlayPressed()
    }

    init {
        upperMessageLabel.getX = { Game.graphics.width * 0.5f }
        upperMessageLabel.getY = { 460.0f }
        upperMessageLabel.shadowOffsetX = 3.0f
        upperMessageLabel.shadowOffsetY = -3.0f

        lowerMessageLabel.getX = { Game.graphics.width * 0.5f }
        lowerMessageLabel.getY = { 400.0f }
        lowerMessageLabel.shadowOffsetX = 3.0f
        lowerMessageLabel.shadowOffsetY = -3.0f

        thanksLabel.getX = { Game.graphics.width * 0.5f }
        thanksLabel.getY = { 190.0f }
        thanksLabel.shadowOffsetX = 2.0f
        thanksLabel.shadowOffsetY = -2.0f

        creditsLabel.getX = { Game.graphics.width * 0.5f }
        creditsLabel.getY = { 150.0f }
        creditsLabel.shadowOffsetX = 2.0f
        creditsLabel.shadowOffsetY = -2.0f

        freePlayButton.getX = { Game.graphics.width * 0.5f - freePlayButton.width * 0.5f }
        freePlayButton.getY = { 50.0f }
        freePlayButton.getWidth = { 300.0f }
        freePlayButton.getHeight = { 50.0f }
    }

    fun fadeIn() {
        fadeInTimer = 3.0.seconds
        fadeInStarted = true
    }

    override fun renderElement(delta: Duration, renderer: Renderer) {
        if (fadeInStarted) {
            fadeInAmount = 1.0f - (fadeInTimer / 3.0.seconds).toFloat()
            color.set(1.0f, 1.0f, 1.0f, fadeInAmount)

            fadeInTimer -= delta
        }

        upperMessageLabel.render(delta, renderer)
        lowerMessageLabel.render(delta, renderer)
        thanksLabel.render(delta, renderer)
        creditsLabel.render(delta, renderer)
        freePlayButton.render(delta, renderer)
    }
}