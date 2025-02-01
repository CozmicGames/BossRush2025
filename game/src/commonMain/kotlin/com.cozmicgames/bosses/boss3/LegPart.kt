package com.cozmicgames.bosses.boss3

import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.entities.worldObjects.ProjectileSource
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.RectangleCollisionShape
import com.littlekt.graphics.g2d.TextureSlice
import com.littlekt.math.geom.cosine
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.sine
import kotlin.time.Duration

class LegPart(val leg: Leg, val parent: LegPart? = null, val flip: Boolean, val index: Int, override val texture: TextureSlice, val partScale: Float, layer: Int) : EnemyPart("boss3leg${index}"), ProjectileSource {
    override val renderLayer = layer

    override val width get() = texture.width * partScale

    override val height get() = texture.height * partScale

    override val collider = Collider(getRectangleCollisionShape(scaleY = 0.8f - index * 0.1f), leg)

    override val flipX get() = !flip

    override val muzzleX = 0.0f
    override val muzzleY = 0.0f
    override val muzzleRotation = 0.0.degrees
    override val projectileSourceId = "boss3"
    override val isStunMode = false

    val halfWidth get() = width * 0.5f

    val halfHeight get() = height * 0.5f

    var legRotation = 0.0.degrees

    override fun updateWorldObject(delta: Duration, isFighting: Boolean) {
        val legRotation = if (flip) -legRotation else legRotation

        val parentRotation = parent?.rotation ?: leg.legAngle
        val parentCos = parentRotation.cosine
        val parentSin = parentRotation.sine

        val pivotX: Float
        val pivotY: Float
        val xOffset: Float
        val yOffset: Float

        if (flip) {
            xOffset = -halfWidth

            if (legRotation.degrees >= 0.0f) {
                if (parent != null) {
                    pivotX = parent.x + (parentCos * -parent.halfWidth - parentSin * parent.halfHeight) // Lower corner
                    pivotY = parent.y + (parentSin * -parent.halfWidth + parentCos * parent.halfHeight)
                    yOffset = -parent.halfHeight
                } else {
                    pivotX = leg.x
                    pivotY = leg.y
                    yOffset = 0.0f
                }
            } else {
                if (parent != null) {
                    pivotX = parent.x + (parentCos * -parent.halfWidth - parentSin * -parent.halfHeight) // Upper corner
                    pivotY = parent.y + (parentSin * -parent.halfWidth + parentCos * -parent.halfHeight)
                    yOffset = parent.halfHeight
                } else {
                    pivotX = leg.x
                    pivotY = leg.y
                    yOffset = 0.0f
                }
            }
        } else {
            xOffset = halfWidth

            if (legRotation.degrees >= 0.0f) {
                if (parent != null) {
                    pivotX = parent.x + (parentCos * parent.halfWidth - parentSin * -parent.halfHeight) // Lower corner
                    pivotY = parent.y + (parentSin * parent.halfWidth + parentCos * -parent.halfHeight)
                    yOffset = parent.halfHeight
                } else {
                    pivotX = leg.x
                    pivotY = leg.y
                    yOffset = 0.0f
                }
            } else {
                if (parent != null) {
                    pivotX = parent.x + (parentCos * parent.halfWidth - parentSin * parent.halfHeight) // Upper corner
                    pivotY = parent.y + (parentSin * parent.halfWidth + parentCos * parent.halfHeight)
                    yOffset = -parent.halfHeight
                } else {
                    pivotX = leg.x
                    pivotY = leg.y
                    yOffset = 0.0f
                }
            }
        }

        rotation = parentRotation + legRotation

        val legCos = rotation.cosine
        val legSin = rotation.sine

        x = pivotX + legCos * xOffset - legSin * yOffset
        y = pivotY + legSin * xOffset + legCos * yOffset

        (collider.shape as RectangleCollisionShape).angle = rotation
        collider.update(x, y)
    }
}