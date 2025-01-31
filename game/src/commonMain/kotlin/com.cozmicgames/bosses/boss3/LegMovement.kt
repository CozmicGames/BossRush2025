package com.cozmicgames.bosses.boss3

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

sealed interface LegMovement {
    fun updateParts(delta: Duration, leg: Leg)

    fun reset() {}
}

private const val LEG_PARALYZED_FACTOR = 0.05f

class SwayLegMovement(val maxAngle: Angle, val frequency: Float, val smoothFactor: Float) : LegMovement {
    private val randomOffset = Game.random.nextFloat()
    private var time = 0.0.seconds

    private val weights = arrayOf(0.1f, 0.6f, 0.3f)

    override fun updateParts(delta: Duration, leg: Leg) {
        time += delta

        for (i in 0 until leg.parts.size) {
            val targetAngle = maxAngle * (sin(time.seconds * frequency + randomOffset + leg.index * PI_F) + 1.0f) * 0.5f * weights[i]
            leg.parts[i].legRotation = lerpAngle(leg.parts[i].legRotation, targetAngle, if (leg.isParalyzed) smoothFactor * LEG_PARALYZED_FACTOR else smoothFactor)
        }
    }

    override fun reset() {
        time = 0.0.seconds
    }
}

class StretchDownLegMovement(val stretchFactor: Float) : LegMovement {
    companion object {
        private val TARGET_ANGLES = arrayOf(
            (-55.0).degrees,
            (-50.0).degrees,
            (-45.0).degrees,
            (-55.0).degrees,
            (-50.0).degrees,
            (-45.0).degrees
        )
    }

    override fun updateParts(delta: Duration, leg: Leg) {
        val legTargetAngle = TARGET_ANGLES[leg.index]

        leg.parts[0].legRotation = lerpAngle(leg.parts[0].legRotation, legTargetAngle, if (leg.isParalyzed) stretchFactor * LEG_PARALYZED_FACTOR else stretchFactor)

        for (i in 1 until leg.parts.size) {
            val targetAngleWeight = (i) / (4.0 * leg.parts.size)
            val partTargetAngle = (-5).degrees * E.pow(targetAngleWeight)
            leg.parts[i].legRotation = lerpAngle(leg.parts[i].legRotation, partTargetAngle, if (leg.isParalyzed) stretchFactor * LEG_PARALYZED_FACTOR else stretchFactor)
        }
    }
}

class HangLegMovement : LegMovement {
    override fun updateParts(delta: Duration, leg: Leg) {
        leg.parts.forEachIndexed { index, part ->
            val hangFactor = (1.0f - index.toFloat() / leg.parts.size.toFloat()).pow(2)
            val hangAngle = 100.0.degrees / leg.parts.size * hangFactor
            part.legRotation = lerpAngle(part.legRotation, -hangAngle, if (leg.isParalyzed) hangFactor * LEG_PARALYZED_FACTOR else hangFactor)
        }
    }
}

class StretchOutLegMovement(val factor: Float) : LegMovement {
    companion object {
        private val TARGET_ANGLES = arrayOf(
            35.0.degrees,
            (-15.0).degrees,
            (-32.0).degrees,
            35.0.degrees,
            (-15.0).degrees,
            (-32.0).degrees
        )
    }

    override fun updateParts(delta: Duration, leg: Leg) {
        leg.parts.forEachIndexed { index, part ->
            val stretchAngleStrength = (1.0f - index.toFloat() / leg.parts.size.toFloat()).pow(4.0f)
            val stretchAngle = TARGET_ANGLES[leg.index] * stretchAngleStrength
            part.legRotation = lerpAngle(part.legRotation, stretchAngle, if (leg.isParalyzed) factor * LEG_PARALYZED_FACTOR else factor)
        }
    }
}

class DefendLegMovement(val smoothFactor: Float = 0.1f) : LegMovement {
    companion object {
        private val TARGET_ANGLES = arrayOf(
            (-40.0).degrees,
            (-50.0).degrees,
            (-60.0).degrees,
            (-40.0).degrees,
            (-50.0).degrees,
            (-60.0).degrees,
        )
    }

    override fun updateParts(delta: Duration, leg: Leg) {
        leg.parts.forEachIndexed { index, part ->
            val defendAngleStrength = (1.0f - index.toFloat() / leg.parts.size.toFloat())
            val defendAngle = TARGET_ANGLES[leg.index] * defendAngleStrength
            part.legRotation = lerpAngle(part.legRotation, defendAngle, if (leg.isParalyzed) smoothFactor * LEG_PARALYZED_FACTOR else smoothFactor)
        }
    }
}

open class CompoundLegMovement(movements: List<LegMovement> = emptyList()) : LegMovement {
    private val movements = movements.toMutableList()

    fun addMovement(movement: LegMovement) {
        movements.add(movement)
    }

    fun removeMovement(movement: LegMovement) {
        movements.remove(movement)
    }

    override fun updateParts(delta: Duration, leg: Leg) {
        movements.forEach { it.updateParts(delta, leg) }
    }

    override fun reset() {
        movements.forEach { it.reset() }
    }
}

open class SequenceLegMovement(private val durationPerMovement: Duration, private val movements: List<LegMovement>) : LegMovement {
    private var timer = 0.0.seconds
    private var currentMovementIndex = 0

    override fun updateParts(delta: Duration, leg: Leg) {
        timer += delta

        if (timer >= durationPerMovement) {
            timer = 0.0.seconds
            currentMovementIndex = (currentMovementIndex + 1) % movements.size
        }

        movements[currentMovementIndex].updateParts(delta, leg)
    }

    override fun reset() {
        timer = 0.0.seconds
        movements.forEach { it.reset() }
    }
}

class IdleLegMovement : CompoundLegMovement(
    listOf(
        HangLegMovement(),
        SwayLegMovement((-100.0).degrees, 0.2f, 0.5f),
    )
)

class ParalyzedLegMovement : CompoundLegMovement(
    listOf(
        StretchDownLegMovement(0.3f)
    )
)

class DeadLegMovement : CompoundLegMovement(
    listOf(
        HangLegMovement()
    )
)
