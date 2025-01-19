package com.cozmicgames.bosses.boss2

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.cozmicgames.physics.Collider
import com.littlekt.graphics.Color
import com.littlekt.graphics.MutableColor
import com.littlekt.graphics.slice
import kotlin.time.Duration

class Shield(val boss: Boss2, scale: Float, layer: Int) : EnemyPart("boss2mouth"), PlayerDamageSource {
    override val renderLayer = layer

    override val texture = Game.resources.shield.slice()

    override val width = Game.resources.shield.width * scale

    override val height = Game.resources.shield.height * scale

    override val mixColor = MutableColor(Color.WHITE)

    override val flipX get() = boss.flip

    override val collider = Collider(getCircleCollisionShape(), this)

    override val damageSourceX get() = boss.x

    override val damageSourceY get() = boss.y

    var intensity = 1.0f

    fun update(delta: Duration, movement: ShieldMovement) {
        movement.updateMouth(delta, this)
        mixColor.a = intensity
        collider.shouldCollide = intensity > 0.5f
    }
}