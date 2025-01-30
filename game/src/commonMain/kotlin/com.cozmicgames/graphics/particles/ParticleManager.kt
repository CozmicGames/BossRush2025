package com.cozmicgames.graphics.particles

import com.cozmicgames.Game
import com.cozmicgames.graphics.particles.effects.*
import com.littlekt.graphics.g2d.SpriteBatch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class ParticleManager {
    private class RunningSystem(val id: String? = null, val effect: ParticleEffect) {
        var duration = 0.0.seconds
    }

    private val effectSuppliers = hashMapOf<String, () -> ParticleEffect>()

    private val systems = arrayListOf<RunningSystem>()
    private val systemsToAdd = arrayListOf<ParticleEffect>()
    private val systemsToRemove = arrayListOf<RunningSystem>()

    init {
        effectSuppliers["DEATH_SPLATTER"] = { DeathSplatterEffect() }
        effectSuppliers["SHOOT_SINGLE"] = { SingleShotEffect() }
        effectSuppliers["SHOOT_CONTINUOUS"] = { ContinuousShotEffect() }
        effectSuppliers["TRAIL"] = { TrailEffect() }
    }

    fun add(effect: ParticleEffect) {
        effect.id = Game.random.nextLong().toString()
        systemsToAdd += effect
    }

    fun remove(effect: ParticleEffect) {
        val system = systems.find { it.effect == effect }
        if (system != null)
            systemsToRemove += system
    }

    fun remove(id: String) {
        val system = systems.find { it.id == id }
        if (system != null)
            systemsToRemove += system
    }

    fun update(delta: Duration) {
        if (systemsToAdd.isNotEmpty()) {
            val toAddBuilder = StringBuilder()

            systemsToAdd.forEachIndexed { index, effect ->
                systems += RunningSystem(effect.id, effect)
                toAddBuilder.append(effect.name)
                toAddBuilder.append(":")
                toAddBuilder.append(effect.id)

                if (index < systemsToAdd.lastIndex)
                    toAddBuilder.append(";")
            }
            systemsToAdd.clear()
        }

        for (system in systems) {
            system.effect.update(delta)

            system.duration += delta
            val systemDuration = system.effect.duration

            if (system.effect.shouldBeRemoved || (systemDuration != null && system.duration >= systemDuration))
                systemsToRemove += system
        }

        systems -= systemsToRemove
        systemsToRemove.clear()
    }

    fun render(batch: SpriteBatch) {
        systems.forEach {
            it.effect.render(batch)
        }
    }
}