package com.cozmicgames.weapons

import com.cozmicgames.entities.Entity
import com.littlekt.math.geom.Angle
import kotlin.math.sqrt

class Projectile(val fromEntity: Entity, val type: ProjectileType, var startX: Float, var startY: Float, var direction: Angle, val speed: Float) {
    var distance = 0.0f
        private set

    var currentX = startX
    var currentY = startY

    fun updateDistance() {
        val dx = currentX - startX
        val dy = currentY - startY
        distance = sqrt(dx * dx + dy * dy)
    }
}
