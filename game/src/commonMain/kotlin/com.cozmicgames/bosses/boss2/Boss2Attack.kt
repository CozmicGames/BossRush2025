package com.cozmicgames.bosses.boss2

import com.cozmicgames.Game
import com.cozmicgames.bosses.*
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

class HitAttack(target: BossTarget? = Game.world.decideOnTarget()) : Boss2Attack() {
    override val duration = 2.0.seconds

    override var bossMovement: BossMovement? = null
    override val bodyMovement = HitBodyMovement(target, 0.3f)
    override val shieldMovement = IdleShieldMovement()

    init {
        if (target == null)
            setDone()
        else
            bossMovement = AimBoss2BossMovement(target.x, target.y, false)
    }
}

class SpinAttack(target: BossTarget? = Game.world.decideOnTarget()) : Boss2Attack() {
    override val duration = 3.0.seconds

    override var bossMovement: BossMovement? = null

    override val bodyMovement = SequenceBodyMovement(
        duration / 2,
        listOf(
            CurlBodyMovement(3.0.degrees, 0.4f),
            CurlBodyMovement((-2.0).degrees, 0.4f)
        )
    )

    override val shieldMovement = IdleShieldMovement()

    init {
        if (target == null)
            setDone()
        else
            bossMovement = SequenceBossMovement(
                duration / 2,
                listOf(
                    AimBoss2BossMovement(target.x, target.y, false),
                    SpinBossMovement(90.0f)
                )
            )
    }
}

class FlyAttack(target: BossTarget? = Game.world.decideOnTarget()) : Boss2Attack() {
    override val duration = 4.0.seconds

    override var bossMovement: BossMovement? = null
    override val bodyMovement = WaveBodyMovement(4.0.degrees, 1.0f, 0.5f)
    override val shieldMovement = IdleShieldMovement()

    init {
        if (target == null)
            setDone()
        else
            bossMovement = FlyAttackBoss2BossMovement(target.x, target.y)
    }
}

class PierceAttack(target: BossTarget? = Game.world.decideOnTarget()) : Boss2Attack() {
    override val duration = 4.0.seconds

    override var bossMovement: BossMovement? = null
    override val bodyMovement = WaveBodyMovement(4.0.degrees, 1.0f, 0.5f)
    override val shieldMovement = IdleShieldMovement()

    init {
        if (target == null)
            setDone()
        else
            bossMovement = PierceAttackBoss2BossMovement(target.x, target.y)
    }
}

class ShootAttack(target: BossTarget? = Game.world.decideOnTarget()) : Boss2Attack() {
    override val duration = 3.0.seconds

    override var bossMovement: BossMovement? = null

    override val bodyMovement = CurlBodyMovement((-1.0).degrees, 0.4f)

    override val shieldMovement = ShootShieldMovement()

    init {
        if (target == null)
            setDone()
        else
            bossMovement = AimBoss2BossMovement(target.x, target.y, false)
    }
}

class BeamAttack(target: BossTarget? = Game.world.decideOnTarget()) : Boss2Attack() {
    override val duration = 3.0.seconds

    override var bossMovement: BossMovement? = null

    override val bodyMovement = CurlBodyMovement((-1.0).degrees, 0.4f)

    override val shieldMovement = BeamShieldMovement()

    init {
        if (target == null)
            setDone()
        else {
            bossMovement = AimBoss2BossMovement(target.x, target.y, false)
        }
    }
}

class SpinShootAttack : Boss2Attack() {
    override val duration = 5.0.seconds

    override val bossMovement = SpinBossMovement(90.0f)

    override val bodyMovement = CurlBodyMovement((-2.0).degrees, 0.4f)

    override val shieldMovement = ShootShieldMovement()
}

class SpinBeamAttack : Boss2Attack() {
    override val duration = 4.0.seconds

    override val bossMovement = SpinBossMovement(90.0f)

    override val bodyMovement = CurlBodyMovement((-2.0).degrees, 0.4f)

    override val shieldMovement = BeamShieldMovement(duration)
}


