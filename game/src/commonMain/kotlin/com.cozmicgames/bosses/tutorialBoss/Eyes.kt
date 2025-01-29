package com.cozmicgames.bosses.tutorialBoss

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.littlekt.graphics.slice

class Eyes(private val boss: TutorialBoss, private val eyesScale: Float, layer: Int) : EnemyPart("bossTutorialEyes") {
    override val renderLayer = layer

    override val collider = null

    override val width get() = Game.resources.bossTutorialEyes.width * eyesScale

    override val height get() = Game.resources.bossTutorialEyes.height * eyesScale

    override var texture = Game.resources.bossTutorialEyes.slice()

    override val flipX get() = boss.isFlipped
}