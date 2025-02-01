package com.cozmicgames.graphics.ui

import com.cozmicgames.Game
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.elements.IconButton
import com.cozmicgames.graphics.ui.elements.Tooltip
import com.cozmicgames.utils.Difficulty
import com.littlekt.graphics.Color
import kotlin.time.Duration

open class PlayButton(difficulty: Difficulty, onClick: () -> Unit) : IconButton(
    Game.textures.playIcon, when (difficulty) {
        Difficulty.EASY -> Color.fromHex("33984b")
        Difficulty.NORMAL -> Color.fromHex("ffdd25")
        Difficulty.HARD -> Color.fromHex("d31321")
        Difficulty.TUTORIAL -> throw IllegalArgumentException("Tutorial difficulty is not supported for PlayButton")
    }, 0.8f, onClick = onClick
) {
    private val tooltip = Tooltip(
        when (difficulty) {
            Difficulty.EASY -> "Play Easy"
            Difficulty.NORMAL -> "Play Normal"
            Difficulty.HARD -> "Play Hard"
            else -> throw IllegalArgumentException("Tutorial difficulty is not supported for PlayButton")
        }
    )

    override fun renderElement(delta: Duration, renderer: Renderer) {
        super.renderElement(delta, renderer)

        if (isEnabled && isHovered)
            tooltip.render(delta, renderer)
    }
}