package com.cozmicgames.entities.worldObjects

import com.cozmicgames.physics.CircleCollisionShape
import com.cozmicgames.physics.RectangleCollisionShape
import com.littlekt.graphics.g2d.SpriteBatch
import com.littlekt.graphics.g2d.TextureSlice
import com.littlekt.math.geom.Angle
import com.littlekt.math.geom.degrees
import kotlin.math.max
import kotlin.time.Duration

abstract class EnemyPart(id: String) : SingleLayerWorldObject(id) {
    abstract val width: Float
    abstract val height: Float
    abstract val texture: TextureSlice

    open val flipX: Boolean = false
    open val flipY: Boolean = false

    protected fun getRectangleCollisionShape(scaleX: Float = 1.0f, scaleY: Float = 1.0f, rotation: Angle = 0.0.degrees) = RectangleCollisionShape(width * scaleX, height * scaleY, rotation)

    protected fun getCircleCollisionShape(scale: Float = 1.0f) = CircleCollisionShape(max(width, height) * 0.5f * scale)

    override fun updateWorldObject(delta: Duration, fightStarted: Boolean) {
        collider?.let {
            (it.shape as? RectangleCollisionShape)?.angle = rotation
            it.update(x, y)
        }
    }

    override fun render(batch: SpriteBatch) {
        batch.draw(texture, x, y, originX = width * 0.5f, originY = height * 0.5f, rotation = rotation, width = width, height = height, scaleX = scale, scaleY = scale, color = color, flipX = flipX, flipY = flipY)
    }
}