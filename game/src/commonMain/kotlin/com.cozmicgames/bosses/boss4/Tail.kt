package com.cozmicgames.bosses.boss4

import com.cozmicgames.Constants
import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.cozmicgames.entities.worldObjects.animations.ParalyzeAnimation
import com.cozmicgames.physics.Hittable
import com.littlekt.math.geom.degrees
import kotlin.math.pow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class Tail(val boss: Boss4, val scale: Float, val layer: Int) : Hittable, PlayerDamageSource {
    override val id = "boss4tail"

    override var x = 0.0f
    override var y = 0.0f
    var rotation = 0.0.degrees

    val tailAngle get() = rotation
    val parts: List<TailPart>
    val isParalyzed get() = paralyzeTimer > 0.0.seconds || boss.isParalyzed

    private var paralyzeTimer = 0.0.seconds

    override val damageSourceX get() = boss.x
    override val damageSourceY get() = boss.y

    init {
        val parts = arrayListOf<TailPart>()

        repeat(Constants.BOSS4_TAIL_PARTS) {
            parts.add(TailPart(this, if (it > 0) parts[it - 1] else null, it, layer))
        }

        this.parts = parts
    }

    fun update(delta: Duration, movement: TailMovement) {
        if (Game.players.isHost)
            movement.updateParts(delta, this)

        paralyzeTimer -= delta
        if (paralyzeTimer < 0.0.seconds)
            paralyzeTimer = 0.0.seconds
    }

    fun paralyze(duration: Duration = 5.0.seconds, addAnimation: Boolean = true) {
        if (isParalyzed)
            return

        paralyzeTimer = duration

        if (addAnimation)
            parts.forEachIndexed { index, tentacle ->
                val strength = (index / parts.size.toFloat()).pow(2.0f)
                tentacle.addEntityAnimation(ParalyzeAnimation(duration, strength))
            }
    }

    fun unparalyze() {
        paralyzeTimer = 0.0.seconds
        parts.forEach {
            it.cancelEntityAnimation<ParalyzeAnimation>()
        }
    }

    override fun onDamageHit() {
        if (boss.isInvulnerable)
            return

        paralyze()
    }
}