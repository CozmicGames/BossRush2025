package com.cozmicgames

import com.cozmicgames.graphics.Graphics2D
import com.cozmicgames.input.ControlManager
import com.cozmicgames.input.InputManager
import com.cozmicgames.multiplayer.PlayerManager
import com.cozmicgames.physics.PhysicsWorld
import com.cozmicgames.states.*
import com.littlekt.Context
import com.littlekt.ContextListener
import com.littlekt.log.Logger
import com.littlekt.util.seconds

class Game(players: PlayerManager, context: Context) : ContextListener(context) {
    companion object {
        lateinit var context: Context
        lateinit var logger: Logger
        lateinit var players: PlayerManager
        lateinit var input: InputManager
        lateinit var graphics: Graphics2D
        val physics = PhysicsWorld(1024.0f, 1024.0f)
        val controls = ControlManager()
        val resources = Resources()
    }

    private lateinit var currentGameState: GameState

    init {
        Companion.players = players
    }

    override suspend fun Context.start() {
        Companion.context = this
        Companion.logger = logger
        physics.width = graphics.width.toFloat()
        physics.height = graphics.height.toFloat()

        resources.load(this)

        Companion.input = InputManager(input)
        val g = Graphics2D(this)
        Companion.graphics = g

        onResize { width, height ->
            g.resize(width, height)
        }

        currentGameState = TestState()

        var isFirstUpdate = true

        onUpdate { delta ->
            players.updatePlayers(delta)
            controls.update(delta.seconds)

            if (isFirstUpdate) {
                currentGameState.begin()
                isFirstUpdate = false
            }

            g.beginFrame()
            val newState = currentGameState.render(delta)()
            g.endFrame()

            if (newState != currentGameState) {
                if (newState !is SuspendGameState)
                    currentGameState.end()
                else
                    if (currentGameState is SuspendableGameState)
                        (currentGameState as SuspendableGameState).suspend()

                if (currentGameState is SuspendGameState && newState is SuspendableGameState)
                    newState.resumeFromSuspension()

                currentGameState = newState

                currentGameState.begin()
            }
        }

        onRelease {
            resources.release()
            g.release()
        }
    }
}