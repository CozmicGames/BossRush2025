package com.cozmicgames.states.boss1

import com.cozmicgames.Game
import com.cozmicgames.entities.PlayerShip
import com.littlekt.math.geom.degrees
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

abstract class Attack {
    abstract val duration: Duration

    abstract val tentacleMovement: TentacleMovement
    abstract val beakMovement: BeakMovement
    abstract val bossMovement: BossMovement

    private var timer = 0.0.seconds
    private val afterAttackActions = arrayListOf<() -> Unit>()

    fun afterAttack(block: () -> Unit) {
        afterAttackActions += block
    }

    fun isDone(delta: Duration): Boolean {
        timer += delta

        if (timer >= duration) {
            afterAttackActions.forEach { it() }
            return true
        }

        return false
    }
}

class SpinAttack(override val duration: Duration = 4.0.seconds) : Attack() {
    override val tentacleMovement: TentacleMovement = CompoundTentacleMovement()
    override val beakMovement: BeakMovement = ScreamBeakMovement()
    override val bossMovement: BossMovement = SpinAttackBossMovement()

    init {
        with(tentacleMovement as CompoundTentacleMovement) {
            StretchOutTentacleMovement(0.8f)
            addMovement(WaveTentacleMovement(10.0.degrees, 0.3f, 0.2f))
        }
    }
}

class GrabAttack(ship: PlayerShip = Game.players.players.random().ship): Attack() {
    override val duration: Duration = 2.0.seconds

    override val tentacleMovement: TentacleMovement = CompoundTentacleMovement()
    override val beakMovement: BeakMovement = ScreamBeakMovement()
    override val bossMovement: BossMovement = GrabAttackMovement(ship)
}
