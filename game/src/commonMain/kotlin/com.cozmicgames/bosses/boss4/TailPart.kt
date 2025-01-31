package com.cozmicgames.bosses.boss4

import com.cozmicgames.Constants
import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.RectangleCollisionShape
import com.littlekt.math.geom.cosine
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.sine
import kotlin.math.pow
import kotlin.time.Duration

class TailPart(val tail: Tail, val parent: TailPart? = null, val index: Int, layer: Int) : EnemyPart("boss4tail${index}") {
    override val renderLayer = layer

    override val collider = Collider(getRectangleCollisionShape(scaleX = (1.0f - index.toFloat() / Constants.BOSS4_TAIL_PARTS).pow(2.0f)), tail)

    override val texture = Game.textures.boss4tailSlices[index]

    override val baseColor get() = tail.boss.camouflageColor

    override val width get() = Game.textures.boss4tail.width * tail.scale * tail.boss.bossScale

    override val height get() = Game.textures.boss4tail.height * tail.scale * tail.boss.bossScale / Constants.BOSS4_TAIL_PARTS

    private val halfWidth get() = width * 0.5f

    private val halfHeight get() = height * 0.5f

    var partRotation = 0.0.degrees

    override fun updateWorldObject(delta: Duration, isFighting: Boolean) {
        val partRotation = partRotation

        val parentRotation = parent?.rotation ?: tail.tailAngle
        val parentCos = parentRotation.cosine
        val parentSin = parentRotation.sine

        val pivotX: Float
        val pivotY: Float

        val xOffset: Float
        val yOffset = -halfHeight

        if (partRotation.degrees >= 0.0f) {
            if (parent != null) {
                pivotX = parent.x + (parentCos * -parent.halfWidth - parentSin * -parent.halfHeight) // Left corner
                pivotY = parent.y + (parentSin * -parent.halfWidth + parentCos * -parent.halfHeight)
                xOffset = halfWidth
            } else {
                pivotX = tail.x + (parentCos * -halfWidth - parentSin * halfHeight)
                pivotY = tail.y + (parentSin * -halfWidth + parentCos * halfHeight)
                xOffset = halfWidth
            }
        } else {
            if (parent != null) {
                pivotX = parent.x + (parentCos * parent.halfWidth - parentSin * -parent.halfHeight) // Right corner
                pivotY = parent.y + (parentSin * parent.halfWidth + parentCos * -parent.halfHeight)
                xOffset = -halfWidth
            } else {
                pivotX = tail.x + (parentCos * halfWidth - parentSin * halfHeight)
                pivotY = tail.y + (parentSin * halfWidth + parentCos * halfHeight)
                xOffset = -halfWidth
            }
        }

        rotation = parentRotation + partRotation

        val partCos = rotation.cosine
        val partSin = rotation.sine

        x = pivotX + partCos * xOffset - partSin * yOffset
        y = pivotY + partSin * xOffset + partCos * yOffset

        (collider.shape as RectangleCollisionShape).angle = rotation
        (collider.shape as RectangleCollisionShape).width = width * (1.0f - index.toFloat() / Constants.BOSS4_TAIL_PARTS).pow(2.0f)
        (collider.shape as RectangleCollisionShape).height = height
        collider.update(x, y)
    }
}