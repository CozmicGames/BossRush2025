package com.cozmicgames

import com.littlekt.createLittleKtApp

fun main() {
    createLittleKtApp {
        width = 960
        height = 660
        title = "Ready for Baittle!"
        canvasId = "canvas"
    }.start {
        Game(it)
    }
}