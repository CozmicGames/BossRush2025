package com.cozmicgames.bosses.boss3

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.cozmicgames.entities.worldObjects.ProjectileSource
import com.cozmicgames.physics.CircleCollisionShape
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.Hittable
import com.littlekt.graphics.slice
import com.littlekt.math.geom.degrees
import com.littlekt.util.seconds
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class Heart(val boss: Boss3, private val heartScale: Float, layer: Int) : EnemyPart("boss3heart"), Hittable, PlayerDamageSource, ProjectileSource {
    override val canBeHit get() = boss.isParalyzed

    override val renderLayer = layer

    override val width get() = Game.textures.boss1heart.width * heartScale * size

    override val height get() = Game.textures.boss1heart.height * heartScale * size

    override val collider = Collider(CircleCollisionShape(Game.textures.boss1heart.width * 1.5f), this)

    override val texture = Game.textures.boss1heart.slice()

    override val damageSourceX get() = boss.x
    override val damageSourceY get() = boss.y

    override val muzzleX = 0.0f
    override val muzzleY = 0.0f
    override val muzzleRotation = 0.0.degrees
    override val projectileSourceId = "boss3"
    override val isStunMode = false

    private var size = 1.0f
    private var timer = 0.0.seconds

    override fun updateWorldObject(delta: Duration, isFighting: Boolean) {
        timer += delta

        size = 1.0f + sin(timer.seconds * 3.0f) * 0.2f

        collider.x = x
        collider.y = y
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

    override fun onBaitHit() {
        boss.paralyze()
    }
}