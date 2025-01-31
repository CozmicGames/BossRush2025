package com.cozmicgames.graphics.ui

import com.cozmicgames.Game
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.utils.Difficulty
import kotlin.time.Duration

open class Healthbar(private val difficulty: Difficulty) : GUIElement() {
    private var health = difficulty.basePlayerHealth

    fun update(health: Int) {
        this.health = health
    }

    override fun renderElement(delta: Duration, renderer: Renderer) {
        renderer.submit(layer) {
            val numBars = difficulty.basePlayerHealth
            val barWidth = width / numBars
            val barHeight = height

            for (i in 0 until numBars) {
                val x = this.x + i * barWidth
                val y = this.y

                if (i < health)
                    Game.textures.playerHealthIndicatorNinepatch.draw(it, x + barWidth * 0.05f, y, barWidth * 0.9f, barHeight)
                else
                    Game.textures.playerHealthEmptyIndicatorNinepatch.draw(it, x + barWidth * 0.05f, y, barWidth * 0.9f, barHeight)
            }
        }
    }
}