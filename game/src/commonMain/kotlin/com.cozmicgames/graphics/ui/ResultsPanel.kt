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
import kotlin.math.ceil
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class ResultsPanel(private val results: FightResults) {
    enum class ResultState {
        NONE,
        RETRY_EASY,
        RETRY_NORMAL,
        RETRY_HARD,
        RETURN
    }

    private var animationTime = 0.0.seconds
    private var isAnimationFinished = false

    var width = 600.0f
    val height = 500.0f
    val x = (Game.graphics.width - width) * 0.5f
    var y = (Game.graphics.height - height) * 0.5f

    private val titleLabel = Label(if (results.isVictory) "Catched!" else "Failed!", 72.0f)
    private val resultsDivider = Divider()
    private val durationLabel = DurationLabel(results.duration)
    private val damageResultLabel = ResultLabel("Damage", "${ceil(results.bossDamage * 100).toInt()} %")
    private val healthResultLabel = ResultLabel("Health", "${ceil(results.playerHealth * 100).toInt()} %")
    private val accuracyResultLabel = ResultLabel("Accuracy", "${ceil(results.accuracy * 100).toInt()} %")
    private val messageLabel = ResultMessageLabel(results.message)
    private val ratingBanner = RatingBanner(results.totalPoints) {
        isAnimationFinished = true
        messageLabel.startAnimation()
    }
    private val returnButton = ReturnButton {
        //TODO: Implement return button
    }
    private val playEasyButton = PlayButton(Difficulty.EASY) {
        //TODO: Implement play easy button
    }
    private val playNormalButton = PlayButton(Difficulty.NORMAL) {

    }
    private val playHardButton = PlayButton(Difficulty.HARD) {

    }

    init {
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
        damageResultLabel.getY = { y + height * 0.63f }
        damageResultLabel.getWidth = { width * 0.65f }
        damageResultLabel.getHeight = { height * 0.1f }

        healthResultLabel.getX = { x + (width - healthResultLabel.width) * 0.5f }
        healthResultLabel.getY = { y + height * 0.53f }
        healthResultLabel.getWidth = { width * 0.65f }
        healthResultLabel.getHeight = { height * 0.1f }

        accuracyResultLabel.getX = { x + (width - accuracyResultLabel.width) * 0.5f }
        accuracyResultLabel.getY = { y + height * 0.43f }
        accuracyResultLabel.getWidth = { width * 0.65f }
        accuracyResultLabel.getHeight = { height * 0.1f }

        ratingBanner.getX = { x + (width - ratingBanner.width) * 0.5f }
        ratingBanner.getY = { y + height * 0.2f }

        messageLabel.getX = { x + (width - messageLabel.width) * 0.5f }
        messageLabel.getY = { y + height * 0.2f }
        messageLabel.getWidth = { width * 0.8f }

        returnButton.getX = { x + (width - (64.0f * 4 + 64.0f * 0.5f * 2 + 64.0f * 1.5f)) * 0.5f }
        returnButton.getY = { y + 17.0f }
        returnButton.getWidth = { 64.0f }
        returnButton.getHeight = { 64.0f }

        playEasyButton.getX = { x + (width - (64.0f * 4 + 64.0f * 0.5f * 2 + 64.0f * 1.5f)) * 0.5f + 64.0f * 2.5f }
        playEasyButton.getY = { y + 17.0f }
        playEasyButton.getWidth = { 64.0f }
        playEasyButton.getHeight = { 64.0f }

        playNormalButton.getX = { x + (width - (64.0f * 4 + 64.0f * 0.5f * 2 + 64.0f * 1.5f)) * 0.5f + 64.0f * 2.5f + 64.0f * 1.5f }
        playNormalButton.getY = { y + 17.0f }
        playNormalButton.getWidth = { 64.0f }
        playNormalButton.getHeight = { 64.0f }

        playHardButton.getX = { x + (width - (64.0f * 4 + 64.0f * 0.5f * 2 + 64.0f * 1.5f)) * 0.5f + 64.0f * 2.5f + 64.0f * 1.5f * 2 }
        playHardButton.getY = { y + 17.0f }
        playHardButton.getWidth = { 64.0f }
        playHardButton.getHeight = { 64.0f }
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

        renderer.submit(RenderLayers.UI_BEGIN) {
            Game.resources.resultBackgroundNinePatch.draw(it, x, y, width, height)
        }

        titleLabel.render(delta, renderer)
        resultsDivider.render(delta, renderer)
        durationLabel.render(delta, renderer)
        damageResultLabel.render(delta, renderer)
        healthResultLabel.render(delta, renderer)
        accuracyResultLabel.render(delta, renderer)
        ratingBanner.render(delta, renderer)
        returnButton.render(delta, renderer)
        playEasyButton.render(delta, renderer)
        playNormalButton.render(delta, renderer)
        playHardButton.render(delta, renderer)

        if (isAnimationFinished)
            messageLabel.render(delta, renderer)

        return ResultState.NONE
    }
}