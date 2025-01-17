package com.cozmicgames.weapons

import com.cozmicgames.Game
import com.littlekt.graphics.Texture
import com.littlekt.graphics.g2d.SpriteBatch
import com.littlekt.math.clamp
import com.littlekt.math.geom.radians
import kotlin.math.atan2
import kotlin.math.sqrt

enum class ProjectileType(val baseType: ProjectileBaseType) {
    ENERGY_BALL(BulletProjectileType({ Game.resources.energyBall }, 16.0f)),
    ENERGY_BEAM(BeamProjectileType(1000.0f) { Game.resources.energyBeam }),
    BAIT_BALL(BulletProjectileType({ Game.resources.baitBall }, 24.0f))
}

sealed interface ProjectileBaseType

class BulletProjectileType(val textureGetter: () -> Texture, val size: Float) : ProjectileBaseType {
    fun render(batch: SpriteBatch, x: Float, y: Float) {
        batch.draw(textureGetter(), x, y, originX = size * 0.5f, originY = size * 0.5f, width = size, height = size)
    }
}

class BeamProjectileType(val maxDistance: Float, val textureGetter: () -> Texture) : ProjectileBaseType {
    fun getLifetime(distance: Float) = (1.0f - distance / maxDistance).clamp(0.0f, 1.0f)

    fun render(batch: SpriteBatch, startX: Float, startY: Float, x: Float, y: Float) {
        val texture = textureGetter()

        val centerX = (startX + x) * 0.5f
        val centerY = (startY + y) * 0.5f

        val dx = x - startX
        val dy = y - startY
        val width = sqrt(dx * dx + dy * dy)

        val height = texture.height.toFloat()
        val angle = atan2(y - startY, x - startX).radians

        batch.draw(texture, centerX, centerY, width * 0.5f, height * 0.5f, width, height, rotation = angle)
    }
}
