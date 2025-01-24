package com.cozmicgames.bosses.boss4

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.cozmicgames.physics.CircleCollisionShape
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.Hittable
import com.littlekt.graphics.slice

class Eyes(private val boss: Boss4, eyesScale: Float, layer: Int) : EnemyPart("boss4head") {
    override val renderLayer = layer

    override val collider = null

    override val width = Game.resources.boss4eyes.width * eyesScale

    override val height = Game.resources.boss4eyes.height * eyesScale

    override val texture = Game.resources.boss4eyes.slice()
}