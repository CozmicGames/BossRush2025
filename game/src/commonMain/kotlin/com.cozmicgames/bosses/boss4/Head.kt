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
import kotlin.time.Duration

class Head(private val boss: Boss4, private val headScale: Float, layer: Int) : EnemyPart("boss4head"), Hittable, PlayerDamageSource {
    override val canHit get() = !boss.isInvulnerable

    override val renderLayer = layer

    override val collider = Collider(CircleCollisionShape(width * 0.4f), this)

    override val width get() = Game.resources.boss4head.width * headScale * boss.bossScale

    override val height get() = Game.resources.boss4head.height * headScale * boss.bossScale

    override val texture = Game.resources.boss4head.slice()

    override val baseColor get() = boss.camouflageColor

    override val damageSourceX get() = boss.x
    override val damageSourceY get() = boss.y

    override fun updateWorldObject(delta: Duration, fightStarted: Boolean) {
        super.updateWorldObject(delta, fightStarted)

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
        boss.paralyze()
    }
}