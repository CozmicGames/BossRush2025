package com.cozmicgames.bosses.boss3

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

class Leg(val boss: Boss3, val index: Int, val flip: Boolean, val layer: Int, val baseRotation: Angle, val scale: Float) : Hittable, PlayerDamageSource {
    override val id = "boss3leg$index"

    override var x = 0.0f
    override var y = 0.0f
    var rotation = 0.0.degrees

    val legAngle get() = rotation + baseRotation
    val parts: List<LegPart>
    val isParalyzed get() = paralyzeTimer > 0.0.seconds || boss.isParalyzed

    private var paralyzeTimer = 0.0.seconds

    override val damageSourceX get() = boss.x
    override val damageSourceY get() = boss.y

    init {
        val parts = arrayListOf<LegPart>()
        val textures = arrayOf(
            Game.textures.boss3legUpper,
            Game.textures.boss3legLower,
            Game.textures.boss3foot
        )

        repeat(3) {
            parts.add(LegPart(this, if (it > 0) parts[it - 1] else null, flip, it, textures[it], scale, layer))
        }

        this.parts = parts
    }

    fun update(delta: Duration, movement: LegMovement) {
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
                val strength = (index / 3.0f).pow(2.0f)
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
        Game.audio.hitEnemySound.play(0.5f)

        paralyze()
    }

    override fun onImpulseHit(x: Float, y: Float, strength: Float) {
        val distance = sqrt(x * x + y * y)

        boss.impulseX = x / distance * strength * 0.15f
        boss.impulseY = y / distance * strength * 0.15f
        boss.impulseSpin = strength * 0.15f
    }
}