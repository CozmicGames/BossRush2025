package com.cozmicgames.weapons

import com.cozmicgames.entities.Entity
import com.cozmicgames.physics.CircleCollisionShape
import com.cozmicgames.physics.Collider

class Projectile(val fromEntity: Entity, val type: ProjectileType, var x: Float, var y: Float, val directionX: Float, val directionY: Float, val speed: Float) {
    val collider = Collider(CircleCollisionShape(type.size * 0.5f))

    init {
        collider.x = x
        collider.y = y
    }
}