package com.cozmicgames.states.boss1

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

sealed interface TentacleMovement {
    fun updateParts(delta: Duration, tentacle: Tentacle)

    fun reset() {}
}

private const val TENTACLE_PARALYZED_FACTOR = 0.05f

class SwayTentacleMovement(val maxAngle: Angle, val frequency: Float, val smoothFactor: Float) : TentacleMovement {
    private val randomOffset = Game.random.nextFloat() * 2.0f * PI_F
    private var time = 0.0.seconds

    override fun updateParts(delta: Duration, tentacle: Tentacle) {
        time += delta

        val targetAngle = maxAngle * sin(time.seconds * frequency + randomOffset)

        tentacle.parts[0].tentacleRotation = lerpAngle(tentacle.parts[0].tentacleRotation, targetAngle, if (tentacle.isParalyzed) smoothFactor * TENTACLE_PARALYZED_FACTOR else smoothFactor)

        for (i in 1 until tentacle.parts.size) {
            tentacle.parts[i].tentacleRotation = lerpAngle(tentacle.parts[i].tentacleRotation, tentacle.parts[i - 1].tentacleRotation, if (tentacle.isParalyzed) smoothFactor * TENTACLE_PARALYZED_FACTOR else smoothFactor)
        }
    }

    override fun reset() {
        time = 0.0.seconds
    }
}

class WaveTentacleMovement(val maxAngle: Angle, val frequency: Float, val smoothFactor: Float) : TentacleMovement {
    private val randomOffset = Game.random.nextFloat() * 2.0f * PI_F
    private var time = 0.0.seconds

    override fun updateParts(delta: Duration, tentacle: Tentacle) {
        time += delta

        tentacle.parts.forEachIndexed { index, part ->
            val waveFactor = (index.toFloat() / tentacle.parts.size.toFloat()).pow(2)
            val targetAngle = maxAngle * sin(time.seconds * frequency + waveFactor * 2.0f * PI_F + randomOffset)
            part.tentacleRotation = lerpAngle(part.tentacleRotation, targetAngle, if (tentacle.isParalyzed) smoothFactor * TENTACLE_PARALYZED_FACTOR else smoothFactor)
        }
    }

    override fun reset() {
        time = 0.0.seconds
    }
}

class StretchDownTentacleMovement(val stretchFactor: Float) : TentacleMovement {
    companion object {
        private val TARGET_ANGLES = arrayOf(
            (-60.0).degrees,
            (-55.0).degrees,
            (-50.0).degrees,
            (-45.0).degrees,
            (-60.0).degrees,
            (-55.0).degrees,
            (-50.0).degrees,
            (-45.0).degrees
        )
    }

    override fun updateParts(delta: Duration, tentacle: Tentacle) {
        val tentacleTargetAngle = TARGET_ANGLES[tentacle.index]

        tentacle.parts[0].tentacleRotation = lerpAngle(tentacle.parts[0].tentacleRotation, tentacleTargetAngle, if (tentacle.isParalyzed) stretchFactor * TENTACLE_PARALYZED_FACTOR else stretchFactor)

        for (i in 1 until tentacle.parts.size) {
            val targetAngleWeight = (i) / (4.0 * tentacle.parts.size)
            val partTargetAngle = (-5).degrees * E.pow(targetAngleWeight)
            tentacle.parts[i].tentacleRotation = lerpAngle(tentacle.parts[i].tentacleRotation, partTargetAngle, if (tentacle.isParalyzed) stretchFactor * TENTACLE_PARALYZED_FACTOR else stretchFactor)
        }
    }
}

class StretchOutTentacleMovement(val stretchFactor: Float) : TentacleMovement {
    companion object {
        private val TARGET_ANGLES = arrayOf(
            60.0.degrees,
            35.0.degrees,
            (0.0).degrees,
            (-35.0).degrees,
            60.0.degrees,
            35.0.degrees,
            (0.0).degrees,
            (-35.0).degrees
        )
    }

