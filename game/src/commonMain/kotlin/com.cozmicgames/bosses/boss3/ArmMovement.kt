package com.cozmicgames.bosses.boss3

import com.cozmicgames.Game
import com.cozmicgames.utils.lerpAngle
import com.cozmicgames.weapons.ProjectileType
import com.littlekt.math.PI_F
import com.littlekt.math.geom.Angle
import com.littlekt.math.geom.cosine
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.sine
import com.littlekt.util.seconds
import kotlin.math.sin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

sealed interface ArmMovement {
    fun updateParts(delta: Duration, arm: Arm)

    fun reset() {}
}

private const val ARM_PARALYZED_FACTOR = 0.05f

class IdleClawMovement(val frequency: Float, val smoothFactor: Float) : ArmMovement {
    private val randomOffset = Game.random.nextFloat() * 2.0f * PI_F
    private var time = 0.0.seconds

    override fun updateParts(delta: Duration, arm: Arm) {
        time += delta
        arm.claw.clawAngle = lerpAngle(arm.claw.clawAngle, 30.0.degrees * sin(time.seconds * frequency + randomOffset), smoothFactor)
    }
}

class ShootClawMovement(val frequency: Duration, val shotCount: Int = 1, val spread: Angle = 0.0.degrees) : ArmMovement {
    private var timer = 0.0.seconds

    override fun updateParts(delta: Duration, arm: Arm) {
        timer += delta

        arm.claw.clawAngle = lerpAngle(arm.claw.clawAngle, 30.0.degrees, 0.4f)

        if (timer >= frequency) {
            if (shotCount > 0)
                for (i in 0 until shotCount) {
                    val angle = (if (arm.flip) arm.claw.muzzleRotation - spread * 0.5f else arm.claw.muzzleRotation - spread * 0.5f) + spread / (shotCount - 1) * i
                    Game.projectiles.spawnProjectile(arm.claw, ProjectileType.ENERGY_BALL, arm.claw.muzzleX, arm.claw.muzzleY, angle, 500.0f, 0.0f)
                }

            timer = 0.0.seconds
        }
    }
}

class SwayArmMovement(val maxAngle: Angle, val frequency: Float, val smoothFactor: Float) : ArmMovement {
    private val randomOffset = Game.random.nextFloat() * 2.0f * PI_F
    private var time = 0.0.seconds

    override fun updateParts(delta: Duration, arm: Arm) {
        time += delta

        val targetAngle = maxAngle * sin(time.seconds * frequency + randomOffset)

        arm.parts[0].armRotation = lerpAngle(arm.parts[0].armRotation, targetAngle, if (arm.isParalyzed) smoothFactor * ARM_PARALYZED_FACTOR else smoothFactor)

        for (i in 1 until arm.parts.size) {
            arm.parts[i].armRotation = lerpAngle(arm.parts[i].armRotation, arm.parts[i - 1].armRotation, if (arm.isParalyzed) smoothFactor * ARM_PARALYZED_FACTOR else smoothFactor)
        }
    }

    override fun reset() {
        time = 0.0.seconds
    }
}

open class CompoundArmMovement(movements: List<ArmMovement> = emptyList()) : ArmMovement {
    private val movements = movements.toMutableList()

    fun addMovement(movement: ArmMovement) {
        movements.add(movement)
    }

    fun removeMovement(movement: ArmMovement) {
        movements.remove(movement)
    }

    override fun updateParts(delta: Duration, arm: Arm) {
        movements.forEach { it.updateParts(delta, arm) }
    }

    override fun reset() {
        movements.forEach { it.reset() }
    }
}

open class SequenceArmMovement(private val durationPerMovement: Duration, private val movements: List<ArmMovement>) : ArmMovement {
    private var timer = 0.0.seconds
    private var currentMovementIndex = 0

    override fun updateParts(delta: Duration, arm: Arm) {
        timer += delta

        if (timer >= durationPerMovement) {
            timer = 0.0.seconds
            currentMovementIndex = (currentMovementIndex + 1) % movements.size
        }

        movements[currentMovementIndex].updateParts(delta, arm)
    }

    override fun reset() {
        timer = 0.0.seconds
        movements.forEach { it.reset() }
    }
}

class IdleArmMovement : CompoundArmMovement(
    listOf(
        SwayArmMovement(15.0.degrees, 0.1f, 0.2f),
        //IdleClawMovement(0.2f, 0.2f)
        ShootClawMovement(0.5.seconds, 3, 30.0.degrees)
    )
)
