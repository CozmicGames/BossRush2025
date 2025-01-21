package com.cozmicgames.bosses.boss2

import com.cozmicgames.Game
import com.cozmicgames.bosses.*
import com.cozmicgames.entities.worldObjects.PlayerShip
import com.littlekt.math.geom.degrees
import kotlin.time.Duration.Companion.seconds

abstract class Boss2Attack : Attack() {
    abstract val bossMovement: BossMovement?
    abstract val bodyMovement: BodyMovement?
    abstract val shieldMovement: ShieldMovement?

    override fun applyToMovement(movement: Movement) {
        movement as? Boss2Movement ?: throw IllegalArgumentException("Movement must be a Boss1Movement")
        bossMovement?.let { movement.bossMovement = it }
        bodyMovement?.let { movement.bodyMovement = it }
        shieldMovement?.let { movement.shieldMovement = it }
    }
}

class HitPlayerAttack(ship: PlayerShip = Game.players.players.random().ship) : Boss2Attack() {
    override val duration = 2.0.seconds

    override val bossMovement = AimBoss2BossMovement(ship.x, ship.y, false)
    override val bodyMovement = HitBodyMovement(ship, 0.3f)
    override val shieldMovement: ShieldMovement = IdleShieldMovement()
}

class SpinAttack(ship: PlayerShip = Game.players.players.random().ship) : Boss2Attack() {
    override val duration = 3.0.seconds

    override val bossMovement: BossMovement = SequenceBossMovement(
        duration / 2,
        listOf(
            AimBoss2BossMovement(ship.x, ship.y, false),
            SpinBossMovement(90.0f)
        )
    )

    override val bodyMovement: BodyMovement = SequenceBodyMovement(
        duration / 2,
        listOf(
            CurlBodyMovement(3.0.degrees, 0.4f),
            CurlBodyMovement((-2.0).degrees, 0.4f)
        )
    )

    override val shieldMovement: ShieldMovement = IdleShieldMovement()
}

class FlyAttack(ship: PlayerShip = Game.players.players.random().ship) : Boss2Attack() {
    override val duration = 4.0.seconds

    override val bossMovement: BossMovement = FlyAttackBoss2BossMovement(ship.x, ship.y)
    override val bodyMovement: BodyMovement = WaveBodyMovement(4.0.degrees, 1.0f, 0.5f)
    override val shieldMovement: ShieldMovement = IdleShieldMovement()
}

class PierceAttack(ship: PlayerShip = Game.players.players.random().ship) : Boss2Attack() {
    override val duration = 4.0.seconds

    override val bossMovement: BossMovement = PierceAttackBoss2BossMovement(ship.x, ship.y)
    override val bodyMovement: BodyMovement = WaveBodyMovement(4.0.degrees, 1.0f, 0.5f)
    override val shieldMovement: ShieldMovement = IdleShieldMovement()
}

class ShootAttack(ship: PlayerShip = Game.players.players.random().ship) : Boss2Attack() {
    override val duration = 3.0.seconds

    override val bossMovement: BossMovement = AimBoss2BossMovement(ship.x, ship.y, false)

    override val bodyMovement: BodyMovement = CurlBodyMovement((-1.0).degrees, 0.4f)

    override val shieldMovement: ShieldMovement = ShootShieldMovement()
}

class BeamAttack(ship: PlayerShip = Game.players.players.random().ship) : Boss2Attack() {
    override val duration = 3.0.seconds

    override val bossMovement: BossMovement = AimBoss2BossMovement(ship.x, ship.y, false)

    override val bodyMovement: BodyMovement = CurlBodyMovement((-1.0).degrees, 0.4f)

    override val shieldMovement: ShieldMovement = BeamShieldMovement()
}

class SpinShootAttack() : Boss2Attack() {
    override val duration = 5.0.seconds

    override val bossMovement: BossMovement = SpinBossMovement(90.0f)

    override val bodyMovement: BodyMovement = CurlBodyMovement((-2.0).degrees, 0.4f)

    override val shieldMovement: ShieldMovement = ShootShieldMovement()
}

class SpinBeamAttack() : Boss2Attack() {
    override val duration = 4.0.seconds

    override val bossMovement: BossMovement = SpinBossMovement(90.0f)

    override val bodyMovement: BodyMovement = CurlBodyMovement((-2.0).degrees, 0.4f)

    override val shieldMovement: ShieldMovement = BeamShieldMovement(duration)
}