    override fun updateParts(delta: Duration, tentacle: Tentacle) {
        val targetAngle = TARGET_ANGLES[tentacle.index]

        tentacle.parts[0].tentacleRotation = lerpAngle(tentacle.parts[0].tentacleRotation, targetAngle, if (tentacle.isParalyzed) stretchFactor * TENTACLE_PARALYZED_FACTOR else stretchFactor)

        for (i in 1 until tentacle.parts.size) {
            tentacle.parts[i].tentacleRotation = lerpAngle(tentacle.parts[i].tentacleRotation, 0.0.degrees, if (tentacle.isParalyzed) stretchFactor * TENTACLE_PARALYZED_FACTOR else stretchFactor)
        }
    }
}

class GrabTentacleMovement(val grabFactor: Float) : TentacleMovement {
    override fun updateParts(delta: Duration, tentacle: Tentacle) {
        tentacle.parts.forEachIndexed { index, part ->
            val grabAngle = lerpAngle(part.tentacleRotation, (-20.0).degrees / (index + 1), grabFactor)
            val curlAngle = lerpAngle(part.tentacleRotation, (Constants.BOSS1_TENTACLE_PARTS * 0.1f).degrees * index, grabFactor)

            val curlFactor = (index.toFloat() / tentacle.parts.size.toFloat()).pow(2)
            part.tentacleRotation = lerpAngle(grabAngle, curlAngle, curlFactor)
        }
    }
}

class HangTentacleMovement : TentacleMovement {
    override fun updateParts(delta: Duration, tentacle: Tentacle) {
        tentacle.parts.forEachIndexed { index, part ->
            val hangFactor = (1.0f - index.toFloat() / tentacle.parts.size.toFloat()).pow(2)
            val hangAngle = 200.0.degrees / Constants.BOSS1_TENTACLE_PARTS * hangFactor
            part.tentacleRotation = lerpAngle(part.tentacleRotation, -hangAngle, if (tentacle.isParalyzed) hangFactor * TENTACLE_PARALYZED_FACTOR else hangFactor)
        }
    }
}

class DefendTentacleMovement(val defendFactor: Float = 0.1f) : TentacleMovement {
    companion object {
        private val TARGET_ANGLES = arrayOf(
            47.0.degrees,
            35.0.degrees,
            (-15.0).degrees,
            (-32.0).degrees,
            47.0.degrees,
            35.0.degrees,
            (-15.0).degrees,
            (-32.0).degrees
        )
    }

    override fun updateParts(delta: Duration, tentacle: Tentacle) {
        tentacle.parts.forEachIndexed { index, part ->
            val defendAngleStrength = (1.0f - index.toFloat() / tentacle.parts.size.toFloat()).pow(4.0f)
            val defendAngle = TARGET_ANGLES[tentacle.index] * defendAngleStrength
            part.tentacleRotation = lerpAngle(part.tentacleRotation, defendAngle, if (tentacle.isParalyzed) defendFactor * TENTACLE_PARALYZED_FACTOR else defendFactor)
        }
    }
}

class CompoundTentacleMovement(movements: List<TentacleMovement> = emptyList()) : TentacleMovement {
    private val movements = movements.toMutableList()

    fun addMovement(movement: TentacleMovement) {
        movements.add(movement)
    }

    fun removeMovement(movement: TentacleMovement) {
        movements.remove(movement)
    }

    override fun updateParts(delta: Duration, tentacle: Tentacle) {
        movements.forEach { it.updateParts(delta, tentacle) }
    }

    override fun reset() {
        movements.forEach { it.reset() }
    }
}

class SequenceTentacleMovement(private val durationPerMovement: Duration, private val movements: List<TentacleMovement>) : TentacleMovement {
    private var timer = 0.0.seconds
    private var currentMovementIndex = 0

    override fun updateParts(delta: Duration, tentacle: Tentacle) {
        timer += delta

        if (timer >= durationPerMovement) {
            timer = 0.0.seconds
            currentMovementIndex = (currentMovementIndex + 1) % movements.size
        }

        movements[currentMovementIndex].updateParts(delta, tentacle)
    }

    override fun reset() {
        timer = 0.0.seconds
        movements.forEach { it.reset() }
    }
}
