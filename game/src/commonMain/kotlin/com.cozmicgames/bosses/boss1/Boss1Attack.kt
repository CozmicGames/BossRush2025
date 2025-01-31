package com.cozmicgames.bosses.boss1

import com.cozmicgames.Game
import com.cozmicgames.bosses.*
import com.cozmicgames.weapons.AreaEffectGrowthType
import com.cozmicgames.weapons.AreaEffectSourceType
import com.cozmicgames.weapons.AreaEffectType
import com.littlekt.math.geom.degrees
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

abstract class Boss1Attack : Attack() {
    abstract val bossMovement: BossMovement?
    abstract val tentacleMovement: TentacleMovement?
    abstract val beakMovement: BeakMovement?

    override fun applyToMovement(movement: Movement) {
        movement as? Boss1Movement ?: throw IllegalArgumentException("Movement must be a Boss1Movement")
        bossMovement?.let { movement.bossMovement = it }
        tentacleMovement?.let { movement.tentacleMovement = it }
        beakMovement?.let { movement.beakMovement = it }
    }
}

class StretchAttack(override val duration: Duration = 4.0.seconds) : Boss1Attack() {
    override val tentacleMovement = CompoundTentacleMovement()
    override val beakMovement = ClosedBeakMovement()
    override val bossMovement = WiggleBoss1BossMovement()

    init {
        tentacleMovement.addMovement(StretchOutTentacleMovement(0.3f))
        tentacleMovement.addMovement(WaveTentacleMovement(5.0.degrees, 0.4f, 0.1f))
    }
}

class DefendAttack(override val duration: Duration = 2.0.seconds) : Boss1Attack() {
    override val tentacleMovement = CompoundTentacleMovement()
    override val beakMovement = ClosedBeakMovement()
    override val bossMovement = IdleBoss1BossMovement()

    init {
        tentacleMovement.addMovement(DefendTentacleMovement())
        tentacleMovement.addMovement(WaveTentacleMovement(5.0.degrees, 0.4f, 0.1f))
    }
}

class FlyAttack(target: BossTarget? = Game.world.decideOnTarget()) : Boss1Attack() {
    override val tentacleMovement = StretchDownTentacleMovement(0.3f)
    override val beakMovement = ScreamBeakMovement()
    override val bossMovement = FlyAttackBoss1BossMovement(target?.x ?: 0.0f, target?.y ?: 0.0f) {
        setDone()
    }

    init {
        if (target == null)
            setDone()
    }
}

class SpinAttack(override val duration: Duration = 4.0.seconds) : Boss1Attack() {
    override val tentacleMovement = StretchOutTentacleMovement(0.3f)
    override val beakMovement = ScreamBeakMovement()
    override val bossMovement = SpinAttackBoss1BossMovement()
}

class SpinFlyAttack(override val duration: Duration = 4.0.seconds, target: BossTarget? = Game.world.decideOnTarget()) : Boss1Attack() {
    override val tentacleMovement = StretchOutTentacleMovement(0.3f)
    override val beakMovement = ScreamBeakMovement()
    override val bossMovement = SpinFlyAttackBoss1BossMovement(target)
}

class ScreamAttack(override val duration: Duration = 5.0.seconds) : Boss1Attack() {
    override val tentacleMovement = StretchOutTentacleMovement(0.3f)
    override val beakMovement = ScreamBeakMovement()
    override val bossMovement = ShakeBossMovement()

    override fun onStart(boss: Boss) {
        Game.areaEffects.spawnEffect(boss as Boss1, AreaEffectType.SHOCKWAVE, AreaEffectSourceType.MOVING, AreaEffectGrowthType.LINEAR, 64.0f, 70.0f, duration)
    }
}
