package com.cozmicgames.weapons

import com.cozmicgames.Game
import com.littlekt.graphics.Texture

enum class ProjectileType {
    ENERGY_BALL {
        override val texture get() = Game.resources.testEnergyBall
        override val size = 16.0f
    }
    ;

    abstract val texture: Texture
    abstract val size: Float
}