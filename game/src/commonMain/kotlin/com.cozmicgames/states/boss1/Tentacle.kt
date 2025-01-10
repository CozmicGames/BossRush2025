package com.cozmicgames.states.boss1

import com.cozmicgames.Constants
import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.animations.ParalyzeAnimation
import com.cozmicgames.physics.Hittable
import com.littlekt.math.geom.Angle
import com.littlekt.math.geom.degrees
import kotlin.math.pow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class Tentacle(val boss: Boss1, val index: Int, val flip: Boolean, val layer: Int, val baseRotation: Angle, val scale: Float) : Hittable {
    override val id = "boss1tentacle$index"

    var x = 0.0f
    var y = 0.0f
    var rotation = 0.0.degrees

    val tentacleAngle get() = rotation + baseRotation
    val parts: List<TentaclePart>
    val isParalyzed get() = paralyzeTimer > 0.0.seconds

    private var paralyzeTimer = 0.0.seconds

    init {
        val parts = arrayListOf<TentaclePart>()

        repeat(Constants.BOSS1_TENTACLE_PARTS) {
            parts.add(TentaclePart(this, if (it > 0) parts[it - 1] else null, flip, it, layer))
        }

        this.parts = parts
    }

    fun update(delta: Duration, movement: TentacleMovement) {
        if (Game.players.isHost)
            movement.updateParts(delta, this)

        paralyzeTimer -= delta
        if (paralyzeTimer < 0.0.seconds)
            paralyzeTimer = 0.0.seconds
    }

    fun paralyze(duration: Duration = 5.0.seconds, addAnimation: Boolean = true) {
        if (boss.isParalyzed)
            return

        paralyzeTimer = duration

        if (addAnimation)
            parts.forEachIndexed { index, tentacle ->
                val strength = (index / Constants.BOSS1_TENTACLE_PARTS.toFloat()).pow(2.0f)
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
        paralyze()
    }
}