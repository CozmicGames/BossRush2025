package com.cozmicgames.states.boss1

import com.cozmicgames.Constants
import com.cozmicgames.Game
import com.cozmicgames.entities.animations.EntityAnimation
import com.cozmicgames.utils.lerpAngle
import com.littlekt.Releasable
import com.littlekt.graphics.g2d.SpriteBatch
import com.littlekt.math.geom.Angle
import com.littlekt.math.geom.degrees
import com.littlekt.util.seconds
import kotlin.math.pow
import kotlin.math.sin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class Tentacle(val index: Int, val flip: Boolean) : Releasable {
    var x = 0.0f
    var y = 0.0f
    var rotation = 0.0.degrees

    private val parts: List<TentaclePart>
    private var time = (Game.random.nextDouble() * 1000.0).seconds

    init {
        val parts = arrayListOf<TentaclePart>()

        repeat(Constants.BOSS1_TENTACLE_PARTS) {
            parts.add(TentaclePart(this, if (it > 0) parts[it - 1] else null, flip, it))
        }

        this.parts = parts

        for (part in parts) {
            Game.entities.add(part)
            Game.physics.addCollider(part.collider)
        }
    }

    private fun waveMovement(maxAngle: Angle = 5.0.degrees, frequency: Float = 3.0f, smoothFactor: Float = 0.2f) {
        val targetAngle = maxAngle * sin(time.seconds * frequency)

        parts[0].tentacleRotation = lerpAngle(parts[0].tentacleRotation, targetAngle, smoothFactor)

        for (i in 1 until parts.size) {
            parts[i].tentacleRotation = lerpAngle(parts[i].tentacleRotation, parts[i - 1].tentacleRotation, smoothFactor)
        }
    }

    private fun grabMovement(targetAngle: Angle, grabFactor: Float = 0.1f) {
        parts.forEachIndexed { index, part ->
            val grabAngle = lerpAngle(part.tentacleRotation, targetAngle / (index + 1), grabFactor)
            val curlAngle = lerpAngle(part.tentacleRotation, (Constants.BOSS1_TENTACLE_PARTS * 0.1f).degrees * index, grabFactor)

            val curlFactor = (index.toFloat() / parts.size.toFloat()).pow(2)
            part.tentacleRotation = lerpAngle(grabAngle, curlAngle, curlFactor)
        }
    }

    private fun curlMovement(curlFactor: Float = 0.05f) {
        parts.forEachIndexed { index, part ->
            val curlAngle = (Constants.BOSS1_TENTACLE_PARTS * 0.08f).degrees * index
            part.tentacleRotation = lerpAngle(part.tentacleRotation, curlAngle, curlFactor)
        }
    }

    private fun hangMovement() {
        parts.forEachIndexed { index, part ->
            val hangAngle = (Constants.BOSS1_TENTACLE_PARTS * 0.08f).degrees * index
            part.tentacleRotation = lerpAngle(part.tentacleRotation, -hangAngle, 0.05f)
        }
    }

    fun update(delta: Duration, movement: Movement) {
        time += delta

        when (movement) {
            is WaveMovement -> waveMovement(movement.maxAngle, movement.frequency, movement.smoothFactor)
            is GrabMovement -> grabMovement(movement.targetAngle, movement.grabFactor)
            is CurlMovement -> curlMovement(movement.curlFactor)
        }

        hangMovement()
    }

    fun render(batch: SpriteBatch) {
        for (part in parts)
            part.render(batch)
    }

    fun addEntityAnimation(animation: EntityAnimation) {
        for (part in parts)
            part.addEntityAnimation(animation)
    }

    override fun release() {
        for (part in parts) {
            Game.entities.remove(part)
            Game.physics.removeCollider(part.collider)
        }
    }
}