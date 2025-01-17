package com.cozmicgames.bosses.boss1

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.cozmicgames.physics.CircleCollisionShape
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.Hittable
import com.littlekt.graphics.slice

class Head(private val boss: Boss1, size: Float, layer: Int) : EnemyPart("boss1head"), Hittable, PlayerDamageSource {
    override val canHit get() = !boss.isInvulnerable

    override val renderLayer = layer

    override val collider = Collider(CircleCollisionShape(size * 0.4f), this)

    override val width = size

    override val height = size

    override val texture = Game.resources.boss1head.slice()

    override val damageSourceX get() = boss.x
    override val damageSourceY get() = boss.y

    override fun onDamageHit() {
        boss.paralyze()
    }
}