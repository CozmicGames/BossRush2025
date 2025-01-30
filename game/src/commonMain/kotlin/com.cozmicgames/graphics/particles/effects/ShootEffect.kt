package com.cozmicgames.graphics.particles.effects

import com.cozmicgames.Game
import com.cozmicgames.graphics.particles.ParticleEffect
import com.cozmicgames.utils.toHsv
import com.littlekt.graphics.Color
import com.littlekt.graphics.MutableColor
import com.littlekt.graphics.toRgba8888
import com.littlekt.math.geom.Angle
import com.littlekt.math.geom.degrees
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

abstract class ShootEffect : ParticleEffect() {
    companion object {
        private const val MIN_SATURATION = 0.7f
        private const val MAX_SATURATION = 1.0f

        private const val MIN_BRIGHTNESS = 0.8f
        private const val MAX_BRIGHTNESS = 1.5f

        private const val MIN_ALPHA = 0.7f
        private const val MAX_ALPHA = 1.0f

        private const val MIN_SIZE = 8.0f
        private const val MAX_SIZE = 13.0f

        private val MIN_LIFETIME = 0.5.seconds
        private val MAX_LIFETIME = 1.0.seconds

        private val MIN_SPEED = 150.0f
        private val MAX_SPEED = 250.0f

        private val SPREAD = 100.0.degrees
    }

    var x = 0.0f
    var y = 0.0f
    var direction = 0.0.degrees
    val baseColor = MutableColor()

    private var isFirstUpdate = true

    abstract val isContinouus: Boolean

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

            val direction = direction + SPREAD * (Game.random.nextFloat() - 0.5f)

            spawn(x, y, direction, speed, lifeTime, size, rotation, color)
        }

        if (isFirstUpdate) {
            repeat(10) {
                spawn()
            }

            isFirstUpdate = false
        }

        if (isContinouus)
            spawn()
    }
}

class SingleShotEffect() : ShootEffect() {
    constructor(x: Float, y: Float, direction: Angle, baseColor: Color) : this() {
        this.x = x
        this.y = y
        this.direction = direction
        this.baseColor.set(baseColor)
    }

    override val name = "SHOOT_SINGLE"

    override val duration = 1.0.seconds

    override val isContinouus = false
}

class ContinuousShotEffect() : ShootEffect() {
    constructor(x: Float, y: Float, direction: Angle, baseColor: Color) : this() {
        this.x = x
        this.y = y
        this.direction = direction
        this.baseColor.set(baseColor)
    }

    override val name = "SHOOT_CONTINUOUS"

    override val duration = null

    override val isContinouus = true

    fun stop() {
        setShouldBeRemoved()
    }
}
