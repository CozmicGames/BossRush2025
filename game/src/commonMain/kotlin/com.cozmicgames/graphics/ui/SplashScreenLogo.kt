package com.cozmicgames.graphics.ui

import com.cozmicgames.Game
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.elements.Image
import com.cozmicgames.graphics.ui.elements.Label
import com.cozmicgames.utils.Easing
import com.littlekt.graphics.HAlign
import com.littlekt.graphics.MutableColor
import com.littlekt.graphics.VAlign
import com.littlekt.math.clamp
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class SplashScreenLogo : GUIElement() {
    private interface Stage {
        fun update(delta: Duration): Stage?
    }

    private inner class Stage0 : Stage {
        var timer = 0.0.seconds

        override fun update(delta: Duration): Stage {
            val factor = (timer / 0.7.seconds).toFloat().clamp(0.0f, 1.0f)

            color.a = Easing.QUAD_IN_OUT(factor)

            timer += delta

            return if (timer > 0.7.seconds) Stage1() else this
        }
    }

    private inner class Stage1 : Stage {
        var timer = 0.0.seconds

        override fun update(delta: Duration): Stage {
            timer += delta

            return if (timer > 1.1.seconds) Stage2() else this
        }
    }

    private inner class Stage2 : Stage {
        var timer = 0.0.seconds

        override fun update(delta: Duration): Stage {
            val factor = (timer / 0.7.seconds).toFloat().clamp(0.0f, 1.0f)

            color.a = 1.0f - Easing.QUAD_IN_OUT(factor)

            timer += delta

            return if (timer > 0.7.seconds) Stage3() else this
        }
    }

    private inner class Stage3 : Stage {
        var timer = 0.0.seconds

        override fun update(delta: Duration): Stage? {
            timer += delta

            return if (timer > 0.5.seconds) null else this
        }
    }

    private val color = MutableColor(1.0f, 1.0f, 1.0f, 0.0f)
    private val image = Image(Game.resources.logo, color)
    private var stage: Stage? = Stage0()
    private var onFinish: () -> Unit = {}
    private var isAnimationStarted = false
    private var isAnimationFinished = false

    init {
        image.getX = { Game.graphics.width * 0.5f - image.width * 0.5f }
        image.getY = { Game.graphics.height * 0.5f - image.height * 0.5f }
        image.getWidth = { Game.resources.logo.width.toFloat() }
        image.getHeight = { Game.resources.logo.height.toFloat() }
    }

    fun startAnimation(onFinish: () -> Unit) {
        if (!isAnimationStarted) {
            isAnimationStarted = true
            this.onFinish = onFinish
        }
    }

    override fun renderElement(delta: Duration, renderer: Renderer) {
        if (isAnimationStarted && !isAnimationFinished) {
            val newStage = stage?.update(delta)
            if (newStage == null) {
                isAnimationFinished = true
                onFinish()
            }

            stage = newStage
        }

        image.render(delta, renderer)
    }
}