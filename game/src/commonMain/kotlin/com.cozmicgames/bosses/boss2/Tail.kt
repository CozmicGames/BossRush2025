package com.cozmicgames.bosses.boss2

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.cozmicgames.physics.Collider
import com.littlekt.graphics.slice

class Tail(val boss: Boss2, scale: Float, layer: Int) : EnemyPart("boss2tail"), PlayerDamageSource {
    override val renderLayer = layer

    override val texture = Game.resources.boss2tail.slice()

    override val width = Game.resources.boss2tail.width * scale

    override val height = Game.resources.boss2tail.height * scale

    override val flipX get() = boss.flip

    override val collider = Collider(getRectangleCollisionShape(scaleX = 0.5f), this)

    override val damageSourceX get() = boss.x

    override val damageSourceY get() = boss.y
}