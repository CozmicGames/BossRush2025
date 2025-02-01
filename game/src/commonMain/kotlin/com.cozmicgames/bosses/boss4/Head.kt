package com.cozmicgames.bosses.boss4

import com.cozmicgames.Game
import com.cozmicgames.bosses.BossHittable
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.cozmicgames.physics.CircleCollisionShape
import com.cozmicgames.physics.Collider
import com.littlekt.math.geom.cosine
import com.littlekt.math.geom.sine
import kotlin.math.sqrt
import kotlin.time.Duration

class Head(override val boss: Boss4, private val headScale: Float, layer: Int) : EnemyPart("boss4head"), BossHittable, PlayerDamageSource {
    override val canBeHit get() = !boss.isInvulnerable

    override val renderLayer = layer

    override val collider = Collider(CircleCollisionShape(width * 0.4f), this)

    override val width get() = Game.textures.boss4head.width * headScale * boss.bossScale

    override val height get() = Game.textures.boss4head.height * headScale * boss.bossScale

    override val texture = Game.textures.boss4head

    override val baseColor get() = boss.camouflageColor

    override val damageSourceX get() = boss.x
    override val damageSourceY get() = boss.y

    override fun updateWorldObject(delta: Duration, isFighting: Boolean) {
        super.updateWorldObject(delta, isFighting)

        val colliderOffsetX = 0.0f
        val colliderOffsetY = height * 0.09f

        val cos = rotation.cosine
        val sin = rotation.sine

        val colliderX = x + cos * colliderOffsetX - sin * colliderOffsetY
        val colliderY = y + sin * colliderOffsetX + cos * colliderOffsetY

        (collider.shape as CircleCollisionShape).radius = width * 0.4f
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