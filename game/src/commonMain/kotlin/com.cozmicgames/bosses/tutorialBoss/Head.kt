package com.cozmicgames.bosses.tutorialBoss

import com.cozmicgames.Game
import com.cozmicgames.bosses.BossHittable
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.cozmicgames.physics.Collider
import com.littlekt.math.geom.cosine
import com.littlekt.math.geom.sine
import kotlin.math.sqrt
import kotlin.time.Duration

class Head(override val boss: TutorialBoss, scale: Float, layer: Int) : EnemyPart("bossTutorialHead"), BossHittable, PlayerDamageSource {
    override val renderLayer = layer

    override val width = Game.textures.bossTutorialHead.width * scale

    override val height = Game.textures.bossTutorialHead.height * scale

    override val flipX get() = boss.isFlipped

    override val collider = Collider(getCircleCollisionShape(0.75f), this)

    override var texture = Game.textures.bossTutorialHead

    override val damageSourceX get() = boss.x
    override val damageSourceY get() = boss.y

    override fun updateWorldObject(delta: Duration, isFighting: Boolean) {
        super.updateWorldObject(delta, isFighting)

        val headColliderOffsetX = width * -0.3f * (if (boss.isFlipped) 1.0f else -1.0f)
        val headColliderOffsetY = 0.0f

        val cos = rotation.cosine
        val sin = rotation.sine

        val headColliderX = x + cos * headColliderOffsetX - sin * headColliderOffsetY
        val headColliderY = y + sin * headColliderOffsetX + cos * headColliderOffsetY

        collider.update(headColliderX, headColliderY)
    }

    override fun onDamageHit() {
        Game.audio.hitEnemySound.play(0.5f)

        boss.paralyze()
    }

    override fun onImpulseHit(x: Float, y: Float, strength: Float) {
        val distance = sqrt(x * x + y * y)

        boss.impulseX = x / distance * strength * 0.15f
        boss.impulseY = y / distance * strength * 0.15f
        boss.impulseSpin = strength * 0.15f
    }

    override fun onBaitHit() {
        boss.paralyze()
    }
}