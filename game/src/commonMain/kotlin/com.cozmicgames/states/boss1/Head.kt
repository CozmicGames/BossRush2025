package com.cozmicgames.states.boss1

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.physics.CircleCollisionShape
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.Hittable
import com.littlekt.graphics.slice

class Head(private val boss: Boss1, layer: Int) : EnemyPart("boss1head"), Hittable {
    override val canHit get() = !boss.isInvulnerable

    override val renderLayer = layer

    override val collider = Collider(CircleCollisionShape(100.0f), this)

    override val width = 256.0f

    override val height = 256.0f

    override val texture = Game.resources.boss1head.slice()

    override fun onDamageHit() {
        boss.paralyze()
    }
}