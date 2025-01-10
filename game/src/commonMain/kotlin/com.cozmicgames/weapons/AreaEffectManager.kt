package com.cozmicgames.weapons

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.AreaEffectSource
import com.cozmicgames.events.Events
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.Hittable
import com.littlekt.graphics.g2d.SpriteBatch
import kotlin.time.Duration

class AreaEffectManager {
    private val areaEffects = arrayListOf<AreaEffect>()

    fun update(delta: Duration) {
        if (!Game.players.isHost)
            return

        val areaEffectsToRemove = arrayListOf<AreaEffect>()
        areaEffects.forEach {
            if (it.update(delta)) {
                areaEffectsToRemove += it
            }
        }
        areaEffects -= areaEffectsToRemove

        for (areaEffect in areaEffects) {
            val filter = { collider: Collider -> collider.userData != areaEffect.fromSource }

            Game.physics.checkCollision(areaEffect.collider, filter) {
                if (it.userData is Hittable) {
                    val strength = areaEffect.growRate * (areaEffect.timer / areaEffect.duration).toFloat()

                    when (areaEffect.type) {
                        AreaEffectType.SHOCKWAVE -> Game.events.addSendEvent(Events.shockwaveHit(it.userData.id, areaEffect.sourceX, areaEffect.sourceY, strength))
                        AreaEffectType.SHOCKWAVE_WITH_DAMAGE -> {
                            Game.events.addSendEvent(Events.shockwaveHit(it.userData.id, areaEffect.sourceX, areaEffect.sourceY, strength))
                            if (it !in areaEffect.hitColliders) {
                                Game.events.addSendEvent(Events.hit(it.userData.id, areaEffect.sourceX, areaEffect.sourceY))
                                areaEffect.hitColliders += it
                            }
                        }
                    }
                }
            }
        }

        Game.players.setGlobalState("renderAreaEffectsCount", areaEffects.size)

        areaEffects.forEachIndexed { index, areaEffect ->
            Game.players.setGlobalState("renderAreaEffectType$index", areaEffect.type.ordinal)
            Game.players.setGlobalState("renderAreaEffectX$index", areaEffect.sourceX)
            Game.players.setGlobalState("renderAreaEffectY$index", areaEffect.sourceY)
            Game.players.setGlobalState("renderAreaEffectRadius$index", areaEffect.radius)
            Game.players.setGlobalState("renderAreaEffectStrength$index", (areaEffect.timer / areaEffect.duration).toFloat())
        }
    }

    fun spawnEffect(fromSource: AreaEffectSource, type: AreaEffectType, sourceType: AreaEffectSourceType, growthType: AreaEffectGrowthType, radius: Float, growRate: Float, duration: Duration) {
        areaEffects += AreaEffect(fromSource, type, sourceType, growthType, fromSource.effectSourceX, fromSource.effectSourceY, radius, growRate, duration)
    }

    fun render(batch: SpriteBatch) {
        val areaEffectCount = Game.players.getGlobalState<Int>("renderAreaEffectsCount") ?: return

        for (index in 0 until areaEffectCount) {
            val areaEffectType = AreaEffectType.entries.getOrNull(Game.players.getGlobalState("renderAreaEffectType$index") ?: -1) ?: continue
            val areaEffectX = Game.players.getGlobalState<Float>("renderAreaEffectX$index") ?: continue
            val areaEffectY = Game.players.getGlobalState<Float>("renderAreaEffectY$index") ?: continue
            val areaEffectRadius = Game.players.getGlobalState<Float>("renderAreaEffectRadius$index") ?: continue
            val areaEffectStrength = Game.players.getGlobalState<Float>("renderAreaEffectStrength$index") ?: continue

            areaEffectType.render(batch, areaEffectX, areaEffectY, areaEffectRadius, areaEffectStrength)
        }
    }
}