package com.cozmicgames.bosses.tutorialBoss

import com.cozmicgames.Game
import com.cozmicgames.bosses.BossHittable
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.cozmicgames.physics.Collider
import kotlin.math.sqrt

class BodyFin(override val boss: TutorialBoss, scale: Float, layer: Int) : EnemyPart("bossTutorialBodyFin"), PlayerDamageSource, BossHittable {
    override val renderLayer = layer

    override val texture = Game.textures.bossTutorialBodyFin

    override val width = Game.textures.bossTutorialBodyFin.width * scale

    override val height = Game.textures.bossTutorialBodyFin.height * scale

    override val flipX get() = boss.isFlipped

    override val collider = Collider(getRectangleCollisionShape(scaleX = 0.5f), this)

    override val damageSourceX get() = boss.x

    override val damageSourceY get() = boss.y

    override fun onImpulseHit(x: Float, y: Float, strength: Float) {
        val distance = sqrt(x * x + y * y)

        boss.impulseX = x / distance * strength * 0.15f
        boss.impulseY = y / distance * strength * 0.15f
        boss.impulseSpin = strength * 0.15f
    }

    override fun onDamageHit() {
        Game.audio.hitEnemySound.play(0.5f)
    }

    override fun onBaitHit() {
        boss.paralyze()
    }
}