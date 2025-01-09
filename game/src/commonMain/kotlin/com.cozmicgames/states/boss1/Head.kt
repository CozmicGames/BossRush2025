package com.cozmicgames.states.boss1

import com.cozmicgames.Game
import com.cozmicgames.entities.EnemyPart
import com.cozmicgames.physics.CircleCollisionShape
import com.cozmicgames.physics.Collider
import com.littlekt.graphics.slice

class Head(layer: Int) : EnemyPart("boss1head") {
    override val renderLayer = layer

    override val collider = Collider(CircleCollisionShape(100.0f), this)

    override val width = 256.0f

    override val height = 256.0f

    override val texture = Game.resources.boss1head.slice()
}