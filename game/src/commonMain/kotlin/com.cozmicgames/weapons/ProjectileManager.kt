package com.cozmicgames.weapons

import com.cozmicgames.Game
import com.cozmicgames.entities.Entity
import com.cozmicgames.events.Events
import com.cozmicgames.physics.Collider
import com.littlekt.graphics.g2d.SpriteBatch
import com.littlekt.util.seconds
import kotlin.time.Duration

class ProjectileManager {
    private val projectiles = arrayListOf<Projectile>()

    fun update(delta: Duration) {
        if (!Game.players.isHost)
            return

        val projectilesToRemove = arrayListOf<Projectile>()

        for (projectile in projectiles) {
            var distance = projectile.speed * delta.seconds

            val filter = { checkCollider: Collider -> checkCollider.userData != projectile.fromEntity }

            val nearestCollider = Game.physics.getNearestLineCollision(projectile.x, projectile.y, projectile.x + projectile.directionX * distance, projectile.y + projectile.directionY * distance, filter) { collisionDistance ->
                distance = collisionDistance
            }

            if (nearestCollider != null) {
                if (nearestCollider.userData is Entity) {
                    val impactX = projectile.x + projectile.directionX * distance
                    val impactY = projectile.y + projectile.directionY * distance

                    Game.events.addSendEvent(Events.hit(nearestCollider.userData, impactX, impactY))
                }

                projectilesToRemove += projectile
                continue
            }

            if (projectile.x < Game.physics.minX - 10000.0f || projectile.x > Game.physics.maxX + 10000.0f || projectile.y < Game.physics.minY - 10000.0f || projectile.y > Game.physics.maxY + 10000.0f) {
                projectilesToRemove += projectile
                continue
            }

            projectile.x += projectile.directionX * distance
            projectile.y += projectile.directionY * distance
            projectile.collider.x = projectile.x
            projectile.collider.y = projectile.y
        }

        projectiles -= projectilesToRemove

        Game.players.setGlobalState("renderProjectileCount", projectiles.size)

        projectiles.forEachIndexed { index, projectile ->
            Game.players.setGlobalState("renderProjectileType$index", projectile.type.ordinal)
            Game.players.setGlobalState("renderProjectileX$index", projectile.x)
            Game.players.setGlobalState("renderProjectileY$index", projectile.y)
        }
    }

    fun spawnProjectile(fromEntity: Entity, type: ProjectileType, x: Float, y: Float, directionX: Float, directionY: Float, speed: Float) {
        if (!Game.players.isHost)
            return

        projectiles += Projectile(fromEntity, type, x, y, directionX, directionY, speed)
    }

    fun render(batch: SpriteBatch) {
        val projectileCount = Game.players.getGlobalState<Int>("renderProjectileCount") ?: return

        for (index in 0 until projectileCount) {
            val projectileType = ProjectileType.entries.getOrNull(Game.players.getGlobalState("renderProjectileType$index") ?: -1) ?: continue
            val projectileX = Game.players.getGlobalState<Float>("renderProjectileX$index") ?: continue
            val projectileY = Game.players.getGlobalState<Float>("renderProjectileY$index") ?: continue

            batch.draw(projectileType.texture, projectileX, projectileY, originX = projectileType.size * 0.5f, originY = projectileType.size * 0.5f, width = projectileType.size, height = projectileType.size)
        }
    }
}