package com.cozmicgames.bosses.tutorialBoss

import com.cozmicgames.Game
import com.cozmicgames.bosses.BossHittable
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.cozmicgames.physics.CircleCollisionShape
import com.cozmicgames.physics.Collider
import com.littlekt.util.seconds
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class Heart(override val boss: TutorialBoss, layer: Int) : EnemyPart("bossTutorialHeart"), BossHittable, PlayerDamageSource {
    override val canBeHit get() = boss.isParalyzed

    override val renderLayer = layer

    override val width get() = Game.textures.bossHeart.width * 2.0f * size

    override val height get() = Game.textures.bossHeart.height * 2.0f * size

    override val flipX get() = boss.isFlipped

    override val collider = Collider(CircleCollisionShape(Game.textures.bossHeart.width * 1.7f), this)

    override val texture = Game.textures.bossHeart

    override val damageSourceX get() = boss.x
    override val damageSourceY get() = boss.y

    private var size = 1.0f
    private var timer = 0.0.seconds

    override fun updateWorldObject(delta: Duration, isFighting: Boolean) {
        timer += delta

        size = 1.0f + sin(timer.seconds * 3.0f) * 0.2f

        collider.update(x, y)
    }

    override fun onDamageHit() {
        boss.hit()
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