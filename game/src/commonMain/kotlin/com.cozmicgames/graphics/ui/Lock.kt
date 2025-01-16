package com.cozmicgames.graphics.ui

import com.cozmicgames.Game
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.elements.Image
import com.cozmicgames.utils.Easing
import com.littlekt.graphics.Color
import com.littlekt.graphics.MutableColor
import com.littlekt.util.seconds
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

open class Lock : GUIElement() {
    companion object {
        private val UNLOCK_DURATION = 0.3.seconds
        private const val MAX_SHACKLE_OFFSET = 0.2f
    }

    val color = MutableColor(Color.WHITE)

    private var animationTimer = 0.0.seconds
    private var animationStarted = false
    private var animationFinished = false
    private var onAnimationFinish: () -> Unit = {}
    private var shackleOffset = 0.0f

    private val body = object : Image(Game.resources.lockBody, color) {
        override var layer: Int
            get() = this@Lock.layer + 1
            set(value) {}
    }
    private val shackle = object : Image(Game.resources.lockShackle, color) {
        override var layer: Int
            get() = this@Lock.layer
            set(value) {}
    }

    init {
        body.getX = { x }
        body.getY = { y }
        body.getWidth = { width }
        body.getHeight = { height }
        shackle.getX = { x }
        shackle.getY = { y + shackleOffset * height }
        shackle.getWidth = { width }
        shackle.getHeight = { height }
    }

    fun startUnlockAnimation(onFinish: () -> Unit) {
        if (!animationStarted) {
            animationStarted = true
            animationFinished = false
            animationTimer = 0.0.seconds
            onAnimationFinish = onFinish
            color.set(Color.WHITE)
        }
    }

    override fun renderElement(delta: Duration, renderer: Renderer) {
        if (animationStarted) {
            animationTimer += delta

            val factor = Easing.QUAD_IN(animationTimer.seconds / UNLOCK_DURATION.seconds)

            if (factor >= 1.0f) {
                animationFinished = true
                color.set(0.0f, 0.0f, 0.0f, 0.0f)
                onAnimationFinish()
            } else
                Color.WHITE.mix(Color(0.5f, 0.5f, 0.5f, 0.0f), factor, color)

            shackleOffset = MAX_SHACKLE_OFFSET * factor
        }

        body.render(delta, renderer)
        shackle.render(delta, renderer)
    }
}