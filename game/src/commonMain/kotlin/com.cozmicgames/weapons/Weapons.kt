package com.cozmicgames.weapons

import com.littlekt.math.geom.degrees
import kotlin.time.Duration.Companion.seconds

enum class Weapons : Weapon {
    ENERGY_GUN {
        override val displayName = "Energy Gun"
        override val fireRate = 0.2.seconds
        override val canContinoousFire = true
        override val projectileType = ProjectileType.ENERGY_BALL
        override val projectileCount = 1
        override val projectileSpeed = 500.0f
        override val spread = 10.0.degrees
        override val isRandomSpread = true
    },
    ENERGY_HARPOON {
        override val displayName = "Energy Harpoon"
        override val fireRate = 0.5.seconds
        override val canContinoousFire = true
        override val projectileType = ProjectileType.ENERGY_BEAM
        override val projectileCount = 1
        override val projectileSpeed = 1500.0f
        override val spread = 0.0.degrees
        override val isRandomSpread = false
    },
    ENERGY_SHOTGUN {
        override val displayName = "Energy Shotgun"
        override val fireRate = 0.8.seconds
        override val canContinoousFire = true
        override val projectileType = ProjectileType.ENERGY_BALL
        override val projectileCount = 5
        override val projectileSpeed = 300.0f
        override val spread = 30.0.degrees
        override val isRandomSpread = false
    }
}