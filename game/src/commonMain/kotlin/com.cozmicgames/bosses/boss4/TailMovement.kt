package com.cozmicgames.bosses.boss4

import com.cozmicgames.Constants
import com.cozmicgames.Game
import com.cozmicgames.utils.lerpAngle
import com.littlekt.math.PI_F
import com.littlekt.math.geom.Angle
import com.littlekt.math.geom.degrees
import com.littlekt.util.seconds
import kotlin.math.E
import kotlin.math.pow
import kotlin.math.sin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

sealed interface TailMovement {
    fun updateParts(delta: Duration, tail: Tail)

    fun reset() {}
}

private const val TAIL_PARALYZED_FACTOR = 0.05f

class WaveTailMovement(val maxAngle: Angle, val frequency: Float, val smoothFactor: Float) : TailMovement {
    private val randomOffset = Game.random.nextFloat() * 2.0f * PI_F
    private var time = 0.0.seconds

    override fun updateParts(delta: Duration, tail: Tail) {
        time += delta

        tail.parts.forEachIndexed { index, part ->
            val waveFactor = (index.toFloat() / tail.parts.size.toFloat()).pow(2)
            val targetAngle = maxAngle * sin(time.seconds * frequency + waveFactor * 2.0f * PI_F + randomOffset)
            part.partRotation = lerpAngle(part.partRotation, targetAngle, if (tail.isParalyzed) smoothFactor * TAIL_PARALYZED_FACTOR else smoothFactor)
        }
    }

    override fun reset() {
        time = 0.0.seconds
    }
}

open class CompoundTentacleMovement(movements: List<TailMovement> = emptyList()) : TailMovement {
    private val movements = movements.toMutableList()

    fun addMovement(movement: TailMovement) {
        movements.add(movement)
    }

    fun removeMovement(movement: TailMovement) {
        movements.remove(movement)
    }

    override fun updateParts(delta: Duration, tail: Tail) {
        movements.forEach { it.updateParts(delta, tail) }
    }

    override fun reset() {
        movements.forEach { it.reset() }
    }
}

open class SequenceTentacleMovement(private val durationPerMovement: Duration, private val movements: List<TailMovement>) : TailMovement {
    private var timer = 0.0.seconds
    private var currentMovementIndex = 0

    override fun updateParts(delta: Duration, tail: Tail) {
        timer += delta

        if (timer >= durationPerMovement) {
            timer = 0.0.seconds
            currentMovementIndex = (currentMovementIndex + 1) % movements.size
        }

        movements[currentMovementIndex].updateParts(delta, tail)
    }

    override fun reset() {
        timer = 0.0.seconds
        movements.forEach { it.reset() }
    }
}

class IdleTailMovement : CompoundTentacleMovement(
    listOf(
        WaveTailMovement(8.0.degrees, 2.0f, 0.1f)
    )
)
