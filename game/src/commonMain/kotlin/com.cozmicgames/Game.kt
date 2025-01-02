package com.cozmicgames

import com.cozmicgames.graphics.Graphics2D
import com.cozmicgames.input.ControlManager
import com.cozmicgames.input.InputManager
import com.cozmicgames.input.InputState
import com.cozmicgames.multiplayer.Multiplayer
import com.littlekt.Context
import com.littlekt.ContextListener

class Game(multiplayer: Multiplayer, context: Context) : ContextListener(context) {
    companion object {
        lateinit var multiplayer: Multiplayer
        lateinit var input: InputManager
        val controls = ControlManager()
    }

    private val inputState = InputState()

    init {
        Companion.multiplayer = multiplayer
    }

    override suspend fun Context.start() {
        Companion.input = InputManager(input)
        val g = Graphics2D(this)

        onResize { width, height ->
            g.resize(width, height)
        }

        onUpdate { delta ->
            Companion.input.update(delta, inputState)

            g.beginFrame()
            g.drawFrame()
            g.endFrame()

            multiplayer.getMyPlayerState().setState("input", inputState)

            if (multiplayer.isHost) {
                println("Host")
            }
        }

        onRelease {
            g.release()
        }
    }
}