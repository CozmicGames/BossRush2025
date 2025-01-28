package com.cozmicgames.bosses.boss2

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.cozmicgames.entities.worldObjects.ProjectileSource
import com.cozmicgames.physics.CircleCollisionShape
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.Hittable
import com.littlekt.graphics.slice
import com.littlekt.util.seconds
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class Heart(private val boss: Boss2, layer: Int) : EnemyPart("boss2heart"), Hittable, PlayerDamageSource, ProjectileSource {
    override val canBeHit get() = boss.isParalyzed

    override val renderLayer = layer

    override val width get() = Game.resources.boss1heart.width * 2.0f * size

    override val height get() = Game.resources.boss1heart.height * 2.0f * size

    override val flipX get() = boss.isFlipped

    override val collider = Collider(CircleCollisionShape(Game.resources.boss1heart.width * 1.5f), this)

    override val texture = Game.resources.boss1heart.slice()

    override val damageSourceX get() = boss.x
    override val damageSourceY get() = boss.y

    private var size = 1.0f
    private var timer = 0.0.seconds

    override fun updateWorldObject(delta: Duration, fightStarted: Boolean) {
        timer += delta

        size = 1.0f + sin(timer.seconds * 3.0f) * 0.2f

        collider.update(x, y)
    }

    override fun onDamageHit() {
        if (!boss.isInvulnerable)
            boss.hit()
    }

    override fun onImpulseHit(x: Float, y: Float, strength: Float) {
        val distance = sqrt(x * x + y * y)

        boss.impulseX = x / distance * strength * 0.15f
        boss.impulseY = y / distance * strength * 0.15f
        boss.impulseSpin = strength * 0.15f
    }


    override val projectileSourceId = "boss2"

    override val muzzleX get() = boss.muzzleX

    override val muzzleY get() = boss.muzzleY

    override val muzzleRotation get() = boss.muzzleRotation

    override val isStunMode = false
}