package com.cozmicgames.graphics.ui

import com.cozmicgames.Constants
import com.cozmicgames.Game
import com.cozmicgames.bosses.boss1.Boss1Desc
import com.cozmicgames.bosses.boss2.Boss2Desc
import com.cozmicgames.bosses.boss3.Boss3Desc
import com.cozmicgames.bosses.boss4.Boss4Desc
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.elements.Label
import com.cozmicgames.states.GameState
import com.cozmicgames.utils.Easing
import com.littlekt.graphics.Color
import com.littlekt.graphics.HAlign
import com.littlekt.graphics.MutableColor
import com.littlekt.math.clamp
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

open class FightSelectionUI(val onSelect: (GameState) -> Unit) : GUIElement() {
    companion object {
        private val BOSS_DESCRIPTORS = arrayOf(
            Boss1Desc(),
            Boss2Desc(),
            Boss3Desc(),
            Boss4Desc()
        )

        private val TRANSITION_TIME_PER_POSTER = 0.2.seconds
        private val TRANSITION_TIME_FINAL_POSTER = 0.5.seconds
    }

    private interface TransitionStage {
        fun update(delta: Duration): TransitionStage?
    }

    private inner class PosterTransitionStage(val index: Int) : TransitionStage {
        var timer = 0.0.seconds

        override fun update(delta: Duration): TransitionStage? {
            val factor = Easing.CUBIC_IN((timer / TRANSITION_TIME_PER_POSTER).toFloat()).clamp(0.0f, 1.0f)
            selectionPosters[index].getY = { 230.0f - (230.0f + Constants.BOSS_SELECTION_POSTER_HEIGHT) * factor }

            timer += delta

            return if (timer > TRANSITION_TIME_PER_POSTER) {
                if (index == selectionPosters.lastIndex)
                    null //TODO: transition to final fight
                else
                    PosterTransitionStage(index + 1)
            } else this
        }
    }

    private inner class StartTransitionStage : TransitionStage {
        private var posterTransitionStage: TransitionStage? = PosterTransitionStage(0)
        private var timer = 0.0.seconds

        override fun update(delta: Duration): TransitionStage {
            posterTransitionStage = posterTransitionStage?.update(delta)
            val factor = 1.0f - (timer / (TRANSITION_TIME_PER_POSTER * 4)).toFloat().clamp(0.0f, 1.0f)
            labelColor.a = factor
            labelShadowColor.a = factor

            timer += delta

            if (posterTransitionStage == null && factor == 0.0f) {
                upperLabel.text = "They've slipped the net, but not for long!"
                upperLabel.fontSize = 40.0f

                lowerLabel.text = "Hunt them down!"
                lowerLabel.fontSize = 40.0f
                return EndTransitionStage()
            }

            return this
        }
    }

    private inner class EndTransitionStage : TransitionStage {
        private var timer = 0.0.seconds
        private var isFirstUpdate = true

        override fun update(delta: Duration): TransitionStage? {
            if (isFirstUpdate) {
                showFinalSelectionPoster = true
                isFirstUpdate = false
            }

            val colorFactor = (timer / TRANSITION_TIME_FINAL_POSTER).toFloat().clamp(0.0f, 1.0f)
            labelColor.a = colorFactor
            labelShadowColor.a = colorFactor

            val posterFactor = Easing.CUBIC_IN((timer / TRANSITION_TIME_FINAL_POSTER).toFloat()).clamp(0.0f, 1.0f)
            finalFightSelectionPoster.getY = { -Constants.BOSS_SELECTION_POSTER_HEIGHT + (230.0f + Constants.BOSS_SELECTION_POSTER_HEIGHT) * posterFactor }

            timer += delta

            return if (timer > TRANSITION_TIME_FINAL_POSTER) null else this
        }
    }

    private val labelColor = MutableColor(Color.WHITE)
    private val labelShadowColor = MutableColor(Color.BLACK)

    private val upperLabel = object : Label("Ready the ship!", 48.0f, labelColor) {
        override var layer: Int
            get() = this@FightSelectionUI.layer + 1
            set(value) {}
    }

    private val lowerLabel = object : Label("Choose your mission!", 48.0f, labelColor) {
        override var layer: Int
            get() = this@FightSelectionUI.layer + 1
            set(value) {}
    }

    private val selectionPosters = List(4) {
        object : SelectionPoster(BOSS_DESCRIPTORS[it], it in Game.game.unlockedBossIndices, onSelect) {
            override var layer: Int
                get() = this@FightSelectionUI.layer + 1
                set(value) {}
        }
    }

    private val finalFightSelectionPoster = object : FinalFightSelectionPoster(BOSS_DESCRIPTORS, false, onSelect) {
        override var layer: Int
            get() = this@FightSelectionUI.layer + 1
            set(value) {}
    }

    private var currentTransitionStage: TransitionStage? = null
    private var showFinalSelectionPoster = false

    init {
        upperLabel.getX = { Game.graphics.width * 0.5f }
        upperLabel.getY = { 560.0f }
        upperLabel.shadowOffsetX = 3.0f
        upperLabel.shadowOffsetY = -3.0f
        upperLabel.shadowColor = labelShadowColor
        upperLabel.hAlign = HAlign.CENTER

        lowerLabel.getX = { Game.graphics.width * 0.5f }
        lowerLabel.getY = { 520.0f }
        lowerLabel.shadowOffsetX = 3.0f
        lowerLabel.shadowOffsetY = -3.0f
        lowerLabel.shadowColor = labelShadowColor
        lowerLabel.hAlign = HAlign.CENTER

        val selectionPosterSpacing = (Game.graphics.width - selectionPosters.size * Constants.BOSS_SELECTION_POSTER_WIDTH) / (selectionPosters.size + 1)

        selectionPosters.forEachIndexed { index, poster ->
            poster.getX = { selectionPosterSpacing + index * (Constants.BOSS_SELECTION_POSTER_WIDTH + selectionPosterSpacing) }
            poster.getY = { 230.0f }
        }

        finalFightSelectionPoster.getX = { Game.graphics.width * 0.5f - finalFightSelectionPoster.width * 0.5f }
        finalFightSelectionPoster.getY = { -Constants.BOSS_SELECTION_POSTER_HEIGHT }
    }

    fun transitionToFinalFight() {
        currentTransitionStage = StartTransitionStage()
    }

    fun unlock(index: Int, callback: () -> Unit) {
        selectionPosters[index].unlock(callback)
    }

    override fun renderElement(delta: Duration, renderer: Renderer) {
        currentTransitionStage = currentTransitionStage?.update(delta)

        upperLabel.render(delta, renderer)
        lowerLabel.render(delta, renderer)

        if (!showFinalSelectionPoster)
            selectionPosters.forEach { it.render(delta, renderer) }

        if (showFinalSelectionPoster)
            finalFightSelectionPoster.render(delta, renderer)
    }
}