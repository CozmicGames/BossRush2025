package com.cozmicgames.weapons

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.PlayerShip
import com.cozmicgames.entities.worldObjects.WorldObject
import com.cozmicgames.entities.worldObjects.ProjectileSource
import com.cozmicgames.events.Events
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.Hittable
import com.littlekt.graphics.g2d.SpriteBatch
import com.littlekt.math.geom.Angle
import com.littlekt.math.geom.cosine
import com.littlekt.math.geom.sine
import com.littlekt.util.seconds
import kotlin.time.Duration

class ProjectileManager {
    private val projectiles = arrayListOf<Projectile>()

    fun update(delta: Duration) {
        if (!Game.players.isHost)
            return

        val projectilesToRemove = arrayListOf<Projectile>()

        for (projectile in projectiles) {
            var projectileAngle = projectile.direction

            if (projectile.fromWorldObject is ProjectileSource && projectile.type.baseType is BeamProjectileType) {
                projectile.startX = projectile.fromWorldObject.muzzleX
                projectile.startY = projectile.fromWorldObject.muzzleY
                projectileAngle += projectile.fromWorldObject.muzzleRotation
            }

            val projectileDirectionX = projectileAngle.cosine
            val projectileDirectionY = projectileAngle.sine

            if (projectile.type.baseType is BeamProjectileType) {
                if (projectile.type.baseType.getLifetime(projectile.distance) <= 0.0f) {
                    projectilesToRemove += projectile
                    continue
                }
            }

            var distance = projectile.speed * delta.seconds

            val filter = { checkCollider: Collider -> checkCollider.userData != projectile.fromWorldObject }

            val nearestCollider = when (projectile.type.baseType) {
                is BulletProjectileType -> Game.physics.getNearestLineCollision(projectile.currentX, projectile.currentY, projectile.currentX + projectileDirectionX * distance, projectile.currentY + projectileDirectionY * distance, filter) { collisionDistance ->
                    distance = collisionDistance
                }

                is BeamProjectileType -> Game.physics.getNearestLineCollision(projectile.startX, projectile.startY, projectile.startX + projectileDirectionX * (projectile.distance + distance), projectile.startY + projectileDirectionY * (projectile.distance + distance), filter) { collisionDistance ->
                    distance = collisionDistance
                }
            }

            if (nearestCollider != null) {
                if (nearestCollider.userData is Hittable && nearestCollider.userData.canHit)
                    Game.events.addSendEvent(Events.hit(nearestCollider.userData.id))

                if (projectile.fromWorldObject is PlayerShip)
                    Game.players.shootStatistics.shotsHit++

                projectilesToRemove += projectile
                continue
            }

            if (projectile.currentX < Game.physics.minX - 100000.0f || projectile.currentX > Game.physics.maxX + 100000.0f || projectile.currentY < Game.physics.minY - 100000.0f || projectile.currentY > Game.physics.maxY + 100000.0f) {
                projectilesToRemove += projectile
                continue
            }

            when (projectile.type.baseType) {
                is BulletProjectileType -> {
                    projectile.currentX += projectileDirectionX * distance
                    projectile.currentY += projectileDirectionY * distance
                }

                is BeamProjectileType -> {
                    projectile.currentX = projectile.startX + projectileDirectionX * (projectile.distance + distance)
                    projectile.currentY = projectile.startY + projectileDirectionY * (projectile.distance + distance)
                }
            }

            projectile.updateDistance()
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

    fun stopBeamProjectile(fromWorldObject: WorldObject) {
        if (!Game.players.isHost)
            return

        projectiles.removeAll { it.fromWorldObject == fromWorldObject && it.type == ProjectileType.ENERGY_BEAM }
    }

    fun spawnProjectile(fromWorldObject: WorldObject, type: ProjectileType, x: Float, y: Float, direction: Angle, speed: Float) {
        if (!Game.players.isHost)
            return

        projectiles += Projectile(fromWorldObject, type, x, y, direction, speed)
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