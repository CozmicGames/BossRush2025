package com.cozmicgames.weapons

import com.littlekt.math.geom.Angle

abstract class Weapon {
    abstract val name: String
    abstract val fireRate: Float
    abstract val canContinoousFire: Boolean
    abstract val projectileCount: Int
    abstract val projectileType: ProjectileType
    abstract val projectileSpeed: Float
    abstract val spread: Angle
    abstract val isRandomSpread: Boolean
}
