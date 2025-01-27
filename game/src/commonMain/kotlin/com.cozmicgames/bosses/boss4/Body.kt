package com.cozmicgames.bosses.boss4

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.cozmicgames.physics.CircleCollisionShape
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.Hittable
import com.littlekt.graphics.slice
import com.littlekt.math.geom.cosine
import com.littlekt.math.geom.sine
import kotlin.math.sqrt
import kotlin.time.Duration

class Body(private val boss: Boss4, private val bodyScale: Float, layer: Int) : EnemyPart("boss4body"), Hittable, PlayerDamageSource {
    override val canHit get() = !boss.isInvulnerable

    override val renderLayer = layer

    override val collider = Collider(CircleCollisionShape(width * 0.4f), this)

    override val width get() = Game.resources.boss4body.width * bodyScale * boss.bossScale

    override val height get() = Game.resources.boss4body.height * bodyScale * boss.bossScale

    override val texture = Game.resources.boss4body.slice()

    override val baseColor get() = boss.camouflageColor

    override val damageSourceX get() = boss.x
    override val damageSourceY get() = boss.y

    val centerCollider = Collider(CircleCollisionShape(width * 0.7f), this)

    override fun onDamageHit() {
        boss.paralyze()
    }

    override fun onImpulseHit(x: Float, y: Float, strength: Float) {
        val distance = sqrt(x * x + y * y)

        boss.impulseX = x / distance * strength * 0.15f
        boss.impulseY = y / distance * strength * 0.15f
        boss.impulseSpin = strength * 0.15f
    }

    override fun updateWorldObject(delta: Duration, fightStarted: Boolean) {
        super.updateWorldObject(delta, fightStarted)

        val centerColliderOffsetX = 0.0f
        val centerColliderOffsetY = height * 0.5f

        val cos = rotation.cosine
        val sin = rotation.sine

        val centerColliderX = x + cos * centerColliderOffsetX - sin * centerColliderOffsetY
        val centerColliderY = y + sin * centerColliderOffsetX + cos * centerColliderOffsetY

        (centerCollider.shape as CircleCollisionShape).radius = width * 0.7f
        centerCollider.update(centerColliderX, centerColliderY)
    }
}