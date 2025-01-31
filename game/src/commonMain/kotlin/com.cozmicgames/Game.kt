package com.cozmicgames

import com.cozmicgames.audio.Audio
import com.cozmicgames.entities.worldObjects.World
import com.cozmicgames.graphics.Graphics2D
import com.cozmicgames.graphics.Textures
import com.cozmicgames.graphics.particles.ParticleManager
import com.cozmicgames.input.ControlManager
import com.cozmicgames.input.InputManager
import com.cozmicgames.physics.PhysicsWorld
import com.cozmicgames.states.*
import com.cozmicgames.weapons.AreaEffectManager
import com.cozmicgames.weapons.ProjectileManager
import com.littlekt.Context
import com.littlekt.ContextListener
import com.littlekt.log.Logger
import com.littlekt.util.seconds
import kotlin.js.Date
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

class Game(context: Context) : ContextListener(context) {
    companion object {
        var upTime = 0.0.seconds
        lateinit var context: Context
        lateinit var logger: Logger
        lateinit var input: InputManager
        lateinit var graphics: Graphics2D
        lateinit var audio: Audio
        val random = Random(Date.now().toLong())
        val player = Player()
        val particles = ParticleManager()
        val physics = PhysicsWorld(1500.0f, 1500.0f)
        val controls = ControlManager()
        val textures = Textures()
        val projectiles = ProjectileManager()
        val areaEffects = AreaEffectManager()
        val world = World()
    }

    private lateinit var currentGameState: GameState

    override suspend fun Context.start() {
        Companion.context = this
        Companion.logger = logger

        audio = Audio()

        Companion.input = InputManager(input)
        val g = Graphics2D(this)
        Companion.graphics = g

        g.load(this)
        textures.load(this)
        audio.load(this)

        onResize { width, height ->
            g.resize(width, height)
        }

        currentGameState = MenuState()

        var isFirstUpdate = true

        onUpdate { delta ->
            upTime += delta

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

            if (newState !== currentGameState) {
                currentGameState.end()
                currentGameState = newState
                currentGameState.begin()
            }
        }

        onRelease {
            audio.release()
            textures.release()
            g.release()
        }
    }
}