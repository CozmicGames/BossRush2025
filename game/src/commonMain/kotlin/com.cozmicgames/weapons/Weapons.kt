package com.cozmicgames.weapons

import com.cozmicgames.Game
import com.littlekt.math.geom.degrees
import kotlin.time.Duration.Companion.seconds

enum class Weapons : Weapon {
    REELGUN {
        override val previewTexture get() = Game.textures.reelgunPreview
        override val price = 0
        override val displayName = "Reelgun"
        override val fireRate = 0.2.seconds
        override val canContinoousFire = true
        override val projectileType = ProjectileType.ENERGY_BALL
        override val projectileCount = 1
        override val projectileSpeed = 500.0f
        override val projectileSpeedFalloff = 0.0f
        override val spread = 10.0.degrees
        override val isRandomSpread = true
        override val tooltipText = "Fires single energy shots"
    },
    HYPER_HARPOON {
        override val previewTexture get() = Game.textures.hyperHarpoonPreview
        override val price = 600
        override val displayName = "Hyper\nHarpoon"
        override val fireRate = 0.5.seconds
        override val canContinoousFire = true
        override val projectileType = ProjectileType.ENERGY_BEAM
        override val projectileCount = 1
        override val projectileSpeed = 1500.0f
        override val projectileSpeedFalloff = 0.0f
        override val spread = 0.0.degrees
        override val isRandomSpread = false
        override val tooltipText = "A high-power beam weapon"
    },
    SHOCKMINE {
        override val previewTexture get() = Game.textures.shockminePreview
        override val price = 700
        override val displayName = "Shockmine"
        override val fireRate = 10.seconds
        override val canContinoousFire = false
        override val projectileType = ProjectileType.SHOCK_MINE
        override val projectileCount = 1
        override val projectileSpeed = 300.0f
        override val projectileSpeedFalloff = 1.2f
        override val spread = 0.0.degrees
        override val isRandomSpread = false
        override val tooltipText = "Creates a shockwave to\npush monsters away\nLasts for 30 seconds"
    },
    BAITBLASTER {
        override val previewTexture get() = Game.textures.baitblasterPreview
        override val price = 1000
        override val displayName = "Baitblaster"
        override val fireRate = 10.seconds
        override val canContinoousFire = false
        override val projectileType = ProjectileType.BAIT_BALL
        override val projectileCount = 1
        override val projectileSpeed = 300.0f
        override val projectileSpeedFalloff = 1.2f
        override val spread = 0.0.degrees
        override val isRandomSpread = false
        override val tooltipText = "Lures monsters and\nstuns them on impact\nLasts for 45 seconds"
    }
}