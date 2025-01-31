package com.cozmicgames.graphics.particles.effects

import com.cozmicgames.Game
import com.cozmicgames.graphics.particles.ParticleEffect
import com.cozmicgames.utils.toHsv
import com.littlekt.graphics.Color
import com.littlekt.math.geom.degrees
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class ExplosionEffect() : ParticleEffect() {
    companion object {
        private const val MIN_SATURATION = 0.7f
        private const val MAX_SATURATION = 1.0f

        private const val MIN_BRIGHTNESS = 0.6f
        private const val MAX_BRIGHTNESS = 1.2f

        private const val MIN_ALPHA = 0.8f
        private const val MAX_ALPHA = 1.0f

        private const val MIN_SIZE = 15.0f
        private const val MAX_SIZE = 25.0f

        private val MIN_LIFETIME = 3.0.seconds
        private val MAX_LIFETIME = 5.0.seconds

        private val MIN_SPEED = 100.0f
        private val MAX_SPEED = 200.0f
    }

    constructor(x: Float, y: Float) : this() {
        this.x = x
        this.y = y
    }

    override val name = "EXPLOSION"

    override val duration = 5.0.seconds

    var x = 0.0f
    var y = 0.0f
    val baseColor = Color.fromHex("ad0d40")

    private var isFirstUpdate = true

    override fun updateSystem(delta: Duration) {
        fun spawn() {
            val (h, s, v) = baseColor.toHsv()
            val saturation = s * (MIN_SATURATION + (MAX_SATURATION - MIN_SATURATION) * Game.random.nextFloat())
            val brightness = v * (MIN_BRIGHTNESS + (MAX_BRIGHTNESS - MIN_BRIGHTNESS) * Game.random.nextFloat())
            val alpha = MIN_ALPHA + (MAX_ALPHA - MIN_ALPHA) * Game.random.nextFloat()
            val color = Color.fromHsv(h, saturation, brightness, alpha)

            val size = MIN_SIZE + (MAX_SIZE - MIN_SIZE) * Game.random.nextFloat()

            val lifeTime = MIN_LIFETIME + (MAX_LIFETIME - MIN_LIFETIME) * Game.random.nextDouble()

            val rotation = 360.0.degrees * Game.random.nextFloat()

            val speed = MIN_SPEED + (MAX_SPEED - MIN_SPEED) * Game.random.nextFloat()

            val direction = 360.0.degrees * Game.random.nextFloat()

            spawn(x, y, direction, speed, lifeTime, size, rotation, color)
        }

        if (isFirstUpdate) {
            repeat(100) {
                spawn()
            }

            isFirstUpdate = false
        }
    }
}