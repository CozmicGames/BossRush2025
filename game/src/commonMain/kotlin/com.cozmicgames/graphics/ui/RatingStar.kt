package com.cozmicgames.graphics.ui

import com.cozmicgames.Game
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.elements.Image
import com.cozmicgames.utils.Easing
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

open class RatingStar(val isFull: Boolean, val onAnimationFinish: () -> Unit) : GUIElement() {
    companion object {
        private val ANIMATION_FULL_TIME = 0.4.seconds
    }

    private inner class EmptyStar : Image(Game.resources.starEmpty) {
        override var layer
            get() = this@RatingStar.layer + 1
            set(value) {}
    }

    private inner class StarFull : Image(Game.resources.starFull) {
        override var layer
            get() = this@RatingStar.layer + 2
            set(value) {}

        private var animationTime = 0.0.seconds

        override fun renderElement(delta: Duration, renderer: Renderer) {
            if (!isAnimationFinished) {
                if (animationTime >= ANIMATION_FULL_TIME) {
                    isAnimationFinished = true
                    onAnimationFinish()
                }

                fullStarScale = Easing.ELASTIC_OUT((animationTime / ANIMATION_FULL_TIME).toFloat())
                animationTime += delta
            } else
                fullStarScale = 1.0f

            super.renderElement(delta, renderer)
        }
    }

    private val emptyStar = EmptyStar()
    private var fullSar: StarFull? = null
    private var fullStarScale = 1.0f

    var isAnimationFinished = !isFull
        private set

    init {
        emptyStar.getX = { x }
        emptyStar.getY = { y }
        emptyStar.getWidth = { width }
        emptyStar.getHeight = { height }
    }

    fun startAnimation() {
        if (!isFull)
            return

        val fullSar = StarFull()
        fullSar.getX = { x + (width - fullSar.width) * 0.5f }
        fullSar.getY = { y + (height - fullSar.height) * 0.5f }
        fullSar.getWidth = { width * fullStarScale }
        fullSar.getHeight = { height * fullStarScale }
        this.fullSar = fullSar
    }

    override fun renderElement(delta: Duration, renderer: Renderer) {
        emptyStar.render(delta, renderer)
        fullSar?.render(delta, renderer)
    }
}