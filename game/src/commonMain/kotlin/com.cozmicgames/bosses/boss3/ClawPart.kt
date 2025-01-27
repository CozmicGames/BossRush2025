package com.cozmicgames.bosses.boss3

import com.cozmicgames.Game
import com.cozmicgames.bosses.boss1.Beak
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.entities.worldObjects.ProjectileSource
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.RectangleCollisionShape
import com.littlekt.graphics.slice
import com.littlekt.math.geom.cosine
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.sine
import kotlin.time.Duration

class ClawPart(val claw: Claw, private val flip: Boolean, private val isUpper: Boolean, private val partScale: Float, layer: Int) : EnemyPart("boss3claw${if (isUpper) "upper" else "lower"}"), ProjectileSource {
    override val renderLayer = layer - 1

    override val texture = if (isUpper) Game.resources.boss3clawUpper.slice() else Game.resources.boss3clawLower.slice()

    override val width get() = texture.width * partScale

    override val height get() = texture.height * partScale

    override val flipX get() = !flip

    override val collider = Collider(getRectangleCollisionShape(scaleX = 0.5f), claw.arm)

    override val muzzleX = 0.0f
    override val muzzleY = 0.0f
    override val muzzleRotation = 0.0.degrees
    override val projectileSourceId = "boss3"
    override val isStunMode = false

    override fun updateWorldObject(delta: Duration, fightStarted: Boolean) {
        val pivotOffsetX = (if (isUpper) claw.width * 0.48f else claw.width * 0.27f) * (if (flip) -1.0f else 1.0f)
        val pivotOffsetY = (if (isUpper) claw.height * 0.18f else -claw.height * 0.25f)

        val clawRotation = claw.rotation
        val clawCos = clawRotation.cosine
        val clawSin = clawRotation.sine

        val pivotX = claw.x + clawCos * pivotOffsetX - clawSin * pivotOffsetY
        val pivotY = claw.y + clawSin * pivotOffsetX + clawCos * pivotOffsetY

        val cos = rotation.cosine
        val sin = rotation.sine

        val xOffset = width * 0.45f * (if (flip) -1.0f else 1.0f)
        val yOffset = 0.0f

        x = pivotX + cos * xOffset - sin * yOffset
        y = pivotY + sin * xOffset + cos * yOffset

        (collider.shape as? RectangleCollisionShape)?.angle = rotation
        collider.update(x, y)
    }
}