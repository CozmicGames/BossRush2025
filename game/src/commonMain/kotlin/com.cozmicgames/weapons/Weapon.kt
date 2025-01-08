package com.cozmicgames.weapons

import com.littlekt.math.geom.Angle

abstract class Weapon {
    abstract val name: String
    abstract val damage: Int
    abstract val fireRate: Float
    abstract val canContinoousFire: Boolean
    abstract val projectileType: ProjectileType
    abstract val projectileSpeed: Float
    abstract val spread: Angle
}
