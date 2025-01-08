package com.cozmicgames.weapons

import com.cozmicgames.entities.Entity
import kotlin.math.sqrt

class Projectile(val fromEntity: Entity, val type: ProjectileType, var startX: Float, var startY: Float, val directionX: Float, val directionY: Float, val speed: Float) {
    val distance: Float
        get() {
            val dx = currentX - startX
            val dy = currentY - startY
            return sqrt(dx * dx + dy * dy)
        }

    var currentX = startX
    var currentY = startY
}
