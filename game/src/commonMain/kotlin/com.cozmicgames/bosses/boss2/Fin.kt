package com.cozmicgames.bosses.boss2

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.cozmicgames.entities.worldObjects.ProjectileSource
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.Hittable
import com.littlekt.graphics.slice
import kotlin.math.sqrt

class Fin(val boss: Boss2, override val flipY: Boolean, scale: Float, layer: Int) : EnemyPart("boss2fin"), PlayerDamageSource, ProjectileSource, Hittable {
    override val renderLayer = layer

    override val texture = Game.textures.boss2fin.slice()

    override val width = Game.textures.boss2fin.width * scale

    override val height = Game.textures.boss2fin.height * scale

    override val flipX get() = boss.isFlipped

    override val collider = Collider(getRectangleCollisionShape(scaleX = 0.5f), this)

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

    override fun onDamageHit() {
        Game.audio.hitEnemySound.play(0.5f)
    }

    override fun onBaitHit() {
        boss.paralyze()
    }
}