package com.cozmicgames.bosses.boss2

import com.cozmicgames.Constants
import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.RectangleCollisionShape
import com.littlekt.math.geom.cosine
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.sine
import kotlin.time.Duration

class BodyPart(val body: Body, val parent: BodyPart? = null, val index: Int, layer: Int) : EnemyPart("boss1bodypart${index}") {
    companion object {
        private val COLLIDER_SCALES = arrayOf(
            0.6f,
            0.7f,
            0.7f,
            0.65f,
            0.6f,
            0.53f,
            0.46f,
            0.4f,
            0.33f,
            0.25f,
            0.21f,
            0.15f
        )

        private val COLLIDER_OFFSETS = arrayOf(
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            -0.05f,
            -0.08f,
            -0.1f,
            -0.1f,
            -0.07f,
            -0.05f,
            -0.03f
        )
    }

    override val renderLayer = layer

    override val collider = if (index < Constants.BOSS2_BODY_PARTS - 1) Collider(getRectangleCollisionShape(scaleY = COLLIDER_SCALES[index]), body) else null

    override val texture = Game.resources.boss2bodySlices[index]

    override val width get() = Game.resources.boss2body.width * body.scale / Constants.BOSS2_BODY_PARTS

    override val height get() = Game.resources.boss2body.height * body.scale

    override val flipX get() = body.boss.isFlipped

    private val halfWidth get() = width * 0.5f

    private val halfHeight get() = height * 0.5f

    var partRotation = 0.0.degrees

    override fun updateWorldObject(delta: Duration, fightStarted: Boolean) {
        val partRotation = if (body.boss.isFlipped) -partRotation else partRotation

        val parentRotation = parent?.rotation ?: body.rotation
        val parentCos = parentRotation.cosine
        val parentSin = parentRotation.sine

        val pivotX: Float
        val pivotY: Float
        val xOffset: Float
        val yOffset: Float

        if (body.boss.isFlipped) {
            xOffset = -halfWidth

            if (partRotation.degrees >= 0.0f) {
                if (parent != null) {
                    pivotX = parent.x + (parentCos * -halfWidth - parentSin * halfHeight * COLLIDER_SCALES[index]) // Lower corner
                    pivotY = parent.y + (parentSin * -halfWidth + parentCos * halfHeight * COLLIDER_SCALES[index])
                    yOffset = -halfHeight * COLLIDER_SCALES[index]
                } else {
                    pivotX = body.x - parentSin * halfHeight * COLLIDER_SCALES[index] // Lower corner
                    pivotY = body.y + parentCos * halfHeight * COLLIDER_SCALES[index]
                    yOffset = -halfHeight * COLLIDER_SCALES[index]
                }
            } else {
                if (parent != null) {
                    pivotX = parent.x + (parentCos * -halfWidth - parentSin * -halfHeight * COLLIDER_SCALES[index]) // Upper corner
                    pivotY = parent.y + (parentSin * -halfWidth + parentCos * -halfHeight * COLLIDER_SCALES[index])
                    yOffset = halfHeight * COLLIDER_SCALES[index]
                } else {
                    pivotX = body.x - parentSin * -halfHeight * COLLIDER_SCALES[index] // Upper corner
                    pivotY = body.y + parentCos * -halfHeight * COLLIDER_SCALES[index]
                    yOffset = halfHeight * COLLIDER_SCALES[index]
                }
            }
        } else {
            xOffset = halfWidth

            if (partRotation.degrees >= 0.0f) {
                if (parent != null) {
                    pivotX = parent.x + (parentCos * halfWidth - parentSin * -halfHeight * COLLIDER_SCALES[index]) // Lower corner
                    pivotY = parent.y + (parentSin * halfWidth + parentCos * -halfHeight * COLLIDER_SCALES[index])
                    yOffset = halfHeight * COLLIDER_SCALES[index]
                } else {
                    pivotX = body.x - parentSin * -halfHeight * COLLIDER_SCALES[index] // Lower corner
                    pivotY = body.y + parentCos * -halfHeight * COLLIDER_SCALES[index]
                    yOffset = halfHeight * COLLIDER_SCALES[index]
                }
            } else {
                if (parent != null) {
                    pivotX = parent.x + (parentCos * halfWidth - parentSin * halfHeight * COLLIDER_SCALES[index]) // Upper corner
                    pivotY = parent.y + (parentSin * halfWidth + parentCos * halfHeight * COLLIDER_SCALES[index])
                    yOffset = -halfHeight * COLLIDER_SCALES[index]
                } else {
                    pivotX = body.x - parentSin * halfHeight * COLLIDER_SCALES[index] // Upper corner
                    pivotY = body.y + parentCos * halfHeight * COLLIDER_SCALES[index]
                    yOffset = -halfHeight * COLLIDER_SCALES[index]
                }
            }
        }

        rotation = parentRotation + partRotation

        val partCos = rotation.cosine
        val partSin = rotation.sine

        x = pivotX + partCos * xOffset - partSin * yOffset
        y = pivotY + partSin * xOffset + partCos * yOffset

        val colliderOffset = COLLIDER_OFFSETS[index] * height
        val colliderX = x + partSin * colliderOffset
        val colliderY = y + partCos * colliderOffset

        collider?.let {
            (it.shape as RectangleCollisionShape).angle = rotation
            it.update(colliderX, colliderY)
        }
    }
}