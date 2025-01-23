package com.cozmicgames.bosses.boss3

import com.cozmicgames.Game
import com.cozmicgames.bosses.*
import com.cozmicgames.entities.worldObjects.PlayerShip
import com.littlekt.math.geom.degrees
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

abstract class Boss3Attack : Attack() {
    abstract val bossMovement: BossMovement?
    abstract val legMovement: LegMovement?
    abstract val armMovement: ArmMovement?
    abstract val beakMovement: BeakMovement?

    override fun applyToMovement(movement: Movement) {
        movement as? Boss3Movement ?: throw IllegalArgumentException("Movement must be a Boss1Movement")
        bossMovement?.let { movement.bossMovement = it }
        legMovement?.let { movement.legMovement = it }
        armMovement?.let { movement.armMovement = it }
        beakMovement?.let { movement.beakMovement = it }
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

class GrabAttack(ship: PlayerShip = Game.players.players.random().ship) : Boss3Attack() {
    override val duration: Duration = 7.0.seconds

    override val bossMovement = GrabAttackBoss3BossMovement(ship)
    override val legMovement = HangLegMovement()
    override val armMovement = GrabArmMovement(ship, 0.2f)
    override val beakMovement = ClosedBeakMovement()
}

class ThrowAttack : Boss3Attack() {
    override val duration: Duration = 2.0.seconds

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

class ShootAttack0(ship: PlayerShip = Game.players.players.random().ship) : Boss3Attack() {
    override val duration = 5.0.seconds

    override val bossMovement = FollowPlayerBoss3BossMovement(ship)
    override val legMovement = CompoundLegMovement(
        listOf(
            DefendLegMovement(),
            SwayLegMovement(10.0.degrees, 0.3f, 0.2f)
        )
    )
    override val armMovement = CompoundArmMovement(listOf(
        AimArmMovement(ship.x, ship.y, 0.1f),
        ShootClawMovement(0.3.seconds, 1, 0.0.degrees)
    ))
    override val beakMovement = ClosedBeakMovement()
}

class ShootAttack1(ship: PlayerShip = Game.players.players.random().ship) : Boss3Attack() {
    override val duration = 5.0.seconds

    override val bossMovement = FollowPlayerBoss3BossMovement(ship)
    override val legMovement = CompoundLegMovement(
        listOf(
            DefendLegMovement(),
            SwayLegMovement(7.0.degrees, 0.4f, 0.2f)
        )
    )
    override val armMovement = CompoundArmMovement(listOf(
        AimArmMovement(ship.x, ship.y, 0.1f),
        ShootClawMovement(0.4.seconds, 3, 30.0.degrees)
    ))
    override val beakMovement = ClosedBeakMovement()
}

class ShootAttack2(ship: PlayerShip = Game.players.players.random().ship) : Boss3Attack() {
    override val duration = 5.0.seconds

    override val bossMovement = FollowPlayerBoss3BossMovement(ship)
    override val legMovement = CompoundLegMovement(
        listOf(
            DefendLegMovement(),
            SwayLegMovement(12.0.degrees, 0.5f, 0.2f)
        )
    )
    override val armMovement = CompoundArmMovement(listOf(
        AimArmMovement(ship.x, ship.y, 0.1f),
        ShootClawMovement(0.2.seconds, 5, 40.0.degrees)
    ))
    override val beakMovement = ClosedBeakMovement()
}

class SpinShootAttack0(override val duration: Duration = 4.0.seconds) : Boss3Attack() {
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

class SpinShootAttack1(override val duration: Duration = 4.0.seconds) : Boss3Attack() {
    override val bossMovement = SpinAttackBoss3BossMovement()
    override val legMovement = StretchOutLegMovement(0.1f)
    override val armMovement = CompoundArmMovement(
        listOf(
            StretchOutArmMovement(0.1f),
            ShootClawMovement(0.4.seconds, 3, 30.0.degrees)
        )
    )
    override val beakMovement = ClosedBeakMovement()
}

class SpinShootAttack2(override val duration: Duration = 4.0.seconds) : Boss3Attack() {
    override val bossMovement = SpinAttackBoss3BossMovement()
    override val legMovement = StretchOutLegMovement(0.1f)
    override val armMovement = CompoundArmMovement(
        listOf(
            StretchOutArmMovement(0.1f),
            ShootClawMovement(0.2.seconds, 5, 40.0.degrees)
        )
    )
    override val beakMovement = ClosedBeakMovement()
}
