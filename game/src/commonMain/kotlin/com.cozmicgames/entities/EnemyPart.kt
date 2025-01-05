package com.cozmicgames.entities

import com.cozmicgames.Game
import com.cozmicgames.entities.animations.EntityAnimation
import com.cozmicgames.entities.animations.HitAnimation
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.RectangleCollisionShape
import com.littlekt.graphics.Color
import com.littlekt.graphics.g2d.SpriteBatch
import com.littlekt.math.geom.degrees
import kotlin.time.Duration

class EnemyPart(id: String) : Entity(id) {
    override val renderLayer = 10

    var x = 0.0f
    var y = 0.0f
    var rotation = 0.0f.degrees

    override val collider = Collider(RectangleCollisionShape(128.0f, 128.0f, 0.0f.degrees), this)

    //TODO: Support multiple animations
    private var currentAnimation: EntityAnimation? = null

    init {
        Game.physics.addCollider(collider)
    }

    override fun update(delta: Duration) {
        currentAnimation?.let {
            if (it.update(delta))
                currentAnimation = null
        }

        collider.x = x
        collider.y = y
        (collider.shape as RectangleCollisionShape).angle = rotation
        collider.update()
        x = collider.x
        y = collider.y
    }

    override fun render(batch: SpriteBatch) {
        val animation = currentAnimation

        val color = animation?.color ?: Color.WHITE
        val scaleX = animation?.scale ?: 1.0f
        val scaleY = animation?.scale ?: 1.0f

        batch.draw(Game.resources.testEnemy, x, y, originX = 64.0f, originY = 64.0f, rotation = rotation, width = 128.0f, height = 128.0f, scaleX = scaleX, scaleY = scaleY, color = color)
    }

    override fun playHitAnimation() {
        if (currentAnimation == null) {
            currentAnimation = HitAnimation()
        }
    }
}