package com.cozmicgames.bosses.tutorialBoss

import com.cozmicgames.Game
import com.cozmicgames.utils.lerpAngle
import com.littlekt.math.PI_F
import com.littlekt.math.geom.Angle
import com.littlekt.math.geom.degrees
import com.littlekt.util.seconds
import kotlin.math.pow
import kotlin.math.sin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

sealed interface BodyMovement {
    fun updateParts(delta: Duration, body: Body)

    fun reset() {}
}

private const val BODY_PARALYZED_FACTOR = 0.05f

open class WaveBodyMovement(val maxAngle: Angle, val frequency: Float, val smoothFactor: Float) : BodyMovement {
    private val randomOffset = Game.random.nextFloat() * 2.0f * PI_F
    private var time = 0.0.seconds

    override fun updateParts(delta: Duration, body: Body) {
        time += delta

        body.parts.forEachIndexed { index, part ->
            val waveFactor = (index.toFloat() / body.parts.size.toFloat()).pow(2)
            val targetAngle = maxAngle * sin(time.seconds * frequency + waveFactor * 2.0f * PI_F + randomOffset)
            part.partRotation = lerpAngle(part.partRotation, targetAngle, if (body.boss.isParalyzed) smoothFactor * BODY_PARALYZED_FACTOR else smoothFactor)
        }
    }

    override fun reset() {
        time = 0.0.seconds
    }
}

open class SequenceBodyMovement(private val durationPerMovement: Duration, private val movements: List<BodyMovement>) : BodyMovement {
    private var timer = 0.0.seconds
    private var currentMovementIndex = 0

    override fun updateParts(delta: Duration, body: Body) {
        timer += delta

        if (timer >= durationPerMovement) {
            timer = 0.0.seconds
            currentMovementIndex = (currentMovementIndex + 1) % movements.size
        }

        movements[currentMovementIndex].updateParts(delta, body)
    }

    override fun reset() {
        timer = 0.0.seconds
        movements.forEach { it.reset() }
    }
}

class IdleBodyMovement : WaveBodyMovement(3.0.degrees, 1.5f, 0.1f)

class ParalyzedBodyMovement : WaveBodyMovement(2.0.degrees, 0.5f, 0.1f)
