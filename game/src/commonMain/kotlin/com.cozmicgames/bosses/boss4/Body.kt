package com.cozmicgames.bosses.boss4

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.cozmicgames.physics.CircleCollisionShape
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.Hittable
import com.littlekt.graphics.Color
import com.littlekt.graphics.slice
import com.littlekt.math.geom.cosine
import com.littlekt.math.geom.sine
import kotlin.time.Duration

class Body(private val boss: Boss4, bodyScale: Float, layer: Int) : EnemyPart("boss4body"), Hittable, PlayerDamageSource {
    override val canHit get() = !boss.isInvulnerable

    override val renderLayer = layer

    override val collider = Collider(CircleCollisionShape(Game.resources.boss4body.width * bodyScale * 0.4f), this)

    override val width = Game.resources.boss4body.width * bodyScale

    override val height = Game.resources.boss4body.height * bodyScale

    override val texture = Game.resources.boss4body.slice()

    override val baseColor get() = boss.camouflageColor

    override val damageSourceX get() = boss.x
    override val damageSourceY get() = boss.y

    val centerCollider = Collider(CircleCollisionShape(Game.resources.boss4body.width * bodyScale * 0.7f), this)

    override fun onDamageHit() {
        boss.paralyze()
    }

    override fun updateWorldObject(delta: Duration, fightStarted: Boolean) {
        super.updateWorldObject(delta, fightStarted)

        val centerColliderOffsetX = 0.0f
        val centerColliderOffsetY = height * 0.5f

        val cos = rotation.cosine
        val sin = rotation.sine

        val centerColliderX = x + cos * centerColliderOffsetX - sin * centerColliderOffsetY
        val centerColliderY = y + sin * centerColliderOffsetX + cos * centerColliderOffsetY

        centerCollider.update(centerColliderX, centerColliderY)
    }
}