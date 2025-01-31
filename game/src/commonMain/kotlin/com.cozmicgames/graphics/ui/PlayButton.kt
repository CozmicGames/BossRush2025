package com.cozmicgames.graphics.ui

import com.cozmicgames.Game
import com.cozmicgames.graphics.ui.elements.IconButton
import com.cozmicgames.utils.Difficulty
import com.littlekt.graphics.Color

open class PlayButton(difficulty: Difficulty, onClick: () -> Unit) : IconButton(
    Game.textures.playIcon, when (difficulty) {
        Difficulty.EASY -> Color.fromHex("33984b")
        Difficulty.NORMAL -> Color.fromHex("ffdd25")
        Difficulty.HARD -> Color.fromHex("d31321")
        Difficulty.TUTORIAL -> throw IllegalArgumentException("Tutorial difficulty is not supported for PlayButton")
    }, 0.8f, onClick = onClick
)