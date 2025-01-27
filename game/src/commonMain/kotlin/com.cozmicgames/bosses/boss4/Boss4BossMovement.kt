package com.cozmicgames.bosses.boss4

import com.cozmicgames.Game
import com.cozmicgames.bosses.*
import com.cozmicgames.utils.lerp
import com.littlekt.math.PI_F
import com.littlekt.math.geom.cosine
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.radians
import com.littlekt.math.geom.sine
import com.littlekt.util.seconds
import kotlin.math.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class IdleBoss4BossMovement : BossMovement {
    private var timer = 0.0.seconds

    private var isFirstUpdate = true
    private var centerX = 0.0f
    private var centerY = 0.0f

    override fun update(delta: Duration, boss: Boss, transform: BossTransform) {
        if (isFirstUpdate) {
            centerX = boss.x
            centerY = boss.y
            isFirstUpdate = false
        }

        timer += delta
        transform.targetX = centerX + 600.0f * cos(timer.seconds)
        transform.targetY = centerY + 600.0f * sin(timer.seconds)
        transform.targetRotation = timer.seconds.radians - 30.0.degrees * (sin(timer.seconds * 3.0f) * 0.5f + 0.5f)
    }
}

class EnterVortexBoss4BossMovement(private val onVanished: () -> Unit) : BossMovement {
    private var timer = 0.0.seconds
    private var movementTimer = 0.0.seconds

    private var isFirstUpdate = true
    private var isVortexOpen = false
    private var wasVortexOpen = false
    private var centerX = 0.0f
    private var centerY = 0.0f

    private var distanceFromCenter = 300.0f
    private var startingAngle = 0.0f

    private var calledOnVanished = false

    override fun update(delta: Duration, boss: Boss, transform: BossTransform) {
        boss as? Boss4 ?: throw IllegalArgumentException("This movement is only for Boss4")

        if (isFirstUpdate) {
            centerX = boss.x
            centerY = boss.y
            startingAngle = boss.rotation.radians

            boss.openVortex(centerX, centerY, 500.0f, 2.0.seconds) {
                isVortexOpen = true
            }

            isFirstUpdate = false
        }

        if (isVortexOpen) {
            val factor = (timer / 3.0.seconds).toFloat()

            if (distanceFromCenter >= 0.0f)
                distanceFromCenter = lerp(300.0f, 50.0f, factor * factor)

            if (boss.bossScale > 0.0f)
                boss.bossScale = lerp(1.0f, 0.0f, factor * factor)

            if (timer > 2.5.seconds && !wasVortexOpen) {
                boss.closeVortex(20.0.seconds)
                wasVortexOpen = true
            }

            timer += delta
        }

        movementTimer += delta
        transform.targetX = centerX + distanceFromCenter * cos(movementTimer.seconds * 3.0f + PI_F * 0.4f)
        transform.targetY = centerY + distanceFromCenter * sin(movementTimer.seconds * 3.0f + PI_F * 0.4f)
        transform.targetRotation = timer.seconds.radians - 15.0.degrees * (sin(movementTimer.seconds * 3.0f + PI_F * 0.7f + startingAngle) * 0.5f + 0.5f)

        if (boss.bossScale <= 0.0f && !calledOnVanished) {
            onVanished()
            calledOnVanished = true
        }
    }
}

class ExitVortexBoss4BossMovement(private val x: Float, private val y: Float, private val onAppeared: () -> Unit) : BossMovement {
    private var timer = 0.0.seconds

    private var isFirstUpdate = true
    private var isVortexOpen = false
    private var wasVortexOpen = false

    private var distanceFromCenter = 50.0f
    private var startingAngle = 0.0f

    private var calledOnAppeared = false

