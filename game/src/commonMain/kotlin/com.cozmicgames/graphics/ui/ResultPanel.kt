package com.cozmicgames.graphics.ui

import com.cozmicgames.Game
import com.cozmicgames.graphics.RenderLayers
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.elements.Divider
import com.cozmicgames.graphics.ui.elements.Label
import com.cozmicgames.utils.Difficulty
import com.cozmicgames.utils.Easing
import com.cozmicgames.utils.FightResults
import com.cozmicgames.utils.lerp
import com.littlekt.graphics.HAlign
import kotlin.math.ceil
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class ResultPanel(private val results: FightResults) {
    enum class ResultState {
        NONE,
        RETRY_EASY,
        RETRY_NORMAL,
        RETRY_HARD,
        RETURN
    }

    private var animationTime = 0.0.seconds
    private var isAnimationFinished = false
    private var resultState = ResultState.NONE

    var width = 600.0f
    val height = 600.0f
    val x = (Game.graphics.width - width) * 0.5f
    var y = (Game.graphics.height - height) * 0.5f

    private val titleLabel = object : Label(if (results.isVictory) "Catched!" else "You're dead!", 72.0f) {
        override var layer: Int
            get() = RenderLayers.UI + 1000
            set(value) {}
    }
    private val resultsDivider = object : Divider() {
        override var layer: Int
            get() = RenderLayers.UI + 1000
            set(value) {}
    }
    private val durationLabel = object : ResultDurationLabel(results.duration) {
        override var layer: Int
            get() = RenderLayers.UI + 1000
            set(value) {}
    }
    private val damageResultLabel = object : ResultLabel("Damage", "${ceil(results.bossDamage * 100).toInt()} %") {
        override var layer: Int
            get() = RenderLayers.UI + 1000
            set(value) {}
    }
    private val healthResultLabel = object : ResultLabel("Health", "${ceil(results.playerHealth * 100).toInt()} %") {
        override var layer: Int
            get() = RenderLayers.UI + 1000
            set(value) {}
    }
    private val accuracyResultLabel = object : ResultLabel("Accuracy", "${ceil(results.accuracy * 100).toInt()} %") {
        override var layer: Int
            get() = RenderLayers.UI + 1000
            set(value) {}
    }
    private val messageLabel = object : ResultMessageLabel(results.message) {
        override var layer: Int
            get() = RenderLayers.UI + 1000
            set(value) {}
    }
    private val ratingBanner = object : RatingBanner(results.totalPoints, {
        isAnimationFinished = true
        messageLabel.startAnimation()
    }) {
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
        titleLabel.shadowOffsetX = 3.0f
        titleLabel.shadowOffsetY = -3.0f

        titleLabel.getX = { x + (width - titleLabel.width) * 0.5f }
        titleLabel.getY = { y + height * 0.87f }
        titleLabel.getWidth = { width * 0.9f }
        titleLabel.getHeight = { height * 0.1f }

        resultsDivider.getX = { x + (width - resultsDivider.width) * 0.5f }
        resultsDivider.getY = { y + height * 0.85f }
        resultsDivider.getWidth = { width * 0.8f }
        resultsDivider.getHeight = { 5.0f }

        durationLabel.getX = { x + (width - durationLabel.width) * 0.5f }
        durationLabel.getY = { y + height * 0.73f }
        durationLabel.getWidth = { width * 0.65f }
        durationLabel.getHeight = { height * 0.1f }

        damageResultLabel.getX = { x + (width - damageResultLabel.width) * 0.5f }
        damageResultLabel.getY = { y + height * 0.65f }
        damageResultLabel.getWidth = { width * 0.65f }
        damageResultLabel.getHeight = { height * 0.1f }

        healthResultLabel.getX = { x + (width - healthResultLabel.width) * 0.5f }
        healthResultLabel.getY = { y + height * 0.57f }
        healthResultLabel.getWidth = { width * 0.65f }
        healthResultLabel.getHeight = { height * 0.1f }

        accuracyResultLabel.getX = { x + (width - accuracyResultLabel.width) * 0.5f }
        accuracyResultLabel.getY = { y + height * 0.49f }
        accuracyResultLabel.getWidth = { width * 0.65f }
        accuracyResultLabel.getHeight = { height * 0.1f }

        ratingBanner.getX = { x + (width - ratingBanner.width) * 0.5f }
        ratingBanner.getY = { y + height * 0.3f }

        messageLabel.getX = { x + (width - messageLabel.width) * 0.5f }
        messageLabel.getY = { y + height * 0.28f }
        messageLabel.getWidth = { width * 0.8f }

        returnLabel.getX = { x + width * 0.28f }
        returnLabel.getY = { y + 88.0f }
        returnLabel.hAlign = HAlign.CENTER

        retryLabel.getX = { x + width * 0.72f }
        retryLabel.getY = { y + 88.0f }
        retryLabel.hAlign = HAlign.CENTER

        returnButton.getX = { x + width * 0.28f - 56.0f * 0.5f }
        returnButton.getY = { y + 17.0f }
        returnButton.getWidth = { 56.0f }
        returnButton.getHeight = { 56.0f }

        if (Game.player.isFreePlay) {
            playEasyButton.getX = { x + width * 0.72f - (56.0f * 3 + 10.0f * 2) * 0.5f }
            playEasyButton.getY = { y + 17.0f }
            playEasyButton.getWidth = { 56.0f }
            playEasyButton.getHeight = { 56.0f }

            playNormalButton.getX = { x + width * 0.72f - (56.0f * 3 + 10.0f * 2) * 0.5f + 56.0f + 10.0f }
            playNormalButton.getY = { y + 17.0f }
            playNormalButton.getWidth = { 56.0f }
            playNormalButton.getHeight = { 56.0f }

            playHardButton.getX = { x + width * 0.72f - (56.0f * 3 + 10.0f * 2) * 0.5f + 56.0f * 2 + 10.0f * 2 }
            playHardButton.getY = { y + 17.0f }
            playHardButton.getWidth = { 56.0f }
            playHardButton.getHeight = { 56.0f }
        } else {
            when (results.difficulty) {
                Difficulty.EASY -> {
                    playEasyButton.getX = { x + width * 0.72f - 56.0f * 0.5f }
                    playEasyButton.getY = { y + 17.0f }
                    playEasyButton.getWidth = { 56.0f }
                    playEasyButton.getHeight = { 56.0f }
                }

                Difficulty.NORMAL -> {
                    playNormalButton.getX = { x + width * 0.72f - 56.0f * 0.5f }
                    playNormalButton.getY = { y + 17.0f }
                    playNormalButton.getWidth = { 56.0f }
                    playNormalButton.getHeight = { 56.0f }
                }

                Difficulty.HARD -> {
                    playHardButton.getX = { x + width * 0.72f - 56.0f * 0.5f }
                    playHardButton.getY = { y + 17.0f }
                    playHardButton.getWidth = { 56.0f }
                    playHardButton.getHeight = { 56.0f }
                }

                else -> {}
            }
        }
    }

    fun renderAndGetResultState(delta: Duration, renderer: Renderer): ResultState {
        animationTime += delta

        if (animationTime < 2.0.seconds) {
            if (!isAnimationFinished) {
                val factor = Easing.BOUNCE_OUT((animationTime / 2.0.seconds).toFloat())
                y = lerp(0.0f, (Game.graphics.height - height) * 0.5f, factor)
            }
        } else {
            y = (Game.graphics.height - height) * 0.5f
            ratingBanner.startAnimation()
            isAnimationFinished = true
        }

        renderer.submit(RenderLayers.UI) {
            Game.textures.resultBackgroundNinePatch.draw(it, x, y, width, height)
            Game.textures.ratingBackgroundNinePatch.draw(it, x + width * 0.1f, y + height * 0.5f, width * 0.8f, height * 0.315f)
        }

        titleLabel.render(delta, renderer)
        resultsDivider.render(delta, renderer)
        durationLabel.render(delta, renderer)
        damageResultLabel.render(delta, renderer)
        healthResultLabel.render(delta, renderer)
        accuracyResultLabel.render(delta, renderer)
        ratingBanner.render(delta, renderer)

        returnLabel.render(delta, renderer)
        retryLabel.render(delta, renderer)
        returnButton.render(delta, renderer)

        if (Game.player.isFreePlay) {
            playEasyButton.render(delta, renderer)
            playNormalButton.render(delta, renderer)
            playHardButton.render(delta, renderer)
        } else {
            when (results.difficulty) {
                Difficulty.EASY -> playEasyButton.render(delta, renderer)
                Difficulty.NORMAL -> playNormalButton.render(delta, renderer)
                Difficulty.HARD -> playHardButton.render(delta, renderer)
                else -> {}
            }
        }

        if (isAnimationFinished)
            messageLabel.render(delta, renderer)

        return resultState
    }
}