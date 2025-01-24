package com.cozmicgames.bosses.boss4

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.cozmicgames.physics.CircleCollisionShape
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.Hittable
import com.littlekt.graphics.slice

class Body(private val boss: Boss4, bodyScale: Float, layer: Int) : EnemyPart("boss4body"), Hittable, PlayerDamageSource {
    override val canHit get() = !boss.isInvulnerable

    override val renderLayer = layer

    override val collider = Collider(CircleCollisionShape(bodyScale * 0.4f), this)

    override val width = Game.resources.boss4body.width * bodyScale

    override val height = Game.resources.boss4body.height * bodyScale

    override val texture = Game.resources.boss4body.slice()

    override val damageSourceX get() = boss.x
    override val damageSourceY get() = boss.y

    override fun onDamageHit() {
        boss.paralyze()
    }
}