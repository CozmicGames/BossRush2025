package com.cozmicgames

import com.cozmicgames.multiplayer.PlayerManager
import com.littlekt.createLittleKtApp

fun main() {
    val multiplayer = PlayroomMultiplayer()

    multiplayer.onPlayerJoin {
        PlayerManager.registerPlayer(it)
    }

    createLittleKtApp {
        width = 960
        height = 540
        title = "Boss Rush 2025"
        canvasId = "canvas"
    }.start {
        Game(multiplayer, it)
    }
}