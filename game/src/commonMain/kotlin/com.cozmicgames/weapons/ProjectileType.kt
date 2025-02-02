package com.cozmicgames.weapons

import com.cozmicgames.Game
import com.cozmicgames.graphics.particles.ParticleEffect
import com.cozmicgames.graphics.particles.effects.ContinuousShotEffect
import com.cozmicgames.graphics.particles.effects.SingleShotEffect
import com.littlekt.graphics.Color
import com.littlekt.graphics.g2d.SpriteBatch
import com.littlekt.graphics.g2d.TextureSlice
import com.littlekt.math.clamp
import com.littlekt.math.geom.Angle
import com.littlekt.math.geom.radians
import kotlin.math.atan2
import kotlin.math.sqrt

enum class ProjectileType(val baseType: ProjectileBaseType, val stunColor: Color, val killColor: Color) {
    ENERGY_BALL(BulletProjectileType({ Game.textures.energyBall }, 16.0f), Color.fromHex("94fdff"), Color.fromHex("e7211d")) {
        override fun createParticleEffect(x: Float, y: Float, direction: Angle, isStun: Boolean): ParticleEffect {
            return SingleShotEffect(x, y, direction, if (isStun) stunColor else killColor)
        }
    },
    ENERGY_BEAM(BeamProjectileType(1000.0f) { Game.textures.energyBeam }, Color.fromHex("94fdff"), Color.fromHex("e7211d")) {
        override fun createParticleEffect(x: Float, y: Float, direction: Angle, isStun: Boolean): ParticleEffect {
            return ContinuousShotEffect(x, y, direction, if (isStun) stunColor else killColor)
        }
    },
    BAIT_BALL(BulletProjectileType({ Game.textures.baitBall }, 24.0f), Color.fromHex("ffd59b"), Color.fromHex("ffd59b")) {
        override fun createParticleEffect(x: Float, y: Float, direction: Angle, isStun: Boolean): ParticleEffect {
            return SingleShotEffect(x, y, direction, if (isStun) stunColor else killColor)
        }
    },
    SHOCK_MINE(BulletProjectileType({ Game.textures.energyBall }, 16.0f), Color.fromHex("5ac54f"), Color.fromHex("fdd2ed")) {
        override fun createParticleEffect(x: Float, y: Float, direction: Angle, isStun: Boolean): ParticleEffect {
            return SingleShotEffect(x, y, direction, if (isStun) stunColor else killColor)
        }
    };

    abstract fun createParticleEffect(x: Float, y: Float, direction: Angle, isStun: Boolean): ParticleEffect
}

sealed interface ProjectileBaseType

class BulletProjectileType(val textureGetter: () -> TextureSlice, val size: Float) : ProjectileBaseType {
    fun render(batch: SpriteBatch, x: Float, y: Float, color: Color) {
        batch.draw(textureGetter(), x, y, originX = size * 0.5f, originY = size * 0.5f, width = size, height = size, color = color)
    }
}

class BeamProjectileType(val maxDistance: Float, val textureGetter: () -> TextureSlice) : ProjectileBaseType {
    fun getLifetime(distance: Float) = (1.0f - distance / maxDistance).clamp(0.0f, 1.0f)

    fun render(batch: SpriteBatch, startX: Float, startY: Float, x: Float, y: Float, color: Color) {
        val texture = textureGetter()

        val centerX = (startX + x) * 0.5f
        val centerY = (startY + y) * 0.5f

        val dx = x - startX
        val dy = y - startY
        val width = sqrt(dx * dx + dy * dy)

        val height = texture.height.toFloat()
        val angle = atan2(y - startY, x - startX).radians

        batch.draw(texture, centerX, centerY, width * 0.5f, height * 0.5f, width, height, rotation = angle, color = color)
    }
}
