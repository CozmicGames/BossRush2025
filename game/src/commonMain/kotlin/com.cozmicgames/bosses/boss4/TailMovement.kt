package com.cozmicgames.bosses.boss4

import com.cozmicgames.Game
import com.cozmicgames.bosses.BossTarget
import com.cozmicgames.utils.lerpAngle
import com.littlekt.math.PI_F
import com.littlekt.math.geom.Angle
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.radians
import com.littlekt.util.seconds
import kotlin.math.atan2
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

class StretchTailMovement(val smoothFactor: Float) : TailMovement {
    override fun updateParts(delta: Duration, tail: Tail) {
        tail.parts.forEachIndexed { index, part ->
            part.partRotation = lerpAngle(part.partRotation, 0.0.degrees, if (tail.isParalyzed) smoothFactor * TAIL_PARALYZED_FACTOR else smoothFactor)
        }
    }
}

class PierceTailMovement(val target: BossTarget, val smoothFactor: Float) : TailMovement {
    override fun updateParts(delta: Duration, tail: Tail) {
        val targetAngle = if (target.y > tail.y) {
            if (target.x > tail.x)
                atan2(target.y - tail.y, target.x - tail.x).radians + 270.0.degrees
            else
                atan2(target.y - tail.y, target.x - tail.x).radians - 450.0.degrees
        } else {
            if (target.x > tail.x)
                atan2(target.y - tail.y, target.x - tail.x).radians - 270.0.degrees
            else
                atan2(target.y - tail.y, target.x - tail.x).radians + 450.0.degrees
        }

        tail.parts.forEachIndexed { index, part ->
            val factor = index.toFloat() / (tail.parts.size - 1).toFloat()
            val partTargetAngle = targetAngle / tail.parts.size * (factor * factor - 2.0f * factor + 1.0f)
            part.partRotation = lerpAngle(part.partRotation, partTargetAngle, if (tail.isParalyzed) smoothFactor * TAIL_PARALYZED_FACTOR else smoothFactor)
        }
    }
}

class ShakeTailMovement(val getStrength: () -> Float = { 1.0f }) : TailMovement {
    override fun updateParts(delta: Duration, tail: Tail) {
        val amplitude = 5.0.degrees * getStrength()

        val targetAngle = (amplitude - amplitude * 0.5f) * Game.random.nextFloat()

        tail.parts[0].partRotation = lerpAngle(tail.parts[0].partRotation, targetAngle, 0.6f)

        for (i in 1 until tail.parts.size) {
            tail.parts[i].partRotation = lerpAngle(tail.parts[i].partRotation, 0.0.degrees, 0.3f)
        }
    }
}

class ParalyzedTailMovement() : TailMovement {
    override fun updateParts(delta: Duration, tail: Tail) {
        val amplitude = 5.0.degrees

        val targetAngle = (amplitude - amplitude * 0.5f) * Game.random.nextFloat()

        tail.parts[0].partRotation = lerpAngle(tail.parts[0].partRotation, targetAngle, 0.05f)

        for (i in 1 until tail.parts.size) {
            tail.parts[i].partRotation = lerpAngle(tail.parts[i].partRotation, 0.0.degrees, 0.05f)
        }
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
