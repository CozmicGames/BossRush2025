package com.cozmicgames.graphics.ui

import com.cozmicgames.Game
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.elements.Label
import com.cozmicgames.utils.Easing
import com.littlekt.graphics.HAlign
import com.littlekt.graphics.MutableColor
import com.littlekt.graphics.VAlign
import com.littlekt.math.clamp
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class FightStartMessage : GUIElement() {
    private interface Stage {
        fun update(delta: Duration): Stage?
    }

    private inner class Stage0 : Stage {
        var timer = 0.0.seconds

        override fun update(delta: Duration): Stage {
            val factor = (timer / 1.3.seconds).toFloat().clamp(0.0f, 1.0f)
            label.fontSize = 140.0f * (1.0f - Easing.QUAD_IN(factor))

            timer += delta

            return if (timer > 1.3.seconds) Stage1() else this
        }
    }

    private inner class Stage1 : Stage {
        var timer = 0.0.seconds

        override fun update(delta: Duration): Stage {
            timer += delta

            return if (timer > 0.4.seconds) Stage2() else this
        }
    }

    private inner class Stage2 : Stage {
        var timer = 0.0.seconds

        init {
            label.text = "Haul!"
        }

        override fun update(delta: Duration): Stage {
            val factor = (timer / 0.8.seconds).toFloat().clamp(0.0f, 1.0f)
            label.fontSize = 600.0f * Easing.QUAD_IN(factor)
            color.a = 0.85f * (1.0f - factor)

            timer += delta

            return if (timer > 0.8.seconds) Stage3() else this
        }
    }

    private inner class Stage3 : Stage {
        var timer = 0.0.seconds

        override fun update(delta: Duration): Stage? {
            timer += delta

            return if (timer > 0.3.seconds) null else this
        }
    }

    private val color = MutableColor(1.0f, 1.0f, 1.0f, 0.7f)
    private val label = Label("Ready?", 0.1f, color)
    private var stage: Stage? = Stage0()
    private var onFinish: () -> Unit = {}
    private var isAnimationStarted = false
    private var isAnimationFinished = false

    init {
        label.getX = { 0.0f }
        label.getY = { 0.0f }
        label.getWidth = { Game.graphics.width.toFloat() }
        label.getHeight = { Game.graphics.height.toFloat() }
        label.hAlign = HAlign.CENTER
        label.vAlign = VAlign.CENTER
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

        if (label.fontSize > 0.0f)
            label.render(delta, renderer)
    }
}