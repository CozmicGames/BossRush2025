package com.cozmicgames.graphics.particles

import com.cozmicgames.Game
import com.cozmicgames.utils.createInstance
import com.cozmicgames.utils.getClassByName
import com.littlekt.graphics.g2d.SpriteBatch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class ParticleManager {
    private class RunningSystem(val id: String? = null, val effect: ParticleEffect) {
        var duration = 0.0.seconds
    }

    private val systems = arrayListOf<RunningSystem>()
    private val systemsToAdd = arrayListOf<ParticleEffect>()
    private val systemsToRemove = arrayListOf<RunningSystem>()

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
        if (Game.players.isHost) {
            if (systemsToAdd.isNotEmpty()) {
                val toAddBuilder = StringBuilder()

                systemsToAdd.forEachIndexed { index, effect ->
                    systems += RunningSystem(effect.id, effect)
                    toAddBuilder.append(effect::class)
                    toAddBuilder.append(":")
                    toAddBuilder.append(effect.id)

                    if (index < systemsToAdd.lastIndex)
                        toAddBuilder.append(";")
                }
                systemsToAdd.clear()

                Game.players.setGlobalState("particleEffectsToAdd", toAddBuilder.toString())
            } else
                Game.players.setGlobalState("particleEffectsToAdd", "")

            for (system in systems) {
                system.effect.update(delta)
                system.effect.writeUpdateData()

                system.duration += delta
                val systemDuration = system.effect.duration

                if (system.effect.shouldBeRemoved || (systemDuration != null && system.duration >= systemDuration))
                    systemsToRemove += system
            }

            systems -= systemsToRemove
            systemsToRemove.clear()
        } else {
            val toAdd = Game.players.getGlobalState("particleEffectsToAdd") ?: ""
            val effectDescriptors = toAdd.split(";")

            for (desc in effectDescriptors) {
                val parts = desc.split(":")
                if (parts.size != 2)
                    continue

                val typeName = parts[0]
                val id = parts[1]

                val type = getClassByName(typeName) ?: continue
                val effect = createInstance(type) as? ParticleEffect ?: continue

                effect.id = id

                systems += RunningSystem(id, effect)
            }

            for (system in systems) {
                system.effect.readUpdateData()

                system.duration += delta
                val systemDuration = system.effect.duration

                if (system.effect.shouldBeRemoved || systemDuration != null && system.duration >= systemDuration) {
                    systemsToRemove += system
                    continue
                }
            }

            systems -= systemsToRemove
            systemsToRemove.clear()
        }
    }

    fun render(batch: SpriteBatch) {
        systems.forEach {
            it.effect.render(batch)
        }
    }
}