    override fun update(delta: Duration, boss: Boss, transform: BossTransform) {
        boss as? Boss4 ?: throw IllegalArgumentException("This movement is only for Boss4")

        if (isFirstUpdate) {
            boss.x = x
            boss.y = y
            startingAngle = boss.rotation.radians

            boss.openVortex(x, y, 500.0f, 2.0.seconds) {
                isVortexOpen = true
            }

            isFirstUpdate = false
        }

        val factor = (timer / 3.0.seconds).toFloat()

        if (distanceFromCenter < 300.0f)
            distanceFromCenter = lerp(50.0f, 300.0f, factor * factor)

        if (boss.bossScale < 1.0f)
            boss.bossScale = lerp(0.0f, 1.0f, factor * factor)

        if (isVortexOpen && !wasVortexOpen && timer >= 3.5.seconds) {
            boss.closeVortex(2.0.seconds)
            wasVortexOpen = true
        }

        timer += delta
        transform.targetX = x + distanceFromCenter * cos(timer.seconds * 3.0f + PI_F * 0.4f)
        transform.targetY = y + distanceFromCenter * sin(timer.seconds * 3.0f + PI_F * 0.4f)
        transform.targetRotation = timer.seconds.radians - 15.0.degrees * (sin(timer.seconds * 3.0f + PI_F * 0.7f + startingAngle) * 0.5f + 0.5f)

        if (boss.bossScale >= 1.0f && !calledOnAppeared) {
            onAppeared()
            calledOnAppeared = true
        }
    }
}

class TeleportBoss4BossMovement(private val onDone: () -> Unit) : BossMovement {
    companion object {
        private fun chooseRandomPosition(boss: Boss): Pair<Float, Float> {
            var tries = 0

            var bestX = 0.0f
            var bestY = 0.0f
            var bestDistance = Float.MAX_VALUE

            while (tries++ < 20) {
                val x = Game.physics.minX + Game.random.nextFloat() * (Game.physics.maxX - Game.physics.minX)
                val y = Game.physics.minY + Game.random.nextFloat() * (Game.physics.maxY - Game.physics.minY)

                val dx = x - boss.x
                val dy = y - boss.y
                var minDistance = dx * dx + dy * dy

                Game.players.players.forEach {
                    val pdx = x - it.ship.x
                    val pdy = y - it.ship.y
                    val distance = pdx * pdx + pdy * pdy

                    if (distance < minDistance)
                        minDistance = distance
                }

                if (minDistance < bestDistance) {
                    bestX = x
                    bestY = y
                    bestDistance = minDistance
                }
            }

            return Pair(bestX, bestY)
        }
    }

    private lateinit var enterVortexMovement: EnterVortexBoss4BossMovement
    private lateinit var exitVortexMovement: ExitVortexBoss4BossMovement

    private var isFirstUpdate = true
    private var secondStage = false
    private var timer = 0.0.seconds

    override fun update(delta: Duration, boss: Boss, transform: BossTransform) {
        boss as? Boss4 ?: throw IllegalArgumentException("This movement is only for Boss4")

        if (isFirstUpdate) {
            boss.isTeleporting = true

            enterVortexMovement = EnterVortexBoss4BossMovement {
                boss.vortex.size = 0.0f
                secondStage = true
            }

            val (x, y) = chooseRandomPosition(boss)
            exitVortexMovement = ExitVortexBoss4BossMovement(x, y) {
                onDone()
                boss.isTeleporting = false
            }

            isFirstUpdate = false
        }

        if (secondStage) {
            timer += delta

            if (timer > 0.5.seconds) //TODO: Add trail effect?
                exitVortexMovement.update(delta, boss, transform)
        } else
            enterVortexMovement.update(delta, boss, transform)
    }
}

class PierceAttackBoss4BossMovement(private val target: BossTarget, val duration: Duration) : BossMovement {
    private val targetingBoss4BossMovement = TargetingBoss4BossMovement(target, true)

    private var timer = 0.0.seconds
    private val targetDistance = 300.0f

    override fun update(delta: Duration, boss: Boss, transform: BossTransform) {
        val dx = target.x - boss.x
        val dy = target.y - boss.y
        val distance = sqrt(dx * dx + dy * dy)

        if (distance < targetDistance) {
            transform.targetX = boss.x
            transform.targetY = boss.y
        } else {
            transform.targetX = lerp(boss.x, boss.x + dx / distance * targetDistance, 0.8f)
            transform.targetY = lerp(boss.y, boss.y + dy / distance * targetDistance, 0.8f)
        }

        timer += delta

        if (timer < duration * 0.33)
            targetingBoss4BossMovement.update(delta, boss, transform)
        else {
            val direction = if (target.x > boss.x) 1.0f else -1.0f
            transform.targetRotation += 40.0.degrees * delta.seconds * direction
        }
    }
}

