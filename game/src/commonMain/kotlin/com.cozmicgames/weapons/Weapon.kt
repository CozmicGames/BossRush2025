package com.cozmicgames.weapons

import com.littlekt.graphics.Texture
import com.littlekt.math.geom.Angle
import kotlin.time.Duration

interface Weapon {
    val previewTexture: Texture
    val price: Int
    val displayName: String
    val fireRate: Duration
    val canContinoousFire: Boolean
    val projectileCount: Int
    val projectileType: ProjectileType
    val projectileSpeed: Float
    val projectileSpeedFalloff: Float
    val spread: Angle
    val isRandomSpread: Boolean
}
