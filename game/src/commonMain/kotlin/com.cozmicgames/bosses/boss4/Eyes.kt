package com.cozmicgames.bosses.boss4

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.littlekt.graphics.slice

class Eyes(private val boss: Boss4, private val eyesScale: Float, layer: Int) : EnemyPart("boss4head") {
    override val renderLayer = layer

    override val collider = null

    override val width get() = Game.resources.boss4eyes.width * eyesScale * boss.bossScale

    override val height get() = Game.resources.boss4eyes.height * eyesScale * boss.bossScale

    override var texture = Game.resources.boss4eyes.slice()
}