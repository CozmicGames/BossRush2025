package com.cozmicgames.bosses.tutorialBoss

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.Hittable
import com.littlekt.graphics.slice
import kotlin.math.sqrt

class Tail(val boss: TutorialBoss, scale: Float, layer: Int) : EnemyPart("bossTutorialTail"), PlayerDamageSource, Hittable {
    override val renderLayer = layer

    override val texture = Game.resources.bossTutorialTail.slice()

    override val width = Game.resources.bossTutorialTail.width * scale

    override val height = Game.resources.bossTutorialTail.height * scale

    override val flipX get() = boss.isFlipped

    override val collider = Collider(getRectangleCollisionShape(scaleX = 0.5f, scaleY = 0.7f), this)

    override val damageSourceX get() = boss.x

    override val damageSourceY get() = boss.y

    override fun onImpulseHit(x: Float, y: Float, strength: Float) {
        val distance = sqrt(x * x + y * y)

        boss.impulseX = x / distance * strength * 0.15f
        boss.impulseY = y / distance * strength * 0.15f
        boss.impulseSpin = strength * 0.15f
    }

    override fun onDamageHit() {
        Game.resources.hitEnemySound.play(0.5f)
    }
}