package com.cozmicgames.bosses.boss3

import com.cozmicgames.Game
import com.cozmicgames.bosses.*
import com.cozmicgames.entities.worldObjects.PlayerShip
import com.cozmicgames.weapons.AreaEffectGrowthType
import com.cozmicgames.weapons.AreaEffectSourceType
import com.cozmicgames.weapons.AreaEffectType
import com.littlekt.math.geom.degrees
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

abstract class Boss3Attack : Attack() {
    abstract val bossMovement: BossMovement?
    abstract val legMovement: LegMovement?
    abstract val armMovement: ArmMovement?
    abstract val beakMovement: BeakMovement?

    override fun applyToMovement(movement: Movement) {
        movement as? Boss3Movement ?: throw IllegalArgumentException("Movement must be a Boss3Movement")
        bossMovement?.let { movement.bossMovement = it }
        legMovement?.let { movement.legMovement = it }
        armMovement?.let { movement.armMovement = it }
        beakMovement?.let { movement.beakMovement = it }
    }
}

class GravityScreamAttack : Boss3Attack() {
    override val duration = 4.0.seconds

    override val bossMovement = WiggleBoss3BossMovement()
    override val legMovement = StretchOutLegMovement(0.1f)
    override val armMovement = StretchOutArmMovement(0.1f)
    override val beakMovement = ScreamBeakMovement()

    override fun onStart(boss: Boss) {
        Game.areaEffects.spawnEffect(boss as Boss3, AreaEffectType.GRAVITY_WAVE, AreaEffectSourceType.MOVING, AreaEffectGrowthType.INVERSE_LINEAR, 600.0f, 70.0f, duration)
    }
}

class StretchAttack(override val duration: Duration = 1.0.seconds) : Boss3Attack() {
    override val bossMovement = WiggleBoss3BossMovement()
    override val legMovement = StretchOutLegMovement(0.1f)
    override val armMovement = StretchOutArmMovement(0.1f)
    override val beakMovement = ClosedBeakMovement()
}

class SpinAttack(override val duration: Duration = 2.0.seconds) : Boss3Attack() {
    override val bossMovement = SpinAttackBoss3BossMovement()
    override val legMovement = StretchOutLegMovement(0.1f)
    override val armMovement = StretchOutArmMovement(0.1f)
    override val beakMovement = ClosedBeakMovement()
}

class GrabAttack(target: BossTarget? = Game.world.decideOnTarget()) : Boss3Attack() {
    override val duration: Duration = 7.0.seconds

    override var bossMovement: BossMovement? = null
    override val legMovement = HangLegMovement()
    override var armMovement: ArmMovement? = null
    override val beakMovement = ClosedBeakMovement()

    init {
        if (target !is PlayerShip)
            setDone()
        else {
            bossMovement = GrabAttackBoss3BossMovement(target)
            armMovement = GrabArmMovement(target, 0.2f)
        }
    }
}

class ThrowAttack : Boss3Attack() {
    override val duration: Duration = 4.0.seconds

    override val bossMovement = SpinBossMovement(90.0f)
    override val legMovement = StretchOutLegMovement(0.05f)
    override val armMovement = CompoundArmMovement(
        listOf(
            KeepArmMovement(),
            ThrowAttackClawMovement(duration)
        )
    )
    override val beakMovement = ClosedBeakMovement()
}

class ShootAttack0(target: BossTarget? = Game.world.decideOnTarget()) : Boss3Attack() {
    override val duration = 5.0.seconds

    override var bossMovement: BossMovement? = null
    override var legMovement: LegMovement? = null
    override var armMovement: ArmMovement? = null
    override val beakMovement = ClosedBeakMovement()

    init {
        if (target !is PlayerShip)
            setDone()
        else {
            bossMovement = FollowPlayerBoss3BossMovement(target)
            legMovement = CompoundLegMovement(
                listOf(
                    DefendLegMovement(),
                    SwayLegMovement(10.0.degrees, 0.3f, 0.2f)
                )
            )
            armMovement = CompoundArmMovement(
                listOf(
                    AimArmMovement(target.x, target.y, 0.1f),
                    ShootClawMovement(0.3.seconds, 1, 0.0.degrees)
                )
            )
        }
    }
}

class ShootAttack1(target: BossTarget? = Game.world.decideOnTarget()) : Boss3Attack() {
    override val duration = 5.0.seconds

    override var bossMovement: BossMovement? = null
    override var legMovement: LegMovement? = null
    override var armMovement: ArmMovement? = null
    override val beakMovement = ClosedBeakMovement()

    init {
        if (target !is PlayerShip)
            setDone()
        else {
            bossMovement = FollowPlayerBoss3BossMovement(target)
            legMovement = CompoundLegMovement(
                listOf(
                    DefendLegMovement(),
                    SwayLegMovement(7.0.degrees, 0.4f, 0.2f)
                )
            )
            armMovement = CompoundArmMovement(
                listOf(
                    AimArmMovement(target.x, target.y, 0.1f),
                    ShootClawMovement(0.4.seconds, 3, 30.0.degrees)
                )
            )
        }
    }
}

class SpinShootAttack(override val duration: Duration = 4.0.seconds) : Boss3Attack() {
    override val bossMovement = SpinAttackBoss3BossMovement()
    override val legMovement = StretchOutLegMovement(0.1f)
    override val armMovement = CompoundArmMovement(
        listOf(
            StretchOutArmMovement(0.1f),
            ShootClawMovement(0.3.seconds, 1, 0.0.degrees)
        )
    )
    override val beakMovement = ClosedBeakMovement()
}

