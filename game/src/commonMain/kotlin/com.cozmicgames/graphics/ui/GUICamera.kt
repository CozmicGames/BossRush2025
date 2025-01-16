package com.cozmicgames.graphics.ui

import com.cozmicgames.Game
import com.littlekt.graphics.OrthographicCamera

class GUICamera {
    val camera = OrthographicCamera()

    init {
        resize(Game.graphics.width, Game.graphics.height)
    }

    fun resize(width: Int, height: Int) {
        camera.ortho(width.toFloat(), height.toFloat())
    }
}