package com.cozmicgames.bosses.boss3

import com.cozmicgames.Game
import com.cozmicgames.bosses.BossHittable
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.cozmicgames.entities.worldObjects.ProjectileSource
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.RectangleCollisionShape
import com.littlekt.graphics.slice
import com.littlekt.math.geom.cosine
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.sine
import kotlin.math.sqrt
import kotlin.time.Duration

class Head(override val boss: Boss3, scale: Float, layer: Int) : EnemyPart("boss3head"), BossHittable, PlayerDamageSource, ProjectileSource {
    override val canBeHit get() = !boss.isInvulnerable

    override val renderLayer = layer

    override val width = Game.textures.boss3head.width * scale

    override val height = Game.textures.boss3head.height * scale

    override val collider = Collider(getRectangleCollisionShape(0.5f, 0.3f), this)

    override var texture = Game.textures.boss3head.slice()

    override val damageSourceX get() = boss.x
    override val damageSourceY get() = boss.y

    override val muzzleX = 0.0f
    override val muzzleY = 0.0f
    override val muzzleRotation = 0.0.degrees
    override val projectileSourceId = "boss3"
    override val isStunMode = false

    val blockingCollider = Collider(getRectangleCollisionShape(0.9f, 0.3f), null)

    override fun updateWorldObject(delta: Duration, isFighting: Boolean) {
        super.updateWorldObject(delta, isFighting)

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
        Game.audio.hitEnemySound.play(0.5f)

        boss.paralyze()
    }

    override fun onImpulseHit(x: Float, y: Float, strength: Float) {
        val distance = sqrt(x * x + y * y)

        boss.impulseX = x / distance * strength * 0.15f
        boss.impulseY = y / distance * strength * 0.15f
        boss.impulseSpin = strength * 0.15f
    }

    override fun onBaitHit() {
        boss.paralyze()
    }
}