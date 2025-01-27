package com.cozmicgames.weapons

import com.cozmicgames.Game
import com.cozmicgames.bosses.BossTarget
import com.cozmicgames.entities.worldObjects.ProjectileSource
import com.cozmicgames.physics.CircleCollisionShape
import com.cozmicgames.physics.Collider
import com.littlekt.math.geom.Angle
import kotlin.math.sqrt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class Projectile(val fromSource: ProjectileSource, val type: ProjectileType, var startX: Float, var startY: Float, var direction: Angle, var speed: Float, val speedFalloff: Float) {
    private inner class BaitTarget : BossTarget {
        var life = 10.0.seconds

        override val id = "BaitBall${fromSource.projectileSourceId}"

        override val x get() = currentX

        override val y get() = currentY

        override val appeal = 100

        val collider = Collider(CircleCollisionShape((type.baseType as BulletProjectileType).size * 0.5f), this)

        override fun onDamageHit() {
            Game.projectiles.removeProjectile(this@Projectile)
        }
    }

    var distance = 0.0f
        private set

    var currentX = startX
    var currentY = startY

    private val bossTarget: BaitTarget? = if (type == ProjectileType.BAIT_BALL)
        BaitTarget()
    else
        null

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

        bossTarget?.let {
            it.collider.update(currentX, currentY)
            it.life -= delta //TODO: Add ripples as graphical effect

            if (it.life <= 0.seconds)
                Game.projectiles.removeProjectile(this)
        }
    }

    fun onRemove() {
        val target = bossTarget ?: return

        Game.physics.removeCollider(target.collider)
        Game.physics.removeHittable(target)
        Game.world.removeBossTarget(target)
    }
}
