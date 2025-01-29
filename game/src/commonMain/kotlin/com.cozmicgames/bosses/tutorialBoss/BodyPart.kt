package com.cozmicgames.bosses.tutorialBoss

import com.cozmicgames.Constants
import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.RectangleCollisionShape
import com.littlekt.math.geom.cosine
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.sine
import kotlin.time.Duration

class BodyPart(val body: Body, val parent: BodyPart? = null, val index: Int, layer: Int) : EnemyPart("bossTutorialBodypart${index}") {
    companion object {
        private val COLLIDER_SCALES = arrayOf(
            0.85f,
            1.0f,
            0.9f,
            0.76f,
            0.62f,
            0.5f,
            0.38f,
            0.26f
        )

        private val COLLIDER_OFFSETS = arrayOf(
            0.0f,
            0.0f,
            0.0f,
            0.0f,
            -0.01f,
            -0.03f,
            -0.05f,
            -0.05f
        )
    }

    override val renderLayer = layer

    override val collider = Collider(getRectangleCollisionShape(scaleY = COLLIDER_SCALES[index]), body)

    override val texture = Game.resources.bossTutorialBodySlices[index]

    override val width get() = Game.resources.bossTutorialBody.width * body.scale / Constants.BOSS_TUTORIAL_BODY_PARTS

    override val height get() = Game.resources.bossTutorialBody.height * body.scale

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