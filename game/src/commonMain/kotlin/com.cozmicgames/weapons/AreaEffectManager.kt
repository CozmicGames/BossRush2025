package com.cozmicgames.weapons

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.AreaEffectSource
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.Hittable
import com.cozmicgames.utils.Easing
import com.littlekt.graphics.g2d.SpriteBatch
import com.littlekt.math.clamp
import kotlin.time.Duration

class AreaEffectManager {
    private val areaEffects = arrayListOf<AreaEffect>()

    fun update(delta: Duration) {
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
                if (it.userData is Hittable && areaEffect.fromSource.shouldHitWithAreaEffect(it.userData.id)) {
                    val factor = (areaEffect.timer / areaEffect.duration).toFloat()
                    val strength = areaEffect.growRate * factor * 0.2f

                    val dx = it.x - areaEffect.sourceX
                    val dy = it.y - areaEffect.sourceY

                    when (areaEffect.type) {
                        AreaEffectType.SHOCKWAVE -> it.userData.onImpulseHit(dx, dy, strength)
                        AreaEffectType.SHOCKWAVE_WITH_DAMAGE -> {
                            it.userData.onImpulseHit(dx, dy, strength)
                            if (it !in areaEffect.hitColliders) {
                                it.userData.onDamageHit()
                                areaEffect.hitColliders += it
                            }
                        }

                        AreaEffectType.GRAVITY_WAVE -> {
                            it.userData.onImpulseHit(-dx, -dy, strength)
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    fun spawnEffect(fromSource: AreaEffectSource, type: AreaEffectType, sourceType: AreaEffectSourceType, growthType: AreaEffectGrowthType, radius: Float, growRate: Float, duration: Duration) {
        areaEffects += AreaEffect(fromSource, type, sourceType, growthType, fromSource.effectSourceX, fromSource.effectSourceY, radius, growRate, duration)
    }

    fun render(batch: SpriteBatch) {
        areaEffects.forEachIndexed { index, areaEffect ->
            val strength = (areaEffect.timer / areaEffect.duration).toFloat()
            val areaEffectAlpha = 1.0f - Easing.CUBIC_OUT(strength).clamp(0.0f, 1.0f)

            val areaEffectType = areaEffect.type
            val areaEffectX = areaEffect.sourceX
            val areaEffectY = areaEffect.sourceY
            val areaEffectRadius = areaEffect.radius

            areaEffectType.render(batch, areaEffectX, areaEffectY, areaEffectRadius, areaEffectAlpha)
        }
    }
}