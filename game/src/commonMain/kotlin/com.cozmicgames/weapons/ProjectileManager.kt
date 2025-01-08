package com.cozmicgames.weapons

import com.cozmicgames.Game
import com.cozmicgames.entities.Entity
import com.cozmicgames.events.Events
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.Hittable
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
            if (projectile.type.baseType is BeamProjectileType && projectile.type.baseType.getLifetime(projectile.distance) <= 0.0f) {
                projectilesToRemove += projectile
                continue

            }

            var distance = projectile.speed * delta.seconds

            val filter = { checkCollider: Collider -> checkCollider.userData != projectile.fromEntity }

            val nearestCollider = Game.physics.getNearestLineCollision(projectile.startX, projectile.startY, projectile.startX + projectile.directionX * distance, projectile.startY + projectile.directionY * distance, filter) { collisionDistance ->
                distance = collisionDistance
            }

            if (nearestCollider != null) {
                if (nearestCollider.userData is Hittable) {
                    val impactX = projectile.currentX + projectile.directionX * distance
                    val impactY = projectile.currentY + projectile.directionY * distance

                    Game.events.addSendEvent(Events.hit(nearestCollider.userData.id, impactX, impactY))
                }

                projectilesToRemove += projectile
                continue
            }

            if (projectile.currentX < Game.physics.minX - 10000.0f || projectile.currentX > Game.physics.maxX + 10000.0f || projectile.currentY < Game.physics.minY - 10000.0f || projectile.currentY > Game.physics.maxY + 10000.0f) {
                projectilesToRemove += projectile
                continue
            }

            projectile.currentX += projectile.directionX * distance
            projectile.currentY += projectile.directionY * distance
        }

        projectiles -= projectilesToRemove

        Game.players.setGlobalState("renderProjectileCount", projectiles.size)

        projectiles.forEachIndexed { index, projectile ->
            Game.players.setGlobalState("renderProjectileType$index", projectile.type.ordinal)
            if (projectile.type == ProjectileType.ENERGY_BEAM) {
                Game.players.setGlobalState("renderProjectileStartX$index", projectile.startX)
                Game.players.setGlobalState("renderProjectileStartY$index", projectile.startY)
            }
            Game.players.setGlobalState("renderProjectileCurrentX$index", projectile.currentX)
            Game.players.setGlobalState("renderProjectileCurrentY$index", projectile.currentY)
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
            val projectileX = Game.players.getGlobalState<Float>("renderProjectileCurrentX$index") ?: continue
            val projectileY = Game.players.getGlobalState<Float>("renderProjectileCurrentY$index") ?: continue

            when (val baseType = projectileType.baseType) {
                is BulletProjectileType -> baseType.render(batch, projectileX, projectileY)
                is BeamProjectileType -> {
                    val startX = Game.players.getGlobalState<Float>("renderProjectileStartX$index") ?: continue
                    val startY = Game.players.getGlobalState<Float>("renderProjectileStartY$index") ?: continue

                    baseType.render(batch, startX, startY, projectileX, projectileY)
                }
            }
        }
    }
}