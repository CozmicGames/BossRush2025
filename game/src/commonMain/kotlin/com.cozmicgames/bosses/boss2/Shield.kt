package com.cozmicgames.bosses.boss2

import com.cozmicgames.Game
import com.cozmicgames.bosses.BossHittable
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.cozmicgames.entities.worldObjects.ProjectileSource
import com.cozmicgames.physics.Collider
import com.littlekt.graphics.Color
import com.littlekt.graphics.MutableColor
import kotlin.math.sqrt
import kotlin.time.Duration

class Shield(override val boss: Boss2, scale: Float, layer: Int) : EnemyPart("boss2mouth"), PlayerDamageSource, ProjectileSource, BossHittable {
    override val renderLayer = layer

    override val texture = Game.textures.shield

    override val width = Game.textures.shield.width * scale

    override val height = Game.textures.shield.height * scale

    override val mixColor = MutableColor(Color.WHITE)

    override val flipX get() = boss.isFlipped

    override val collider = Collider(getCircleCollisionShape(), this)

    override val damageSourceX get() = boss.x

    override val damageSourceY get() = boss.y

    var intensity = 1.0f

    fun update(delta: Duration, movement: ShieldMovement) {
        movement.updateShield(delta, this)
        mixColor.a = intensity
        collider.shouldCollide = intensity > 0.5f
    }

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

    override fun onDamageHit() {
        Game.audio.hitEnemySound.play(0.5f)
    }

    override fun onBaitHit() {
        boss.paralyze()
    }
}