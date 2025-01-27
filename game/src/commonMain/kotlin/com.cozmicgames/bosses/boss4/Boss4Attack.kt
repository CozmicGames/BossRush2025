package com.cozmicgames.bosses.boss4

import com.cozmicgames.Game
import com.cozmicgames.bosses.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

abstract class Boss4Attack : Attack() {
    abstract val bossMovement: BossMovement?
    abstract val tailMovement: TailMovement?
    abstract val beakMovement: BeakMovement?

    override fun applyToMovement(movement: Movement) {
        movement as? Boss4Movement ?: throw IllegalArgumentException("Movement must be a Boss4Movement")
        bossMovement?.let { movement.bossMovement = it }
        tailMovement?.let { movement.tailMovement = it }
        beakMovement?.let { movement.beakMovement = it }
    }
}

class CamouflageAttack : Boss4Attack() {
    override val duration = 0.1.seconds

    override val bossMovement = null
    override val tailMovement = null
    override val beakMovement = null

    override fun onStart(boss: Boss) {
        boss as? Boss4 ?: throw IllegalArgumentException("Boss must be a Boss4")

        boss.camouflage()
    }
}

class HuntingAttack(private val stealth: Boolean, override val duration: Duration, target: BossTarget? = Game.world.decideOnTarget()) : Boss4Attack() {
    override var bossMovement: BossMovement? = null
    override val tailMovement = StretchTailMovement(0.1f)
    override val beakMovement = ClosedBeakMovement()

    init {
        if (target == null)
            setDone()
        else
            bossMovement = HuntingBoss4BossMovement(target)
    }

    override fun onStart(boss: Boss) {
        boss as? Boss4 ?: throw IllegalArgumentException("Boss must be a Boss4")

        if (stealth)
            boss.camouflage()
    }
}

class PierceAttack(target: BossTarget? = Game.world.decideOnTarget()) : Boss4Attack() {
    override val duration = 4.0.seconds

    override var bossMovement: BossMovement? = null
    override var tailMovement: TailMovement? = null
    override val beakMovement = ScreamBeakMovement()

    init {
        if (target == null)
            setDone()
        else {
            bossMovement = PierceAttackBoss4BossMovement(target, duration)
            tailMovement = PierceTailMovement(target, 0.3f)
        }
    }
}

class FlyAttack(private val stealth: Boolean, target: BossTarget? = Game.world.decideOnTarget()) : Boss4Attack() {
    override val duration = 5.0.seconds

    override var bossMovement: BossMovement? = null
    override val tailMovement = StretchTailMovement(0.1f)
    override val beakMovement = ClosedBeakMovement()

    init {
        if (target == null)
            setDone()
        else
            bossMovement = FlyAttackBoss4BossMovement(target)
    }

    override fun onStart(boss: Boss) {
        boss as? Boss4 ?: throw IllegalArgumentException("Boss must be a Boss4")

        if (stealth) {
            boss.camouflage()
        }
    }
}

class TeleportAttack : Boss4Attack() {
    override val bossMovement = TeleportBoss4BossMovement {
        setDone()
    }
    override val tailMovement = StretchTailMovement(0.1f)
    override val beakMovement = ClosedBeakMovement()
}

class TeleportAndFlyAttack(target: BossTarget? = Game.world.decideOnTarget()) : Boss4Attack() {
    override var bossMovement: BossMovement? = null
    override val tailMovement = StretchTailMovement(0.1f)
    override val beakMovement = ClosedBeakMovement()

    init {
        if (target == null)
            setDone()
        else
            bossMovement = TeleportAndFlyAttackBoss4BossMovement(target) {
                setDone()
            }
    }
}

