package com.cozmicgames.bosses.boss3

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.cozmicgames.entities.worldObjects.animations.ParalyzeAnimation
import com.cozmicgames.physics.Hittable
import com.littlekt.math.geom.Angle
import com.littlekt.math.geom.degrees
import kotlin.math.pow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class Arm(val boss: Boss3, val index: Int, val flip: Boolean, val layer: Int, val baseRotation: Angle, val scale: Float) : Hittable, PlayerDamageSource {
    override val id = "boss3arm$index"

    var x = 0.0f
    var y = 0.0f
    var rotation = 0.0.degrees

    val armAngle get() = rotation + baseRotation
    val parts: List<ArmPart>
    val isParalyzed get() = paralyzeTimer > 0.0.seconds || boss.isParalyzed

    private var paralyzeTimer = 0.0.seconds

    override val damageSourceX get() = boss.x
    override val damageSourceY get() = boss.y

    init {
        val parts = arrayListOf<ArmPart>()

        parts.add(ArmPart(this, null, flip, 0, Game.resources.boss3arm, scale, layer))
        parts.add(Claw(this, parts[0], flip, 0, scale, layer))

        this.parts = parts
    }

    fun update(delta: Duration, movement: ArmMovement) {
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
            parts.forEachIndexed { index, part ->
                val strength = (index / 2.0f).pow(2.0f)
                part.addEntityAnimation(ParalyzeAnimation(duration, strength))
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