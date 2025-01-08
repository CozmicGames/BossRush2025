package com.cozmicgames.states.boss1

import com.cozmicgames.Constants
import com.cozmicgames.Game
import com.cozmicgames.entities.EnemyPart
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.RectangleCollisionShape
import com.littlekt.math.geom.cosine
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.sine
import kotlin.time.Duration

class TentaclePart(val tentacle: Tentacle, val parent: TentaclePart? = null, val flip: Boolean, val index: Int, layer: Int) : EnemyPart("boss1tentacle${index}") {
    override val renderLayer = layer

    override val collider = Collider(getCollisionShape(scaleY = 0.8f - index * 0.1f), tentacle) //TODO: Set proper userData, use some kind of proxy object

    override val texture = Game.resources.boss1tentacleSlices[index]

    override val width get() = Game.resources.boss1tentacle.width * 3.0f / Constants.BOSS1_TENTACLE_PARTS

    override val height get() = Game.resources.boss1tentacle.height * 3.0f

    override val flipX get() = flip

    private val halfWidth get() = width * 0.5f

    private val halfHeight get() = height * 0.5f

    var tentacleRotation = 0.0.degrees

    override fun updateEntity(delta: Duration) {
        if (Game.players.isHost) {
            val tentacleRotation = if (flip) -tentacleRotation else tentacleRotation

            val parentRotation = parent?.rotation ?: tentacle.rotation
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

            collider.x = x
            collider.y = y
            (collider.shape as RectangleCollisionShape).angle = rotation
            collider.update()

            Game.players.setGlobalState("boss1tentacle${index}X", x)
            Game.players.setGlobalState("boss1tentacle${index}Y", y)
            Game.players.setGlobalState("boss1tentacle${index}Angle", rotation.degrees)
        } else {
            x = Game.players.getGlobalState("boss1tentacle${index}X") ?: 0.0f
            y = Game.players.getGlobalState("boss1tentacle${index}Y") ?: 0.0f
            rotation = (Game.players.getGlobalState("boss1tentacle${index}Angle") ?: 0.0f).degrees
        }
    }
}