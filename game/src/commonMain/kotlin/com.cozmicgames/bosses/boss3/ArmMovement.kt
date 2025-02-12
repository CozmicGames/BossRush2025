package com.cozmicgames.bosses.boss3

import com.cozmicgames.Game
import com.cozmicgames.physics.Grabbable
import com.cozmicgames.utils.lerpAngle
import com.cozmicgames.weapons.ProjectileType
import com.littlekt.graphics.Color
import com.littlekt.math.PI_F
import com.littlekt.math.geom.*
import com.littlekt.util.seconds
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sin
import kotlin.math.sqrt
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
            if (shotCount > 0 && !arm.isParalyzed)
                for (i in 0 until shotCount) {
                    val angle = (if (arm.flip) arm.claw.muzzleRotation - spread * 0.5f else arm.claw.muzzleRotation - spread * 0.5f) + spread / (shotCount - 1) * i
                    Game.projectiles.spawnProjectile(arm.claw, ProjectileType.ENERGY_BALL, arm.claw.muzzleX, arm.claw.muzzleY, angle, 500.0f, 0.0f)
                    Game.audio.shootSound.play(0.7f)
                }

            timer = 0.0.seconds
        }
    }
}

class OpenClawMovement(val smoothFactor: Float) : ArmMovement {
    override fun updateParts(delta: Duration, arm: Arm) {
        arm.claw.clawAngle = lerpAngle(arm.claw.clawAngle, 0.0.degrees, smoothFactor)
    }
}

class SnapClawMovement(val frequency: Float, val smoothFactor: Float) : ArmMovement {
    private var timer = 0.0.seconds

    override fun updateParts(delta: Duration, arm: Arm) {
        timer += delta

        arm.claw.clawAngle = lerpAngle(arm.claw.clawAngle, 40.0.degrees * (1.0f - abs(sin(timer.seconds * frequency))), smoothFactor)
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

class GrabArmMovement(val target: Grabbable, val smoothFactor: Float) : ArmMovement {
    override fun updateParts(delta: Duration, arm: Arm) {
        val targetX = target.x
        val targetY = target.y

        val dx = arm.claw.grabX - targetX
        val dy = arm.claw.grabY - targetY
        val distance = sqrt(dx * dx + dy * dy)

        val otherDx = arm.otherArm.claw.grabX - targetX
        val otherDy = arm.otherArm.claw.grabY - targetY
        val otherDistance = sqrt(otherDx * otherDx + otherDy * otherDy)

        if (distance < otherDistance) {
            val targetAngle = if (arm.flip)
                -atan2(arm.claw.y - targetY, arm.claw.x - targetX).radians + 180.0.degrees
            else
                -atan2(arm.claw.y - targetY, arm.claw.x - targetX).radians

            arm.parts[0].armRotation = lerpAngle(arm.parts[0].armRotation, targetAngle, if (arm.isParalyzed) smoothFactor * ARM_PARALYZED_FACTOR else smoothFactor)

            for (i in 1 until arm.parts.size)
                arm.parts[i].armRotation = lerpAngle(arm.parts[i].armRotation, 0.0.degrees, if (arm.isParalyzed) smoothFactor * ARM_PARALYZED_FACTOR else smoothFactor)

            if (arm.claw.tryGrabObject())
                arm.boss.movementController.onGrabbed()
        }
    }
}

class StretchOutArmMovement(val smoothFactor: Float) : ArmMovement {
    val angles = arrayOf(
        15.0.degrees,
        (-15.0).degrees,
    )

    override fun updateParts(delta: Duration, arm: Arm) {
        arm.parts[0].armRotation = lerpAngle(arm.parts[0].armRotation, angles[arm.index], if (arm.isParalyzed) smoothFactor * ARM_PARALYZED_FACTOR else smoothFactor)

        for (i in (1 until arm.parts.size)) {
            arm.parts[i].armRotation = lerpAngle(arm.parts[i].armRotation, 0.0.degrees, if (arm.isParalyzed) smoothFactor * ARM_PARALYZED_FACTOR else smoothFactor)
        }
    }
}

class AimArmMovement(val targetX: Float, val targetY: Float, val smoothFactor: Float) : ArmMovement {
    override fun updateParts(delta: Duration, arm: Arm) {
        val dx = arm.claw.x - targetX
        val dy = arm.claw.y - targetY
        val distance = sqrt(dx * dx + dy * dy)

        val otherDx = arm.otherArm.claw.x - targetX
        val otherDy = arm.otherArm.claw.y - targetY
        val otherDistance = sqrt(otherDx * otherDx + otherDy * otherDy)

        if (distance < otherDistance) {
            val targetAngle = if (arm.flip)
                -atan2(arm.claw.y - targetY, arm.claw.x - targetX).radians + 180.0.degrees
            else
                -atan2(arm.claw.y - targetY, arm.claw.x - targetX).radians

            arm.parts[0].armRotation = lerpAngle(arm.parts[0].armRotation, targetAngle - arm.baseRotation, if (arm.isParalyzed) smoothFactor * ARM_PARALYZED_FACTOR else smoothFactor)

            for (i in 1 until arm.parts.size)
                arm.parts[i].armRotation = lerpAngle(arm.parts[i].armRotation, 0.0.degrees, if (arm.isParalyzed) smoothFactor * ARM_PARALYZED_FACTOR else smoothFactor)
        }
    }
}

class KeepArmMovement : ArmMovement {
    override fun updateParts(delta: Duration, arm: Arm) {

    }
}

class ThrowAttackClawMovement(val duration: Duration) : ArmMovement {
    private var timer = 0.0.seconds
    private var isReleased = hashSetOf<Arm>()

    override fun updateParts(delta: Duration, arm: Arm) {
        timer += delta

        if (timer >= duration * 0.8 && arm !in isReleased) {
            val impulseStrength = 4.0f
            val impulseAngle = arm.claw.rotation + if (arm.flip) (-90.0).degrees else 90.0.degrees
            val impulseX = impulseAngle.cosine * impulseStrength
            val impulseY = impulseAngle.sine * impulseStrength

            arm.claw.releaseGrabbedObject(impulseX, impulseY)
            isReleased += arm
        }
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
        IdleClawMovement(0.2f, 0.2f)
    )
)

class ParalyzedArmMovement : CompoundArmMovement(
    listOf(
        StretchOutArmMovement(0.2f),
        OpenClawMovement(0.5f)
    )
)

class DeadArmMovement : CompoundArmMovement(
    listOf(
        KeepArmMovement(),
        OpenClawMovement(0.2f)
    )
)
