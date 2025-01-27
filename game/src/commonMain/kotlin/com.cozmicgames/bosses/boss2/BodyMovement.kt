package com.cozmicgames.bosses.boss2

import com.cozmicgames.Constants
import com.cozmicgames.Game
import com.cozmicgames.bosses.BossTarget
import com.cozmicgames.utils.lerpAngle
import com.littlekt.math.PI_F
import com.littlekt.math.geom.Angle
import com.littlekt.math.geom.degrees
import com.littlekt.util.seconds
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

sealed interface BodyMovement {
    fun updateParts(delta: Duration, body: Body)

    fun reset() {}
}

private const val BODY_PARALYZED_FACTOR = 0.05f

open class SwayBodyMovement(val maxAngle: Angle, val frequency: Float, val smoothFactor: Float) : BodyMovement {
    private val randomOffset = Game.random.nextFloat() * 2.0f * PI_F
    private var time = 0.0.seconds

    override fun updateParts(delta: Duration, body: Body) {
        time += delta

        val targetAngle = maxAngle * sin(time.seconds * frequency + randomOffset)

        body.parts[0].partRotation = lerpAngle(body.parts[0].partRotation, targetAngle, if (body.isParalyzed) smoothFactor * BODY_PARALYZED_FACTOR else smoothFactor)

        for (i in 1 until body.parts.size) {
            body.parts[i].partRotation = lerpAngle(body.parts[i].partRotation, body.parts[i - 1].partRotation, if (body.isParalyzed) smoothFactor * BODY_PARALYZED_FACTOR else smoothFactor)
        }
    }

    override fun reset() {
        time = 0.0.seconds
    }
}

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

open class CurlBodyMovement(val maxAngle: Angle, val smoothFactor: Float) : BodyMovement {
    override fun updateParts(delta: Duration, body: Body) {
        body.parts.forEach {
            it.partRotation = lerpAngle(it.partRotation, maxAngle, if (body.boss.isParalyzed) smoothFactor * BODY_PARALYZED_FACTOR else smoothFactor)
        }
    }
}

open class StretchBodyMovement(smoothFactor: Float) : CurlBodyMovement(0.0.degrees, smoothFactor)

open class HitBodyMovement(val target: BossTarget?, val smoothFactor: Float) : BodyMovement {
    private var targetAngle = 0.0.degrees
    private var isInitialized = false

    override fun updateParts(delta: Duration, body: Body) {
        if (!isInitialized) {
            targetAngle = atan2((target?.y ?: 0.0f) - body.y, (target?.x ?: 0.0f) - body.x).degrees
            isInitialized = false
        }

        val partAngle = targetAngle / Constants.BOSS2_BODY_PARTS

        body.parts.forEach {
            it.partRotation = lerpAngle(it.partRotation, partAngle, smoothFactor)
        }
    }
}

open class CompoundBodyMovement(movements: List<BodyMovement> = emptyList()) : BodyMovement {
    private val movements = movements.toMutableList()

    fun addMovement(movement: BodyMovement) {
        movements.add(movement)
    }

    fun removeMovement(movement: BodyMovement) {
        movements.remove(movement)
    }

    override fun updateParts(delta: Duration, body: Body) {
        movements.forEach { it.updateParts(delta, body) }
    }

    override fun reset() {
        movements.forEach { it.reset() }
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
