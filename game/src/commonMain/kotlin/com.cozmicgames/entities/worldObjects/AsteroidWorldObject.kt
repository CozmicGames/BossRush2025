package com.cozmicgames.entities.worldObjects

import com.cozmicgames.Game
import com.cozmicgames.graphics.RenderLayers
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.physics.CircleCollisionShape
import com.cozmicgames.physics.Collider
import com.cozmicgames.utils.Difficulty
import com.littlekt.math.geom.Angle
import com.littlekt.math.geom.cosine
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.sine
import com.littlekt.util.seconds
import kotlin.time.Duration

class AsteroidWorldObject(index: Int) : WorldObject("asteroid$index"), PlayerDamageSource {
    companion object {
        private const val MIN_SIZE = 32.0f
        private const val MAX_SIZE = 128.0f

        private const val MIN_ROTATION_SPEED = -30.0f
        private const val MAX_ROTATION_SPEED = 30.0f

        private val MIN_DIRECTION_ANGLE = (-10.0).degrees
        private val MAX_DIRECTION_ANGLE = (-50.0).degrees

        private const val MIN_SPEED = 50.0f
        private const val MAX_SPEED = 100.0f
    }

    override val damageSourceX get() = x
    override val damageSourceY get() = y

    var size = 0.0f

    private var directionX = 0.0f
    private var directionY = 0.0f
    private var speed = 0.0f
    private var rotationSpeed = 0.0f

    override var collider = Collider(CircleCollisionShape(0.0f), this)

    fun reset(x: Float, y: Float) {
        this.x = x
        this.y = y

        val directionAngle = MIN_DIRECTION_ANGLE + (MAX_DIRECTION_ANGLE - MIN_DIRECTION_ANGLE) * Game.random.nextFloat()
        directionX = directionAngle.cosine
        directionY = directionAngle.sine
        speed = MIN_SPEED + Game.random.nextFloat() * (MAX_SPEED - MIN_SPEED)
        rotation = 360.0.degrees * Game.random.nextFloat()
        size = MIN_SIZE + Game.random.nextFloat() * (MAX_SIZE - MIN_SIZE)
        rotationSpeed = MIN_ROTATION_SPEED + Game.random.nextFloat() * (MAX_ROTATION_SPEED - MIN_ROTATION_SPEED)
        collider = Collider(CircleCollisionShape(size * 0.5f), this)
    }

    override fun updateWorldObject(delta: Duration, fightStarted: Boolean) {
        rotation += rotationSpeed.degrees * delta.seconds

        x += directionX * speed * delta.seconds
        y += directionY * speed * delta.seconds

        collider.update(x, y)
    }

    override fun render(renderer: Renderer) {
        renderer.submit(RenderLayers.ASTEROIDS_BEGIN) {
            it.draw(Game.resources.asteroid0, x, y, size * 0.5f, size * 0.5f, width = size, height = size, rotation = rotation)
        }
    }
}