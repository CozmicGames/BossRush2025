package com.cozmicgames.graphics.ui

import com.cozmicgames.Game
import com.cozmicgames.graphics.RenderLayers
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.elements.Divider
import com.cozmicgames.graphics.ui.elements.Label
import com.cozmicgames.utils.Difficulty
import com.cozmicgames.utils.lerp
import com.littlekt.math.clamp
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class FinalFightFailedUI : GUIElement() {
    companion object {
        private val SLIDE_TIME = 0.7.seconds
    }

    enum class ResultState {
        NONE,
        RETRY_EASY,
        RETRY_NORMAL,
        RETRY_HARD,
        RETURN
    }

    private var slideInAmount = 0.0f
    private var startSlideInAmount = 0.0f
    private var targetSlideInAmount = 0.0f
    private var timer = 0.0.seconds
    private var slideCallback: () -> Unit = {}

    var resultState = ResultState.NONE
        private set

    private val titleLabel = object : Label("You're dead!", 72.0f) {
        override var layer: Int
            get() = RenderLayers.UI + 1000
            set(value) {}
    }
    private val divider = object : Divider() {
        override var layer: Int
            get() = RenderLayers.UI + 1000
            set(value) {}
    }

    private val returnLabel = object : Label("Return", 32.0f) {
        override var layer: Int
            get() = RenderLayers.UI + 1000
            set(value) {}
    }
    private val retryLabel = object : Label("Retry", 32.0f) {
        override var layer: Int
            get() = RenderLayers.UI + 1000
            set(value) {}
    }
    private val returnButton = object : ReturnButton({
        resultState = ResultState.RETURN
    }) {
        override var layer: Int
            get() = RenderLayers.UI + 1000
            set(value) {}
    }
    private val playEasyButton = object : PlayButton(Difficulty.EASY, {
        resultState = ResultState.RETRY_EASY
    }) {
        override var layer: Int
            get() = RenderLayers.UI + 1000
            set(value) {}
    }
    private val playNormalButton = object : PlayButton(Difficulty.NORMAL, {
        resultState = ResultState.RETRY_NORMAL
    }) {
        override var layer: Int
            get() = RenderLayers.UI + 1000
            set(value) {}
    }
    private val playHardButton = object : PlayButton(Difficulty.HARD, {
        resultState = ResultState.RETRY_HARD
    }) {
        override var layer: Int
            get() = RenderLayers.UI + 1000
            set(value) {}
    }

    init {
        getX = { (Game.graphics.width - width) * 0.5f }
        getY = { -height + slideInAmount * (height + (Game.graphics.height - height) * 0.5f) }
        getWidth = { 600.0f }
        getHeight = { 220.0f }

        titleLabel.shadowOffsetX = 3.0f
        titleLabel.shadowOffsetY = -3.0f

        titleLabel.getX = { x + (width - titleLabel.width) * 0.5f }
        titleLabel.getY = { y + height * 0.78f }
        titleLabel.getWidth = { width * 0.9f }
        titleLabel.getHeight = { height * 0.1f }

        divider.getX = { x + (width - divider.width) * 0.5f }
        divider.getY = { y + height * 0.7f }
        divider.getWidth = { width * 0.8f }
        divider.getHeight = { 5.0f }

        returnLabel.getX = { x + width * 0.18f }
        returnLabel.getY = { y + 75.0f }
        returnLabel.getWidth = { 100.0f }
        returnLabel.getHeight = { 40.0f }

        retryLabel.getX = { x + width * 0.51f }
        retryLabel.getY = { y + 75.0f }
        retryLabel.getWidth = { 100.0f }
        retryLabel.getHeight = { 40.0f }

        returnButton.getX = { x + (width - (56.0f * 4 + 56.0f * 0.5f * 2 + 56.0f * 1.2f)) * 0.5f }
        returnButton.getY = { y + 17.0f }
        returnButton.getWidth = { 56.0f }
        returnButton.getHeight = { 56.0f }

        playEasyButton.getX = { x + (width - (56.0f * 4 + 56.0f * 0.5f * 2 + 56.0f * 1.2f)) * 0.5f + 56.0f * 2.5f }
        playEasyButton.getY = { y + 17.0f }
        playEasyButton.getWidth = { 56.0f }
        playEasyButton.getHeight = { 56.0f }

        playNormalButton.getX = { x + (width - (56.0f * 4 + 56.0f * 0.5f * 2 + 56.0f * 1.2f)) * 0.5f + 56.0f * 2.5f + 56.0f * 1.2f }
        playNormalButton.getY = { y + 17.0f }
        playNormalButton.getWidth = { 56.0f }
        playNormalButton.getHeight = { 56.0f }

        playHardButton.getX = { x + (width - (56.0f * 4 + 56.0f * 0.5f * 2 + 56.0f * 1.2f)) * 0.5f + 56.0f * 2.5f + 56.0f * 1.2f * 2 }
        playHardButton.getY = { y + 17.0f }
        playHardButton.getWidth = { 56.0f }
        playHardButton.getHeight = { 56.0f }
    }

    fun slideIn(callback: () -> Unit = {}) {
        startSlideInAmount = slideInAmount
        targetSlideInAmount = 1.0f
        timer = SLIDE_TIME * (1.0 - slideInAmount)
        slideCallback = {
            callback()
            slideCallback = {}
        }
    }

    fun slideOut(callback: () -> Unit = {}) {
        startSlideInAmount = slideInAmount
        targetSlideInAmount = 0.0f
        timer = SLIDE_TIME * slideInAmount.toDouble()
        slideCallback = {
            callback()
            slideCallback = {}
        }
    }

    override fun renderElement(delta: Duration, renderer: Renderer) {
        if (slideInAmount != targetSlideInAmount) {
            slideInAmount = lerp(startSlideInAmount, targetSlideInAmount, 1.0f - (timer / SLIDE_TIME).toFloat()).clamp(0.0f, 1.0f)

            timer -= delta
        } else
            slideCallback()

        renderer.submit(RenderLayers.UI) {
            Game.textures.resultBackgroundNinePatch.draw(it, x, y, width, height)
        }

        titleLabel.render(delta, renderer)
        divider.render(delta, renderer)
        returnLabel.render(delta, renderer)
        retryLabel.render(delta, renderer)
        returnButton.render(delta, renderer)
        playEasyButton.render(delta, renderer)
        playNormalButton.render(delta, renderer)
        playHardButton.render(delta, renderer)
    }
}