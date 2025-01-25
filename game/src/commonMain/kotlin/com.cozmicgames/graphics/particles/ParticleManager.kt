package com.cozmicgames.graphics.particles

import com.littlekt.graphics.g2d.SpriteBatch
import kotlin.time.Duration

class ParticleManager {
    val systems = arrayListOf<ParticleSystem>()

    val globalSystem = ParticleSystem()

    fun update(delta: Duration) {
        for (system in systems)
            system.update(delta)

        globalSystem.update(delta)
    }

    fun render(batch: SpriteBatch) {
        for (system in systems)
            system.render(batch)

        globalSystem.render(batch)
    }
}