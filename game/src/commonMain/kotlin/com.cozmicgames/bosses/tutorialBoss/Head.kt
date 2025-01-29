package com.cozmicgames.bosses.tutorialBoss

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.Hittable
import com.littlekt.graphics.slice
import com.littlekt.math.geom.cosine
import com.littlekt.math.geom.sine
import kotlin.math.sqrt
import kotlin.time.Duration

class Head(private val boss: TutorialBoss, scale: Float, layer: Int) : EnemyPart("bossTutorialHead"), Hittable, PlayerDamageSource {
    override val renderLayer = layer

    override val width = Game.resources.bossTutorialHead.width * scale

    override val height = Game.resources.bossTutorialHead.height * scale

    override val flipX get() = boss.isFlipped

    override val collider = Collider(getCircleCollisionShape(0.75f), this)

    override var texture = Game.resources.bossTutorialHead.slice()

    override val damageSourceX get() = boss.x
    override val damageSourceY get() = boss.y

    override fun updateWorldObject(delta: Duration, fightStarted: Boolean) {
        super.updateWorldObject(delta, fightStarted)

        val headColliderOffsetX = width * -0.3f * (if (boss.isFlipped) 1.0f else -1.0f)
        val headColliderOffsetY = 0.0f

        val cos = rotation.cosine
        val sin = rotation.sine

        val headColliderX = x + cos * headColliderOffsetX - sin * headColliderOffsetY
        val headColliderY = y + sin * headColliderOffsetX + cos * headColliderOffsetY

        collider.update(headColliderX, headColliderY)
    }

    override fun onDamageHit() {
        Game.resources.hitEnemySound.play(0.5f)

        boss.paralyze()
    }

    override fun onImpulseHit(x: Float, y: Float, strength: Float) {
        val distance = sqrt(x * x + y * y)

        boss.impulseX = x / distance * strength * 0.15f
        boss.impulseY = y / distance * strength * 0.15f
        boss.impulseSpin = strength * 0.15f
    }
}