class AimBoss4BossMovement(private val targetX: Float, private val targetY: Float) : BossMovement {
    override fun update(delta: Duration, boss: Boss, transform: BossTransform) {
        val dx = targetX - boss.x
        val dy = targetY - boss.y
        transform.targetRotation = atan2(dy, dx).radians - 90.0.degrees
    }
}

class TargetingBoss4BossMovement(private val target: BossTarget, private val side: Boolean) : BossMovement {
    override fun update(delta: Duration, boss: Boss, transform: BossTransform) {
        val dx = target.x - boss.x
        val dy = target.y - boss.y

        if (side) {
            if (dy > 0) {
                if (dx > 0)
                    transform.targetRotation = atan2(dy, dx).radians
                else
                    transform.targetRotation = atan2(dy, dx).radians + 180.0.degrees
            } else {
                if (dx > 0)
                    transform.targetRotation = atan2(dy, dx).radians - 180.0.degrees
                else
                    transform.targetRotation = atan2(dy, dx).radians
            }
        } else
            transform.targetRotation = atan2(dy, dx).radians - 90.0.degrees
    }
}

class HuntingBoss4BossMovement(val target: BossTarget) : BossMovement {
    private var timer = 0.0.seconds

    override fun update(delta: Duration, boss: Boss, transform: BossTransform) {
        timer += delta

        val angle = (timer.seconds * 100.0f).degrees

        transform.targetX = target.x + 300.0f * angle.cosine
        transform.targetY = target.y + 300.0f * angle.sine
        transform.targetRotation = atan2(boss.y - target.y, boss.x - target.x).radians + 90.0.degrees
    }
}

class FlyAttackBoss4BossMovement(private val target: BossTarget, private val aimTime: Duration = 3.0.seconds, private val onReached: () -> Unit = {}) : BossMovement {
    private val aimMovement = TargetingBoss4BossMovement(target, false)
    private lateinit var destinationMovement: DestinationBossMovement

    private var speedModifier = 2.0f
    private var timer = 0.0.seconds
    private var isFirstUpdate = true

    override fun update(delta: Duration, boss: Boss, transform: BossTransform) {
        if (isFirstUpdate) {
            val dx = target.x - boss.x
            val dy = target.y - boss.y

            val targetX = target.x + dx * 1.1f
            val targetY = target.y + dy * 1.1f

            destinationMovement = DestinationBossMovement(targetX, targetY) {
                speedModifier = 1.0f
                onReached()
            }

            isFirstUpdate = false
        }

        timer += delta

        transform.moveSpeedModifier = speedModifier

        if (timer < aimTime)
            aimMovement.update(delta, boss, transform)
        else
            destinationMovement.update(delta, boss, transform)
    }
}

class TeleportAndFlyAttackBoss4BossMovement(target: BossTarget, private val onReached: () -> Unit = {}) : BossMovement {
    private val flyMovement = FlyAttackBoss4BossMovement(target, 5.0.seconds) {
        teleportMovement = TeleportBoss4BossMovement(onReached)
    }

    private lateinit var teleportMovement: TeleportBoss4BossMovement

    private var secondStage = false
    private var isFirstUpdate = true

    override fun update(delta: Duration, boss: Boss, transform: BossTransform) {
        if (isFirstUpdate) {
            teleportMovement = TeleportBoss4BossMovement {
                secondStage = true
            }

            isFirstUpdate = false
        }

        if (secondStage)
            flyMovement.update(delta, boss, transform)
        else
            teleportMovement.update(delta, boss, transform)
    }
}

class FollowPlayerBoss4BossMovement(private val target: BossTarget, private val onReached: () -> Unit = {}) : BossMovement {
    companion object {
        private const val TARGET_DISTANCE = 300.0f
    }

    private var timer = 0.0.seconds

    override fun update(delta: Duration, boss: Boss, transform: BossTransform) {
        val dx = target.x - boss.x
        val dy = target.y - boss.y
        val distance = sqrt(dx * dx + dy * dy)

        if (distance < TARGET_DISTANCE) {
            transform.targetX = boss.x
            transform.targetY = boss.y
            onReached()
        } else {
            transform.targetX = lerp(boss.x, boss.x + dx / distance * TARGET_DISTANCE, 0.8f)
            transform.targetY = lerp(boss.y, boss.y + dy / distance * TARGET_DISTANCE, 0.8f)
        }

        timer += delta
        transform.targetRotation = atan2(dy, dx).radians - 90.0.degrees
    }
}