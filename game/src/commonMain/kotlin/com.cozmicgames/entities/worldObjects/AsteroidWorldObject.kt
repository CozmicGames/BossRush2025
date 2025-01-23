package com.cozmicgames.entities.worldObjects

import com.cozmicgames.Game
import com.cozmicgames.graphics.RenderLayers
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.physics.CircleCollisionShape
import com.cozmicgames.physics.Collider
import com.cozmicgames.utils.Difficulty
import com.littlekt.math.geom.degrees
import com.littlekt.util.seconds
import kotlin.time.Duration

class AsteroidWorldObject(index: Int, private val directionX: Float, private val directionY: Float, private val speed: Float, private val difficulty: Difficulty) : WorldObject("asteroid$index") {
    companion object {
        private const val MIN_SIZE = 32.0f
        private const val MAX_SIZE = 128.0f

        private const val MIN_ROTATION_SPEED = 5.0f
        private const val MAX_ROTATION_SPEED = 30.0f
    }

    private val size = MIN_SIZE + Game.random.nextFloat() * (MAX_SIZE - MIN_SIZE)
    private val rotationSpeed = MIN_ROTATION_SPEED + Game.random.nextFloat() * (MAX_ROTATION_SPEED - MIN_ROTATION_SPEED)

    override val collider = Collider(CircleCollisionShape(size), this)

    override fun updateWorldObject(delta: Duration, fightStarted: Boolean) {
        rotation += rotationSpeed.degrees * delta.seconds

        x += directionX * speed * delta.seconds
        y += directionY * speed * delta.seconds
    }

    override fun render(renderer: Renderer) {
        renderer.submit(RenderLayers.ASTEROIDS_BEGIN) {
            it.draw(Game.resources.asteroid0, x, y, size * 0.5f, size * 0.5f, width = size, height = size, rotation = rotation)
        }
    }

    override fun onAddToWorld() {
        Game.world.add(this)
    }

    override fun onAddToPhysics() {
        if (difficulty == Difficulty.EASY)
            return

        Game.physics.addCollider(collider)
    }

    override fun onRemoveFromWorld() {
        Game.world.remove(this)
    }

    override fun onRemoveFromPhysics() {
        Game.physics.removeCollider(collider)
    }
}