package com.cozmicgames.bosses.boss1

import com.cozmicgames.Constants
import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.RectangleCollisionShape
import com.littlekt.math.geom.cosine
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.sine
import kotlin.time.Duration

class TentaclePart(val tentacle: Tentacle, val parent: TentaclePart? = null, val flip: Boolean, val index: Int, layer: Int) : EnemyPart("boss1tentacle${index}") {
    override val renderLayer = layer

    override val collider = Collider(getRectangleCollisionShape(scaleY = 0.8f - index * 0.1f), tentacle)

    override val texture = Game.textures.boss1tentacleSlices[index]

    override val width get() = Game.textures.boss1tentacle.width * tentacle.scale / Constants.BOSS1_TENTACLE_PARTS

    override val height get() = Game.textures.boss1tentacle.height * 3.0f

    override val flipX get() = flip

    private val halfWidth get() = width * 0.5f

    private val halfHeight get() = height * 0.5f

    var tentacleRotation = 0.0.degrees

    override fun updateWorldObject(delta: Duration, isFighting: Boolean) {
        val tentacleRotation = if (flip) -tentacleRotation else tentacleRotation

        val parentRotation = parent?.rotation ?: tentacle.tentacleAngle
        val parentCos = parentRotation.cosine
        val parentSin = parentRotation.sine

        val pivotX: Float
        val pivotY: Float
        val xOffset: Float
        val yOffset: Float

        if (flip) {
            xOffset = -halfWidth

            if (tentacleRotation.degrees >= 0.0f) {
                if (parent != null) {
                    pivotX = parent.x + (parentCos * -halfWidth - parentSin * halfHeight) // Lower corner
                    pivotY = parent.y + (parentSin * -halfWidth + parentCos * halfHeight)
                    yOffset = -halfHeight
                } else {
                    pivotX = tentacle.x
                    pivotY = tentacle.y
                    yOffset = 0.0f
                }
            } else {
                if (parent != null) {
                    pivotX = parent.x + (parentCos * -halfWidth - parentSin * -halfHeight) // Upper corner
                    pivotY = parent.y + (parentSin * -halfWidth + parentCos * -halfHeight)
                    yOffset = halfHeight
                } else {
                    pivotX = tentacle.x
                    pivotY = tentacle.y
                    yOffset = 0.0f
                }
            }
        } else {
            xOffset = halfWidth

            if (tentacleRotation.degrees >= 0.0f) {
                if (parent != null) {
                    pivotX = parent.x + (parentCos * halfWidth - parentSin * -halfHeight) // Lower corner
                    pivotY = parent.y + (parentSin * halfWidth + parentCos * -halfHeight)
                    yOffset = halfHeight
                } else {
                    pivotX = tentacle.x
                    pivotY = tentacle.y
                    yOffset = 0.0f
                }
            } else {
                if (parent != null) {
                    pivotX = parent.x + (parentCos * halfWidth - parentSin * halfHeight) // Upper corner
                    pivotY = parent.y + (parentSin * halfWidth + parentCos * halfHeight)
                    yOffset = -halfHeight
                } else {
                    pivotX = tentacle.x
                    pivotY = tentacle.y
                    yOffset = 0.0f
                }
            }
        }

        rotation = parentRotation + tentacleRotation

        val tentacleCos = rotation.cosine
        val tentacleSin = rotation.sine

        x = pivotX + tentacleCos * xOffset - tentacleSin * yOffset
        y = pivotY + tentacleSin * xOffset + tentacleCos * yOffset

        (collider.shape as RectangleCollisionShape).angle = rotation
        collider.update(x, y)
    }
}