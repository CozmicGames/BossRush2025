package com.cozmicgames.bosses.boss2

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.cozmicgames.entities.worldObjects.ProjectileSource
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.Hittable
import com.littlekt.graphics.slice
import kotlin.math.sqrt

class Sword(private val boss: Boss2, scale: Float, layer: Int) : EnemyPart("boss2sword"), PlayerDamageSource, ProjectileSource, Hittable {
    override val renderLayer = layer

    override val width = Game.resources.boss2sword.width * scale

    override val height = Game.resources.boss2sword.height * scale

    override val flipX get() = boss.isFlipped

    override val collider = Collider(getRectangleCollisionShape(scaleY = 0.4f), this)

    override var texture = Game.resources.boss2sword.slice()

    override val damageSourceX get() = boss.x

    override val damageSourceY get() = boss.y

    override val projectileSourceId = "boss2"

    override val muzzleX get() = boss.muzzleX

    override val muzzleY get() = boss.muzzleY

    override val muzzleRotation get() = boss.muzzleRotation

    override val isStunMode = false

    override fun onImpulseHit(x: Float, y: Float, strength: Float) {
        val distance = sqrt(x * x + y * y)

        boss.impulseX = x / distance * strength * 0.15f
        boss.impulseY = y / distance * strength * 0.15f
        boss.impulseSpin = strength * 0.15f
    }
}