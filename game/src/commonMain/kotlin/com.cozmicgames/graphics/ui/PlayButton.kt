package com.cozmicgames.graphics.ui

import com.cozmicgames.Game
import com.cozmicgames.graphics.ui.elements.IconButton
import com.cozmicgames.utils.Difficulty
import com.littlekt.graphics.Color

class PlayButton(difficulty: Difficulty, onClick: () -> Unit) : IconButton(
    Game.resources.playIcon, when (difficulty) {
        Difficulty.EASY -> Color.fromHex("33984b")
        Difficulty.NORMAL -> Color.fromHex("ffdd25")
        Difficulty.HARD -> Color.fromHex("d31321")
    }, 0.8f, onClick = onClick
)