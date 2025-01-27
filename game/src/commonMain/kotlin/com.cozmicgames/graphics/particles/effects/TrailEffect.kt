package com.cozmicgames.graphics.particles.effects

import com.cozmicgames.Constants
import com.cozmicgames.Game
import com.cozmicgames.graphics.particles.ParticleEffect
import com.cozmicgames.utils.toHsv
import com.littlekt.graphics.Color
import com.littlekt.graphics.MutableColor
import com.littlekt.graphics.toRgba8888
import com.littlekt.math.geom.Angle
import com.littlekt.math.geom.cosine
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.sine
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class TrailEffect() : ParticleEffect() {
    companion object {
        private const val MIN_SATURATION = 0.7f
        private const val MAX_SATURATION = 1.0f

        private const val MIN_BRIGHTNESS = 0.8f
        private const val MAX_BRIGHTNESS = 1.5f

        private const val MIN_ALPHA = 0.3f
        private const val MAX_ALPHA = 0.7f

        private const val MIN_SIZE = 6.0f
        private const val MAX_SIZE = 10.0f

        private val MIN_LIFETIME = 2.0.seconds
        private val MAX_LIFETIME = 3.0.seconds

        private val MIN_SPEED = 200.0f
        private val MAX_SPEED = 250.0f

        private val SPREAD = 50.0.degrees
    }

    constructor(playerShipId: String, leftEngine: Boolean) : this() {
        this.playerShipId = playerShipId
        this.leftEngine = leftEngine
    }

    override val duration = null

    var playerShipId = ""
    var leftEngine = false

    var x = 0.0f
    var y = 0.0f
    var direction = 0.0.degrees
    val baseColor = Color.fromHex("dbfeff")

    private var previousPlayerShipX = 0.0f
    private var previousPlayerShipY = 0.0f
    private var previousPlayerShipRotation = 0.0.degrees

    override fun updateSystem(delta: Duration) {
        val ship = Game.players.getById(playerShipId)?.ship

        if (ship == null) {
            setShouldBeRemoved()
            return
        }

        val currentPlayerShipX = ship.x
        val currentPlayerShipY = ship.y
        val currentPlayerShipRotation = ship.rotation

        var shouldSpawn = false

        if (!leftEngine && currentPlayerShipRotation < previousPlayerShipRotation - 1.0.degrees)
            shouldSpawn = true
        else if (leftEngine && currentPlayerShipRotation > previousPlayerShipRotation + 1.0.degrees)
            shouldSpawn = true

        if (currentPlayerShipX != previousPlayerShipX || currentPlayerShipY != previousPlayerShipY)
            shouldSpawn = true

        previousPlayerShipX = currentPlayerShipX
        previousPlayerShipY = currentPlayerShipY
        previousPlayerShipRotation = currentPlayerShipRotation

        val offsetX = -Constants.PLAYER_SHIP_WIDTH * 0.34f
        val offsetY = if (leftEngine) -Constants.PLAYER_SHIP_HEIGHT * 0.4f else Constants.PLAYER_SHIP_HEIGHT * 0.4f

        val cos = currentPlayerShipRotation.cosine
        val sin = currentPlayerShipRotation.sine

        x = currentPlayerShipX + offsetX * cos - offsetY * sin
        y = currentPlayerShipY + offsetX * sin + offsetY * cos
        direction = currentPlayerShipRotation + 180.0.degrees

        if (shouldSpawn) {
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
