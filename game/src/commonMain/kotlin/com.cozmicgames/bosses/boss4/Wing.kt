package com.cozmicgames.bosses.boss4

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.cozmicgames.physics.CircleCollisionShape
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.Hittable
import com.littlekt.graphics.slice

class Wing(private val boss: Boss4, left: Boolean, wingScale: Float, layer: Int) : EnemyPart("boss4wing${if (left) "left" else "right"}"), Hittable, PlayerDamageSource {
    override val canHit get() = !boss.isInvulnerable

    override val renderLayer = layer

    override val collider = Collider(CircleCollisionShape(wingScale * 0.4f), this)

    override val width = Game.resources.boss4head.width * wingScale

    override val height = Game.resources.boss4head.height * wingScale

    override val flipX = !left

    override val texture = Game.resources.boss4wing.slice()

    override val damageSourceX get() = boss.x
    override val damageSourceY get() = boss.y

    override fun onDamageHit() {
        boss.paralyze()
    }
}