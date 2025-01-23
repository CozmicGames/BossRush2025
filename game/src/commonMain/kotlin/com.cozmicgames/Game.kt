package com.cozmicgames

import com.cozmicgames.entities.worldObjects.World
import com.cozmicgames.events.EventManager
import com.cozmicgames.graphics.Graphics2D
import com.cozmicgames.input.ControlManager
import com.cozmicgames.input.InputManager
import com.cozmicgames.multiplayer.PlayerManager
import com.cozmicgames.physics.PhysicsWorld
import com.cozmicgames.states.*
import com.cozmicgames.weapons.AreaEffectManager
import com.cozmicgames.weapons.ProjectileManager
import com.littlekt.Context
import com.littlekt.ContextListener
import com.littlekt.async.newSingleThreadAsyncContext
import com.littlekt.log.Logger
import com.littlekt.util.seconds
import kotlin.js.Date
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

class Game(players: PlayerManager, context: Context) : ContextListener(context) {
    companion object {
        var upTime = 0.0.seconds
        lateinit var context: Context
        lateinit var logger: Logger
        lateinit var players: PlayerManager
        lateinit var input: InputManager
        lateinit var graphics: Graphics2D
        val physics = PhysicsWorld(1500.0f, 1500.0f)
        val controls = ControlManager()
        val resources = Resources()
        val projectiles = ProjectileManager()
        val areaEffects = AreaEffectManager()
        val world = World()
        val events = EventManager()
        val random = Random(Date.now().toLong())
    }

    private lateinit var currentGameState: GameState

    init {
        Companion.players = players
    }

    override suspend fun Context.start() {
        Companion.context = this
        Companion.logger = logger

        resources.load(this)

        Companion.input = InputManager(input)
        val g = Graphics2D(this)
        Companion.graphics = g

        onResize { width, height ->
            g.resize(width, height)
        }

        currentGameState = SplashScreenState()

        var isFirstUpdate = true

        newSingleThreadAsyncContext()

        onUpdate { delta ->
            upTime += delta

            events.processEvents()

            projectiles.update(delta)
            areaEffects.update(delta)
            controls.update(delta.seconds)

            if (isFirstUpdate) {
                currentGameState.begin()
                isFirstUpdate = false
            }

            g.beginFrame()
            val newState = currentGameState.render(delta)()
            g.endFrame()

            players.update(delta)
            events.sendEvents()

            if (newState !== currentGameState) {
                currentGameState.end()
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