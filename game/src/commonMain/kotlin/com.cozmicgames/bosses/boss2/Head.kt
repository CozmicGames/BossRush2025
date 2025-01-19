package com.cozmicgames.bosses.boss2

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.Hittable
import com.littlekt.graphics.slice

class Head(private val boss: Boss2, scale: Float, layer: Int) : EnemyPart("enemy2head"), Hittable, PlayerDamageSource {
    override val canHit get() = !boss.isInvulnerable

    override val renderLayer = layer

    override val width = Game.resources.boss2head.width * scale

    override val height = Game.resources.boss2head.height * scale

    override val flipX get() = boss.flip

    override val collider = Collider(getCircleCollisionShape(0.9f), this)

    override val texture = Game.resources.boss2head.slice()

    override val damageSourceX get() = boss.x
    override val damageSourceY get() = boss.y

    override fun onDamageHit() {
        boss.paralyze()
    }
}