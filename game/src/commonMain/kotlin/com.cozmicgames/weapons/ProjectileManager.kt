package com.cozmicgames.weapons

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.PlayerShip
import com.cozmicgames.entities.worldObjects.ProjectileSource
import com.cozmicgames.graphics.particles.ParticleEffect
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
    private val projectilesToRemove = arrayListOf<Projectile>()

    fun update(delta: Duration) {
        for (projectile in projectiles) {
            if (projectile.type.baseType is BeamProjectileType) {
                projectile.startX = projectile.fromSource.muzzleX
                projectile.startY = projectile.fromSource.muzzleY
                projectile.direction = projectile.fromSource.muzzleRotation
            }

            val projectileAngle = projectile.direction
            val projectileDirectionX = projectileAngle.cosine
            val projectileDirectionY = projectileAngle.sine

            if (projectile.type.baseType is BeamProjectileType) {
                if (projectile.type.baseType.getLifetime(projectile.distance) <= 0.0f) {
                    projectilesToRemove += projectile
                    continue
                }
            }

            var distance = projectile.speed * delta.seconds
            projectile.speed *= 1.0f - projectile.speedFalloff * delta.seconds

            val filter = { checkCollider: Collider ->
                checkCollider.userData != projectile.fromSource && (checkCollider.userData as? ProjectileSource)?.projectileSourceId != projectile.fromSource.projectileSourceId
            }

            val nearestCollider = when (projectile.type.baseType) {
                is BulletProjectileType -> Game.physics.getNearestLineCollision(projectile.currentX, projectile.currentY, projectile.currentX + projectileDirectionX * distance, projectile.currentY + projectileDirectionY * distance, filter) { collisionDistance ->
                    distance = collisionDistance
                }

                is BeamProjectileType -> Game.physics.getNearestLineCollision(projectile.startX, projectile.startY, projectile.startX + projectileDirectionX * (projectile.distance + distance), projectile.startY + projectileDirectionY * (projectile.distance + distance), filter) { collisionDistance ->
                    distance = collisionDistance
                }
            }

            if (nearestCollider != null) {
                if (nearestCollider.userData is Hittable && nearestCollider.userData.canBeHit) {
                    if(projectile.type == ProjectileType.BAIT_BALL)
                        nearestCollider.userData.onBaitHit()
                    else
                        nearestCollider.userData.onDamageHit()
                }

                if (projectile.fromSource is PlayerShip)
                    Game.player.shootStatistics.shotsHit++

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

            projectile.onUpdate(delta)
        }

        projectilesToRemove.forEach {
            it.onRemove()
        }
        projectiles -= projectilesToRemove
        projectilesToRemove.clear()
    }

    fun stopBeamProjectile(fromSource: ProjectileSource) {
        projectilesToRemove += projectiles.filter { it.fromSource == fromSource && it.type == ProjectileType.ENERGY_BEAM }
    }

    fun spawnProjectile(fromSource: ProjectileSource, type: ProjectileType, x: Float, y: Float, direction: Angle, speed: Float, speedFalloff: Float, withParticleEffect: Boolean = true) {
        var particleEffect: ParticleEffect? = null

        if (withParticleEffect) {
            particleEffect = type.createParticleEffect(x, y, direction, fromSource.isStunMode)
            Game.particles.add(particleEffect)
        }

        val projectile = Projectile(fromSource, type, x, y, direction, speed, speedFalloff)
        projectile.particleEffect = particleEffect
        projectile.onAdded()
        projectiles += projectile
    }

    fun removeProjectile(projectile: Projectile) {
        projectiles -= projectile
        projectile.onRemove()
    }

    fun render(batch: SpriteBatch) {
        projectiles.forEach {
            val type = it.type
            val x = it.currentX
            val y = it.currentY

            when (val baseType = type.baseType) {
                is BulletProjectileType -> baseType.render(batch, x, y)
                is BeamProjectileType -> {
                    val startX = it.startX
                    val startY = it.startY

                    baseType.render(batch, startX, startY, x, y)
                }
            }
        }
    }
}