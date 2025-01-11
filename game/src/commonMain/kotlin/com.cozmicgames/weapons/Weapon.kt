package com.cozmicgames.weapons

import com.littlekt.math.geom.Angle
import kotlin.time.Duration

interface Weapon {
    val displayName: String
    val fireRate: Duration
    val canContinoousFire: Boolean
    val projectileCount: Int
    val projectileType: ProjectileType
    val projectileSpeed: Float
    val spread: Angle
    val isRandomSpread: Boolean
}
