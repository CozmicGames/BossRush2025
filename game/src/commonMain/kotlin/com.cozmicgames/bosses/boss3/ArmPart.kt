package com.cozmicgames.bosses.boss3

import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.entities.worldObjects.ProjectileSource
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.RectangleCollisionShape
import com.littlekt.graphics.Texture
import com.littlekt.graphics.slice
import com.littlekt.math.geom.cosine
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.sine
import kotlin.time.Duration

open class ArmPart(val arm: Arm, val parent: ArmPart? = null, val flip: Boolean, val index: Int, texture: Texture, val partScale: Float, layer: Int) : EnemyPart("boss3arm${index}"), ProjectileSource {
    override val renderLayer = layer

    override val texture = texture.slice()

    override val width get() = texture.texture.width * partScale

    override val height get() = texture.texture.height * partScale

    override val collider = Collider(getRectangleCollisionShape(scaleY = 0.8f - index * 0.1f), arm)

    override val flipX get() = !flip

    override val muzzleX = 0.0f
    override val muzzleY = 0.0f
    override val muzzleRotation = 0.0.degrees
    override val projectileSourceId = "boss3"
    override val isStunMode = false

    private val halfWidth get() = width * 0.5f

    private val halfHeight get() = height * 0.5f

    var armRotation = 0.0.degrees
        set(value) {
            if (value < 90.0.degrees && value > (-90.0).degrees)
                field = value
        }

    override fun updateWorldObject(delta: Duration, isFighting: Boolean) {
        val armRotation = if (flip) -armRotation else armRotation

        val parentRotation = parent?.rotation ?: arm.armAngle
        val parentCos = parentRotation.cosine
        val parentSin = parentRotation.sine

        val pivotX: Float
        val pivotY: Float
        val xOffset: Float
        val yOffset: Float

        if (flip) {
            xOffset = -halfWidth

            if (armRotation.degrees >= 0.0f) {
                if (parent != null) {
                    pivotX = parent.x + (parentCos * -halfWidth - parentSin * halfHeight) // Lower corner
                    pivotY = parent.y + (parentSin * -halfWidth + parentCos * halfHeight)
                    yOffset = -halfHeight
                } else {
                    pivotX = arm.x
                    pivotY = arm.y
                    yOffset = 0.0f
                }
            } else {
                if (parent != null) {
                    pivotX = parent.x + (parentCos * -halfWidth - parentSin * -halfHeight) // Upper corner
                    pivotY = parent.y + (parentSin * -halfWidth + parentCos * -halfHeight)
                    yOffset = halfHeight
                } else {
                    pivotX = arm.x
                    pivotY = arm.y
                    yOffset = 0.0f
                }
            }
        } else {
            xOffset = halfWidth

            if (armRotation.degrees >= 0.0f) {
                if (parent != null) {
                    pivotX = parent.x + (parentCos * halfWidth - parentSin * -halfHeight) // Lower corner
                    pivotY = parent.y + (parentSin * halfWidth + parentCos * -halfHeight)
                    yOffset = halfHeight
                } else {
                    pivotX = arm.x
                    pivotY = arm.y
                    yOffset = 0.0f
                }
            } else {
                if (parent != null) {
                    pivotX = parent.x + (parentCos * halfWidth - parentSin * halfHeight) // Upper corner
                    pivotY = parent.y + (parentSin * halfWidth + parentCos * halfHeight)
                    yOffset = -halfHeight
                } else {
                    pivotX = arm.x
                    pivotY = arm.y
                    yOffset = 0.0f
                }
            }
        }

        rotation = parentRotation + armRotation

        val armCos = rotation.cosine
        val armSin = rotation.sine

        x = pivotX + armCos * xOffset - armSin * yOffset
        y = pivotY + armSin * xOffset + armCos * yOffset

        (collider.shape as? RectangleCollisionShape)?.angle = rotation
        collider.update(x, y)
    }
}