package com.cozmicgames.graphics.particles.effects

import com.cozmicgames.Game
import com.cozmicgames.graphics.particles.ParticleEffect
import com.cozmicgames.utils.toHsv
import com.littlekt.graphics.Color
import com.littlekt.math.geom.Angle
import com.littlekt.math.geom.degrees
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class DeathSplatterEffect() : ParticleEffect() {
    companion object {
        private const val MIN_SATURATION = 0.7f
        private const val MAX_SATURATION = 1.0f

        private const val MIN_BRIGHTNESS = 0.6f
        private const val MAX_BRIGHTNESS = 1.2f

        private const val MIN_ALPHA = 0.3f
        private const val MAX_ALPHA = 0.7f

        private const val MIN_SIZE = 15.0f
        private const val MAX_SIZE = 25.0f

        private val MIN_LIFETIME = 3.0.seconds
        private val MAX_LIFETIME = 5.0.seconds

        private val MIN_SPEED = 200.0f
        private val MAX_SPEED = 250.0f

        private val SPREAD = 120.0.degrees
    }

    constructor(x: Float, y: Float, direction: Angle): this() {
        this.x = x
        this.y = y
        this.direction = direction
    }

    override val duration = 5.0.seconds

    var x = 0.0f
    var y = 0.0f
    var direction = 0.0.degrees
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

            val direction = direction + SPREAD * (Game.random.nextFloat() - 0.5f)

            spawn(x, y, direction, speed, lifeTime, size, rotation, color)
        }

        if (isFirstUpdate) {
            repeat(10) {
                spawn()
            }

            isFirstUpdate = false
        }
    }

    override fun writeUpdateData() {
        Game.players.setGlobalState("particleEffect${id}x", x)
        Game.players.setGlobalState("particleEffect${id}y", y)
        Game.players.setGlobalState("particleEffect${id}direction", direction.degrees)
        Game.players.setGlobalState("particleEffect${id}remove", shouldBeRemoved)
    }

    override fun readUpdateData() {
        x = Game.players.getGlobalState("particleEffect${id}x") ?: 0.0f
        y = Game.players.getGlobalState("particleEffect${id}y") ?: 0.0f
        direction = Game.players.getGlobalState<Float>("particleEffect${id}direction")?.degrees ?: 0.0.degrees
        if (Game.players.getGlobalState<Boolean>("particleEffect${id}remove") == true)
            setShouldBeRemoved()
    }
}