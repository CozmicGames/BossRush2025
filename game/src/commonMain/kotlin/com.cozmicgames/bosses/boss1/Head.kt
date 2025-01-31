package com.cozmicgames.bosses.boss1

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.cozmicgames.physics.CircleCollisionShape
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.Hittable
import com.littlekt.graphics.slice
import kotlin.math.sqrt

class Head(private val boss: Boss1, size: Float, layer: Int) : EnemyPart("boss1head"), Hittable, PlayerDamageSource {
    override val canBeHit get() = !boss.isInvulnerable

    override val renderLayer = layer

    override val collider = Collider(CircleCollisionShape(size * 0.4f), this)

    override val width = size

    override val height = size

    override var texture = Game.textures.boss1head.slice()

    override val damageSourceX get() = boss.x
    override val damageSourceY get() = boss.y

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