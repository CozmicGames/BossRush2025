package com.cozmicgames.bosses.boss2

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.cozmicgames.physics.Collider
import com.littlekt.graphics.slice
import com.littlekt.math.geom.degrees

class Mouth(val boss: Boss2, scale: Float, layer: Int) : EnemyPart("boss2mouth"), PlayerDamageSource {
    override val renderLayer = layer

    override val texture = Game.resources.boss2mouth.slice()

    override val width = Game.resources.boss2mouth.width * scale

    override val height = Game.resources.boss2mouth.height * scale

    override val flipX get() = boss.flip

    override val collider = Collider(getRectangleCollisionShape(scaleY = 0.5f), this)

    override val damageSourceX get() = boss.x

    override val damageSourceY get() = boss.y

    var mouthAngle = 0.0.degrees
}