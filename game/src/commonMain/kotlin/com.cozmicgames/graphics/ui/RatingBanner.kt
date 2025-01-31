package com.cozmicgames.graphics.ui

import com.cozmicgames.Game
import com.cozmicgames.graphics.Renderer
import kotlin.time.Duration

open class RatingBanner(val rating: Int, private val onAnimationFinished: () -> Unit) : GUIElement() {
    private lateinit var stars: Array<RatingStar>

    init {
        width = 700.0f
        height = 100.0f

        stars = Array(5) {
            object : RatingStar(it <= rating, {
                if (it != stars.lastIndex) stars[it + 1].startAnimation()
            }) {
                override var layer: Int
                    get() = this@RatingBanner.layer + 1
                    set(value) {}
            }
        }

        stars.forEach {
            it.getX = { x + (width - (it.width * 5 + it.width * 0.4f * 4)) * 0.5f + it.width * 1.4f * stars.indexOf(it) }
            it.getY = { y + height - it.height - 10.0f }
            it.getWidth = { height * 0.5f }
            it.getHeight = { height * 0.5f }
        }
    }

    private var isAnimationStarted = false
    private var isAnimationFinished = false

    fun startAnimation() {
        if (isAnimationStarted)
            return

        stars.first().startAnimation()
        isAnimationStarted = true
    }

    override fun renderElement(delta: Duration, renderer: Renderer) {
        if (!isAnimationFinished && stars.all { it.isAnimationFinished }) {
            onAnimationFinished()
            isAnimationFinished = true
        }

        renderer.submit(layer) {
            it.draw(Game.textures.resultBanner, x, y, width = width, height = height)
        }

        stars.forEach { it.render(delta, renderer) }
    }
}