package com.cozmicgames.graphics

import com.cozmicgames.Game
import com.littlekt.graphics.Color
import com.littlekt.math.clamp
import com.littlekt.resources.Textures
import kotlin.math.sqrt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class Transition(fromOpenToClose: Boolean) {
    companion object {
        private val DURATION = 0.7.seconds
        private val COLOR = Color.fromHex("131313")
    }

    private var direction = if (fromOpenToClose) -1.0f else 1.0f
    private var amount = if (fromOpenToClose) 1.0f else 0.0f

    private var timer = 0.0.seconds
    private var isStarted = false
    private var callback: () -> Unit = {}

    fun start(callback: () -> Unit) {
        isStarted = true
        this.callback = callback
    }

    fun render(delta: Duration, renderer: Renderer) {
        if (isStarted) {
            timer += delta

            amount = if (direction > 0.0f)
                (timer / DURATION).toFloat().clamp(0.0f, 1.0f)
            else
                1.0f - (timer / DURATION).toFloat().clamp(0.0f, 1.0f)

            if ((direction > 0.0f && amount >= 1.0f) || (direction < 0.0f && amount <= 0.0f))
                callback()
        }

        renderer.submit(RenderLayers.TRANSITION) {
            val centerScale = amount * sqrt(2.0f) * 1.05f

            val centerWidth = Game.graphics.width * centerScale
            val centerHeight = Game.graphics.height * centerScale

            val centerX = (Game.graphics.width - centerWidth) * 0.5f
            val centerY = (Game.graphics.height - centerHeight) * 0.5f

            if (centerScale > 0.0f)
                it.draw(Game.textures.transition, centerX - 1.0f, centerY - 1.0f, width = centerWidth + 2.0f, height = centerHeight + 2.0f, color = COLOR)

            val leftRightWidth = (Game.graphics.width - centerWidth) * 0.5f

            if (leftRightWidth > 0.0f) {
                val sideY = (Game.graphics.height - centerHeight) * 0.5f
                val leftX = 0.0f
                val rightX = Game.graphics.width - leftRightWidth
                val leftRightHeight = centerHeight

                it.draw(Textures.white, leftX - 1.0f, sideY - 1.0f, width = leftRightWidth + 2.0f, height = leftRightHeight + 2.0f, color = COLOR)
                it.draw(Textures.white, rightX - 1.0f, sideY - 1.0f, width = leftRightWidth + 2.0f, height = leftRightHeight + 2.0f, color = COLOR)
            }

            val topBottomHeight = (Game.graphics.height - centerHeight) * 0.5f

            if (topBottomHeight > 0.0f) {
                val topY = 0.0f
                val bottomY = Game.graphics.height - topBottomHeight
                val topBottomWidth = Game.graphics.width.toFloat()

                it.draw(Textures.white, -1.0f, topY - 1.0f, width = topBottomWidth + 2.0f, height = topBottomHeight + 2.0f, color = COLOR)
                it.draw(Textures.white, -1.0f, bottomY - 1.0f, width = topBottomWidth + 2.0f, height = topBottomHeight + 2.0f, color = COLOR)
            }
        }
    }
}