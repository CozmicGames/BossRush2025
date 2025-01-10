package com.cozmicgames.weapons

import com.cozmicgames.entities.worldObjects.AreaEffectSource
import com.cozmicgames.physics.CircleCollisionShape
import com.cozmicgames.physics.Collider
import com.littlekt.util.seconds
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class AreaEffect(val fromSource: AreaEffectSource, val type: AreaEffectType, val sourceType: AreaEffectSourceType, val growthType: AreaEffectGrowthType, var sourceX: Float, var sourceY: Float, var radius: Float, var growRate: Float, var duration: Duration) {
    val collider = Collider(CircleCollisionShape(radius))
    val hitColliders = arrayListOf<Collider>()

    var timer = 0.0.seconds
        private set

    fun update(delta: Duration): Boolean {
        timer += delta

        when (sourceType) {
            AreaEffectSourceType.STATIC -> {}
            AreaEffectSourceType.MOVING -> {
                sourceX = fromSource.effectSourceX
                sourceY = fromSource.effectSourceY
            }
        }

        when (growthType) {
            AreaEffectGrowthType.NONE -> {}
            AreaEffectGrowthType.LINEAR -> radius += delta.seconds * growRate
            AreaEffectGrowthType.EXPONENTIAL -> radius += delta.seconds * growRate * radius
        }

        collider.x = sourceX
        collider.y = sourceY
        (collider.shape as CircleCollisionShape).radius = radius
        collider.update()

        return timer >= duration
    }
}
