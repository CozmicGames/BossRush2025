package com.cozmicgames.weapons

import com.cozmicgames.Game
import com.littlekt.graphics.g2d.SpriteBatch
import com.littlekt.util.seconds
import kotlin.time.Duration

class ProjectileManager {
    private val projectiles = arrayListOf<Projectile>()

    fun update(delta: Duration) {
        val projectilesToRemove = arrayListOf<Projectile>()

        projectiles.forEach {
            it.x += it.directionX * it.speed * delta.seconds
            it.y += it.directionY * it.speed * delta.seconds

            if (it.x < Game.physics.minX - 10000.0f || it.x > Game.physics.maxX + 10000.0f || it.y < Game.physics.minY - 10000.0f || it.y > Game.physics.maxY + 10000.0f)
                projectilesToRemove += it
        }

        projectiles -= projectilesToRemove

        Game.players.setGlobalState("projectileCount", projectiles.size)

        projectiles.forEachIndexed { index, projectile ->
            Game.players.setGlobalState("projectileType$index", projectile.type.ordinal)
            Game.players.setGlobalState("projectileX$index", projectile.x)
            Game.players.setGlobalState("projectileY$index", projectile.y)
        }
    }

    fun spawnProjectile(type: ProjectileType, x: Float, y: Float, directionX: Float, directionY: Float, speed: Float) {
        projectiles += Projectile(type, x, y, directionX, directionY, speed)
    }

    fun render(batch: SpriteBatch) {
        val projectileCount = Game.players.getGlobalState<Int>("projectileCount") ?: return

        for (index in 0 until projectileCount) {
            val projectileType = ProjectileType.entries.getOrNull(Game.players.getGlobalState("projectileType$index") ?: -1) ?: continue
            val projectileX = Game.players.getGlobalState<Float>("projectileX$index") ?: continue
            val projectileY = Game.players.getGlobalState<Float>("projectileY$index") ?: continue

            batch.draw(projectileType.texture, projectileX, projectileY, originX = projectileType.size * 0.5f, originY = projectileType.size * 0.5f, width = projectileType.size, height = projectileType.size)
        }
    }
}