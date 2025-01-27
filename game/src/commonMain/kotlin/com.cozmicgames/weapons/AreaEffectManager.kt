package com.cozmicgames.weapons

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.AreaEffectSource
import com.cozmicgames.events.Events
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.Hittable
import com.cozmicgames.utils.Easing
import com.littlekt.graphics.g2d.SpriteBatch
import com.littlekt.math.clamp
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
                    val factor = (areaEffect.timer / areaEffect.duration).toFloat()
                    val strength = areaEffect.growRate * factor * 0.2f

                    val dx = it.x - areaEffect.sourceX
                    val dy = it.y - areaEffect.sourceY

                    when (areaEffect.type) {
                        AreaEffectType.SHOCKWAVE -> Game.events.addSendEvent(Events.impulseHit(it.userData.id, dx, dy, strength))
                        AreaEffectType.SHOCKWAVE_WITH_DAMAGE -> {
                            Game.events.addSendEvent(Events.impulseHit(it.userData.id, dx, dy, strength))
                            if (it !in areaEffect.hitColliders) {
                                Game.events.addSendEvent(Events.hit(it.userData.id))
                                areaEffect.hitColliders += it
                            }
                        }

                        AreaEffectType.GRAVITY_WAVE -> {
                            Game.events.addSendEvent(Events.impulseHit(it.userData.id, -dx, -dy, strength))
                        }

                        else -> {}
                    }
                }
            }
        }

        Game.players.setGlobalState("renderAreaEffectsCount", areaEffects.size)

        areaEffects.forEachIndexed { index, areaEffect ->
            val strength = (areaEffect.timer / areaEffect.duration).toFloat()
            val alpha = 1.0f - Easing.CUBIC_OUT(strength).clamp(0.0f, 1.0f)

            Game.players.setGlobalState("renderAreaEffectType$index", areaEffect.type.ordinal)
            Game.players.setGlobalState("renderAreaEffectX$index", areaEffect.sourceX)
            Game.players.setGlobalState("renderAreaEffectY$index", areaEffect.sourceY)
            Game.players.setGlobalState("renderAreaEffectRadius$index", areaEffect.radius)
            Game.players.setGlobalState("renderAreaEffectAlpha$index", alpha)
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
            val areaEffectAlpha = Game.players.getGlobalState<Float>("renderAreaEffectAlpha$index") ?: continue

            areaEffectType.render(batch, areaEffectX, areaEffectY, areaEffectRadius, areaEffectAlpha)
        }
    }
}