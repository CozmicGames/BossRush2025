package com.cozmicgames.entities

import com.cozmicgames.physics.RectangleCollisionShape
import com.littlekt.graphics.g2d.SpriteBatch
import com.littlekt.graphics.g2d.TextureSlice
import com.littlekt.math.geom.Angle
import com.littlekt.math.geom.degrees
import kotlin.time.Duration

abstract class EnemyPart(id: String) : Entity(id) {
    abstract val width: Float
    abstract val height: Float
    abstract val texture: TextureSlice

    open val flipX: Boolean = false
    open val flipY: Boolean = false

    protected fun getCollisionShape(scaleX: Float = 1.0f, scaleY: Float = 1.0f, rotation: Angle = 0.0.degrees) = RectangleCollisionShape(width * scaleX, height * scaleY, rotation)

    override fun updateEntity(delta: Duration) {
        collider.x = x
        collider.y = y
        (collider.shape as? RectangleCollisionShape)?.angle = rotation
        collider.update()
        x = collider.x
        y = collider.y
    }

    override fun render(batch: SpriteBatch) {
        batch.draw(texture, x, y, originX = width * 0.5f, originY = height * 0.5f, rotation = rotation, width = width, height = height, scaleX = scale, scaleY = scale, color = color, flipX = flipX, flipY = flipY)
    }
}