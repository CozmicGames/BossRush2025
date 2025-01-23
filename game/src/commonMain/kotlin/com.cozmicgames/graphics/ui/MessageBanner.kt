package com.cozmicgames.graphics.ui

import com.cozmicgames.Game
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.elements.Image
import com.cozmicgames.graphics.ui.elements.Label
import com.littlekt.util.seconds
import kotlin.time.Duration

class MessageBanner : GUIElement() {
    companion object {
        private val MESSAGES = arrayOf(
            "Don’t forget your towel for a space trip!",
            "Looks kinda fishy!",
            "How much is the fish?",
            "Do you feel hooked?",
            "Whole worlds spin on acts of imagination.",
            "Just keep fishing, just keep fishing!",
            "So long, and thanks for all the fish.",
            "Plenty of fish in space.",
            "Space fish are trophies, not food.",
            "Holy space mackerel!",
            "May the fish be with you.",
            "Captain Fish Hook reporting for duty!",
            "Fishy business ahead!",
            "Fishy fishy fishy fish!",
            "In the beginning, there were fish, now there are space fish!",
            "You spin me right round, space fish right round!"
        )
    }

    private val background = Image(Game.resources.messageBannerBackground)
    private val messageLabel = Label("Welcome to the bay!", 32.0f)
    private var messageX = 0.0f
    private val possibleMessages = arrayListOf<String>()

    init {
        background.getX = { x }
        background.getY = { y }
        background.getWidth = { width }
        background.getHeight = { height }

        messageLabel.shadowOffsetX = 4.0f
        messageLabel.shadowOffsetY = -4.0f
        messageLabel.getX = { messageX }
        messageLabel.getY = { y + (height - messageLabel.height) * 0.5f }
        messageLabel.getWidth = { width }
        messageLabel.getHeight = { height * 0.8f }
    }

    override fun renderElement(delta: Duration, renderer: Renderer) {
        messageX -= delta.seconds * 100.0f

        if (messageX < -width * 1.2f) {
            if (possibleMessages.isEmpty())
                possibleMessages.addAll(MESSAGES)

            messageX = width
            messageLabel.text = possibleMessages.removeAt(possibleMessages.indices.random())
        }

        background.render(delta, renderer)
        renderer.pushScissor(x, y, width, height)
        messageLabel.render(delta, renderer)
        renderer.popScissor()
    }
}