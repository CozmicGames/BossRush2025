package com.cozmicgames.states.boss1

import com.cozmicgames.Constants
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

sealed interface TentacleMovement {
    fun updateParts(delta: Duration, tentacle: Tentacle)
}

class SwayTentacleMovement(val maxAngle: Angle, val frequency: Float, val smoothFactor: Float) : TentacleMovement {
    private val randomOffset = Game.random.nextFloat() * 2.0f * PI_F
    private var time = 0.0.seconds

    override fun updateParts(delta: Duration, tentacle: Tentacle) {
        time += delta

        val targetAngle = maxAngle * sin(time.seconds * frequency + randomOffset)

        tentacle.parts[0].tentacleRotation = lerpAngle(tentacle.parts[0].tentacleRotation, targetAngle, smoothFactor)

        for (i in 1 until tentacle.parts.size) {
            tentacle.parts[i].tentacleRotation = lerpAngle(tentacle.parts[i].tentacleRotation, tentacle.parts[i - 1].tentacleRotation, smoothFactor)
        }
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
            part.tentacleRotation = lerpAngle(part.tentacleRotation, targetAngle, smoothFactor)
        }
    }
}

class StretchOutTentacleMovement(val stretchFactor: Float) : TentacleMovement {
    companion object {
        private val TARGET_ANGLES = arrayOf(
            70.0.degrees,
            35.0.degrees,
            (0.0).degrees,
            (-35.0).degrees,
            (-70.0).degrees,
            (-35.0).degrees,
            0.0.degrees,
            35.0.degrees
        )
    }

    override fun updateParts(delta: Duration, tentacle: Tentacle) {
        val targetAngle = TARGET_ANGLES[tentacle.index]

        tentacle.parts.forEach { part ->
            part.tentacleRotation = lerpAngle(part.tentacleRotation, targetAngle, stretchFactor)
        }
    }
}

class GrabTentacleMovement(val targetAngle: Angle, val grabFactor: Float) : TentacleMovement {
    override fun updateParts(delta: Duration, tentacle: Tentacle) {
        tentacle.parts.forEachIndexed { index, part ->
            val grabAngle = lerpAngle(part.tentacleRotation, targetAngle / (index + 1), grabFactor)
            val curlAngle = lerpAngle(part.tentacleRotation, (Constants.BOSS1_TENTACLE_PARTS * 0.1f).degrees * index, grabFactor)

            val curlFactor = (index.toFloat() / tentacle.parts.size.toFloat()).pow(2)
            part.tentacleRotation = lerpAngle(grabAngle, curlAngle, curlFactor)
        }
    }
}

class CurlTentacleMovement(val curlFactor: Float) : TentacleMovement {
    override fun updateParts(delta: Duration, tentacle: Tentacle) {
        tentacle.parts.forEachIndexed { index, part ->
            val curlAngle = (Constants.BOSS1_TENTACLE_PARTS * 0.08f).degrees * index
            part.tentacleRotation = lerpAngle(part.tentacleRotation, curlAngle, curlFactor)
        }
    }
}

class HangTentacleMovement : TentacleMovement {
    override fun updateParts(delta: Duration, tentacle: Tentacle) {
        tentacle.parts.forEachIndexed { index, part ->
            val hangFactor = (1.0f - index.toFloat() / tentacle.parts.size.toFloat()).pow(2)
            val hangAngle = 200.0.degrees / Constants.BOSS1_TENTACLE_PARTS * hangFactor
            part.tentacleRotation = lerpAngle(part.tentacleRotation, -hangAngle, hangFactor)
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
}