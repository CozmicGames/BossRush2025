package com.cozmicgames.bosses.boss2

import com.cozmicgames.Constants
import com.cozmicgames.Game
import com.cozmicgames.bosses.BossHittable
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.cozmicgames.entities.worldObjects.animations.ParalyzeAnimation
import com.littlekt.math.geom.degrees
import kotlin.math.pow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class Body(override val boss: Boss2, val scale: Float, layer: Int) : BossHittable, PlayerDamageSource {
    override val id = "boss2body"

    override var x = 0.0f
    override var y = 0.0f
    var rotation = 0.0.degrees

    val parts: List<BodyPart>
    val isParalyzed get() = paralyzeTimer > 0.0.seconds || boss.isParalyzed

    private var paralyzeTimer = 0.0.seconds


    override val damageSourceX get() = boss.x
    override val damageSourceY get() = boss.y

    init {
        val parts = arrayListOf<BodyPart>()

        repeat(Constants.BOSS2_BODY_PARTS) {
            parts.add(BodyPart(this, if (it > 0) parts[it - 1] else null, it, layer))
        }

        this.parts = parts
    }

    fun update(delta: Duration, movement: BodyMovement) {
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
                val strength = (index / Constants.BOSS2_BODY_PARTS.toFloat()).pow(2.0f)
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
        if (boss.isInvulnerable)
            return

        Game.audio.hitEnemySound.play(0.5f)

        paralyze()
    }

    override fun onBaitHit() {
        boss.paralyze()
    }
}