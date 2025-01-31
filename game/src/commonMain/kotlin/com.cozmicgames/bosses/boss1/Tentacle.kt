package com.cozmicgames.bosses.boss1

import com.cozmicgames.Constants
import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.cozmicgames.entities.worldObjects.animations.ParalyzeAnimation
import com.cozmicgames.physics.Hittable
import com.littlekt.math.geom.Angle
import com.littlekt.math.geom.degrees
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class Tentacle(val boss: Boss1, val index: Int, val flip: Boolean, val layer: Int, val baseRotation: Angle, val scale: Float) : Hittable, PlayerDamageSource {
    override val id = "boss1tentacle$index"

    override var x = 0.0f
    override var y = 0.0f
    var rotation = 0.0.degrees

    val tentacleAngle get() = rotation + baseRotation
    val parts: List<TentaclePart>
    val isParalyzed get() = paralyzeTimer > 0.0.seconds || boss.isParalyzed

    private var paralyzeTimer = 0.0.seconds

    override val damageSourceX get() = boss.x
    override val damageSourceY get() = boss.y

    init {
        val parts = arrayListOf<TentaclePart>()

        repeat(Constants.BOSS1_TENTACLE_PARTS) {
            parts.add(TentaclePart(this, if (it > 0) parts[it - 1] else null, flip, it, layer))
        }

        this.parts = parts
    }

    fun update(delta: Duration, movement: TentacleMovement) {
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
        if (boss.isInvulnerable)
            return

        paralyze()
    }

    override fun onImpulseHit(x: Float, y: Float, strength: Float) {
        val distance = sqrt(x * x + y * y)

        boss.impulseX = x / distance * strength * 0.15f
        boss.impulseY = y / distance * strength * 0.15f
        boss.impulseSpin = strength * 0.15f
    }

    override fun onBaitHit() {
        boss.paralyze()
    }
}