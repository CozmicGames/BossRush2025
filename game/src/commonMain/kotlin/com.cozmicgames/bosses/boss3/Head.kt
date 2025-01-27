package com.cozmicgames.bosses.boss3

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.cozmicgames.entities.worldObjects.ProjectileSource
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.Hittable
import com.cozmicgames.physics.RectangleCollisionShape
import com.littlekt.graphics.slice
import com.littlekt.math.geom.cosine
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.sine
import kotlin.time.Duration

class Head(private val boss: Boss3, scale: Float, layer: Int) : EnemyPart("boss3head"), Hittable, PlayerDamageSource, ProjectileSource {
    override val canHit get() = !boss.isInvulnerable

    override val renderLayer = layer

    override val width = Game.resources.boss3head.width * scale

    override val height = Game.resources.boss3head.height * scale

    override val collider = Collider(getRectangleCollisionShape(0.5f, 0.3f), this)

    override val texture = Game.resources.boss3head.slice()

    override val damageSourceX get() = boss.x
    override val damageSourceY get() = boss.y

    override val muzzleX = 0.0f
    override val muzzleY = 0.0f
    override val muzzleRotation = 0.0.degrees
    override val projectileSourceId = "boss3"
    override val isStunMode = false

    val blockingCollider = Collider(getRectangleCollisionShape(0.9f, 0.3f), null)

    override fun updateWorldObject(delta: Duration, fightStarted: Boolean) {
        super.updateWorldObject(delta, fightStarted)

        val cos = rotation.cosine
        val sin = rotation.sine

        val blockingOffsetX = 0.0f
        val blockingOffsetY = 0.1f * height

        val blockingColliderX = x + blockingOffsetX * cos - blockingOffsetY * sin
        val blockingColliderY = y + blockingOffsetX * sin + blockingOffsetY * cos

        (blockingCollider.shape as? RectangleCollisionShape)?.angle = rotation
        blockingCollider.update(blockingColliderX, blockingColliderY)

        val colliderOffsetX = 0.0f
        val colliderOffsetY = -0.2f * height

        val colliderX = x + colliderOffsetX * cos - colliderOffsetY * sin
        val colliderY = y + colliderOffsetX * sin + colliderOffsetY * cos

        (collider.shape as? RectangleCollisionShape)?.angle = rotation
        collider.update(colliderX, colliderY)
    }

    override fun onDamageHit() {
        boss.paralyze()
    }
}