package com.cozmicgames.bosses.boss1

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.PlayerShip
import com.cozmicgames.weapons.AreaEffectGrowthType
import com.cozmicgames.weapons.AreaEffectSourceType
import com.cozmicgames.weapons.AreaEffectType
import com.littlekt.math.geom.degrees
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

abstract class Attack {
    open val duration: Duration? = null

    abstract val tentacleMovement: TentacleMovement
    abstract val beakMovement: BeakMovement
    abstract val bossMovement: BossMovement

    private var timer = 0.0.seconds
    private val afterAttackActions = arrayListOf<() -> Unit>()
    private var isDone = false

    fun afterAttack(block: () -> Unit) {
        afterAttackActions += block
    }

    fun setDone() {
        isDone = true
    }

    fun isDone(delta: Duration): Boolean {
        timer += delta

        val duration = duration

        if (duration != null && timer >= duration)
            setDone()

        if (isDone)
            afterAttackActions.forEach { it() }

        return isDone
    }

    fun cancel(runAfterAttackListeners: Boolean) {
        setDone()

        if (!runAfterAttackListeners)
            afterAttackActions.clear()
    }

    open fun onStart(boss: Boss1) {}
}

class StretchAttack(override val duration: Duration = 4.0.seconds) : Attack() {
    override val tentacleMovement = CompoundTentacleMovement()
    override val beakMovement = ClosedBeakMovement()
    override val bossMovement = WiggleBossMovement()

    init {
        tentacleMovement.addMovement(StretchOutTentacleMovement(0.3f))
        tentacleMovement.addMovement(WaveTentacleMovement(5.0.degrees, 0.4f, 0.1f))
    }
}

class DefendAttack(override val duration: Duration = 2.0.seconds) : Attack() {
    override val tentacleMovement = CompoundTentacleMovement()
    override val beakMovement = ClosedBeakMovement()
    override val bossMovement = IdleBossMovement()

    init {
        tentacleMovement.addMovement(DefendTentacleMovement())
        tentacleMovement.addMovement(WaveTentacleMovement(5.0.degrees, 0.4f, 0.1f))
    }
}

class GrabAttack(ship: PlayerShip = Game.players.players.random().ship) : Attack() {
    override val duration: Duration = 2.0.seconds

    override val tentacleMovement = SequenceTentacleMovement(
        duration / 2, listOf(
            StretchDownTentacleMovement(0.3f),
            GrabTentacleMovement(0.2f)
        )
    )
    override val beakMovement = ScreamBeakMovement()
    override val bossMovement = GrabAttackMovement(ship)
}

class FlyAttack(ship: PlayerShip = Game.players.players.random().ship) : Attack() {
    override val tentacleMovement = StretchDownTentacleMovement(0.3f)
    override val beakMovement = ScreamBeakMovement()
    override val bossMovement = FlyAttackBossMovement(ship.x, ship.y) {
        setDone()
    }
}

class SpinAttack(override val duration: Duration = 4.0.seconds) : Attack() {
    override val tentacleMovement = StretchOutTentacleMovement(0.3f)
    override val beakMovement = ScreamBeakMovement()
    override val bossMovement = SpinAttackBossMovement()
}

class ScreamAttack(override val duration: Duration = 5.0.seconds) : Attack() {
    override val tentacleMovement = StretchOutTentacleMovement(0.3f)
    override val beakMovement = ScreamBeakMovement()
    override val bossMovement = ShakeBossMovement()

    override fun onStart(boss: Boss1) {
        Game.areaEffects.spawnEffect(boss, AreaEffectType.SHOCKWAVE, AreaEffectSourceType.MOVING, AreaEffectGrowthType.LINEAR, 64.0f, 70.0f, duration)
    }
}
