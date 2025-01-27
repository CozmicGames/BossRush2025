package com.cozmicgames.bosses.boss4

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.Hittable
import com.cozmicgames.physics.RectangleCollisionShape
import com.littlekt.graphics.slice
import com.littlekt.math.geom.cosine
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.sine
import kotlin.math.sqrt
import kotlin.time.Duration

class Wing(private val boss: Boss4, private val left: Boolean, private val wingScale: Float, layer: Int) : EnemyPart("boss4wing${if (left) "left" else "right"}"), Hittable, PlayerDamageSource {
    override val canHit get() = !boss.isInvulnerable

    override val renderLayer = layer

    override val collider = Collider(RectangleCollisionShape(width, height * 0.15f, 0.0.degrees), this)

    override val width get() = Game.resources.boss4wing.width * wingScale * boss.bossScale

    override val height get() = Game.resources.boss4wing.height * wingScale * boss.bossScale

    override val flipX = !left

    override val texture = Game.resources.boss4wing.slice()

    override val baseColor get() = boss.camouflageColor

    override val damageSourceX get() = boss.x
    override val damageSourceY get() = boss.y

    val lowerCollider = Collider(RectangleCollisionShape(width, height * 0.15f, 0.0.degrees), this)

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

        val mainColliderOffsetX = if (left) 0.1f * width else -0.1f * width
        val mainColliderOffsetY = 0.1f * height

        val cos = rotation.cosine
        val sin = rotation.sine

        val mainColliderX = x + cos * mainColliderOffsetX - sin * mainColliderOffsetY
        val mainColliderY = y + sin * mainColliderOffsetX + cos * mainColliderOffsetY

        (collider.shape as RectangleCollisionShape).angle = rotation + if (left) 35.0.degrees else (-35.0).degrees
        (collider.shape as RectangleCollisionShape).width = width
        (collider.shape as RectangleCollisionShape).height = height * 0.15f
        collider.update(mainColliderX, mainColliderY)

        val lowerColliderOffsetX = if (left) 0.2f * width else -0.2f * width
        val lowerColliderOffsetY = -0.1f * height

        val lowerColliderX = x + cos * lowerColliderOffsetX - sin * lowerColliderOffsetY
        val lowerColliderY = y + sin * lowerColliderOffsetX + cos * lowerColliderOffsetY

        (lowerCollider.shape as RectangleCollisionShape).width = width
        (lowerCollider.shape as RectangleCollisionShape).height = height * 0.15f
        lowerCollider.update(lowerColliderX, lowerColliderY)
    }
}