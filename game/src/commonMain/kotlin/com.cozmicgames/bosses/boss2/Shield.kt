package com.cozmicgames.bosses.boss2

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.cozmicgames.entities.worldObjects.ProjectileSource
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.Hittable
import com.littlekt.graphics.Color
import com.littlekt.graphics.MutableColor
import com.littlekt.graphics.slice
import kotlin.math.sqrt
import kotlin.time.Duration

class Shield(val boss: Boss2, scale: Float, layer: Int) : EnemyPart("boss2mouth"), PlayerDamageSource, ProjectileSource, Hittable {
    override val renderLayer = layer

    override val texture = Game.resources.shield.slice()

    override val width = Game.resources.shield.width * scale

    override val height = Game.resources.shield.height * scale

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
        Game.resources.hitEnemySound.play(0.5f)
    }
}