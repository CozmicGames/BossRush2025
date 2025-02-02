package com.cozmicgames.weapons

import com.cozmicgames.Game
import com.cozmicgames.bosses.BossHittable
import com.cozmicgames.bosses.BossTarget
import com.cozmicgames.entities.worldObjects.AreaEffectSource
import com.cozmicgames.entities.worldObjects.ProjectileSource
import com.cozmicgames.graphics.particles.ParticleEffect
import com.cozmicgames.graphics.particles.effects.ShootEffect
import com.cozmicgames.physics.CircleCollisionShape
import com.cozmicgames.physics.Collider
import com.littlekt.math.geom.Angle
import kotlin.math.sqrt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class Projectile(val fromSource: ProjectileSource, val type: ProjectileType, var startX: Float, var startY: Float, var direction: Angle, var speed: Float, val speedFalloff: Float, val isStunMode: Boolean) : AreaEffectSource {
    private inner class BaitTarget : BossTarget {
        override val id = "BaitBall${fromSource.projectileSourceId}"

        override val x get() = currentX

        override val y get() = currentY

        override val appeal = 100

        val collider = Collider(CircleCollisionShape((type.baseType as BulletProjectileType).size * 0.5f), this)

        fun checkCollision() {
            Game.physics.checkCollision(collider, { it != collider }) {
                if (it.userData is BossHittable) {
                    it.userData.boss.paralyze()
                    Game.projectiles.removeProjectile(this@Projectile)
                }
            }
        }
    }

    var distance = 0.0f
        private set

    var currentX = startX
    var currentY = startY

    var particleEffect: ParticleEffect? = null

    private val bossTarget: BaitTarget? = if (type == ProjectileType.BAIT_BALL)
        BaitTarget()
    else
        null

    private var areaEffectTimer = 0.0.seconds
    private var lifeTimer = 0.0.seconds
    private var setLifeTimer = false

    override val effectSourceX get() = currentX
    override val effectSourceY get() = currentY


    fun onAdded() {
        val target = bossTarget ?: return

        Game.physics.addCollider(target.collider)
        Game.physics.addHittable(target)
        Game.world.addBossTarget(target)
    }

    fun onUpdate(delta: Duration) {
        val dx = currentX - startX
        val dy = currentY - startY
        distance = sqrt(dx * dx + dy * dy)

        bossTarget?.collider?.update(currentX, currentY)
        bossTarget?.checkCollision()

        (particleEffect as? ShootEffect)?.let {
            it.x = startX
            it.y = startY
            it.direction = direction
        }

        if ((type == ProjectileType.SHOCK_MINE || type == ProjectileType.BAIT_BALL) && speed < 10.0f) {
            if (!setLifeTimer) {
                lifeTimer = when (type) {
                    ProjectileType.SHOCK_MINE -> 30.0.seconds
                    ProjectileType.BAIT_BALL -> 45.0.seconds
                    else -> 0.0.seconds
                }
                setLifeTimer = true
            }

            areaEffectTimer -= delta

            if (areaEffectTimer <= 0.0.seconds) {
                when (type) {
                    ProjectileType.SHOCK_MINE -> {
                        Game.areaEffects.spawnEffect(this, AreaEffectType.SHOCKWAVE, AreaEffectSourceType.MOVING, AreaEffectGrowthType.LINEAR, 24.0f, 60.0f, 3.5.seconds)
                        areaEffectTimer = 2.0.seconds
                    }

                    ProjectileType.BAIT_BALL -> {
                        Game.areaEffects.spawnEffect(this, AreaEffectType.BAIT, AreaEffectSourceType.MOVING, AreaEffectGrowthType.LINEAR, 16.0f, 20.0f, 1.0.seconds)
                        areaEffectTimer = 0.7.seconds
                    }

                    else -> {}
                }
            }

            lifeTimer -= delta
            if (lifeTimer <= 0.0.seconds)
                Game.projectiles.removeProjectile(this)
        }
    }

    fun onRemove() {
        particleEffect?.setShouldBeRemoved()

        val target = bossTarget ?: return

        Game.physics.removeCollider(target.collider)
        Game.physics.removeHittable(target)
        Game.world.removeBossTarget(target)
    }
}
