package com.cozmicgames.bosses.boss4

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.littlekt.math.geom.degrees
import com.littlekt.util.seconds
import kotlin.math.sin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class Beak(private val boss: Boss4, val scale: Float, layer: Int): PlayerDamageSource {
    override val id = "boss1beak"

    val leftBeak = BeakPart(this, true, layer)
    val rightBeak = BeakPart(this, false, layer)

    var x = 0.0f
    var y = 0.0f
    var rotation = 0.0.degrees
    var beakAngle = 0.0.degrees

    override val damageSourceX get() = boss.x
    override val damageSourceY get() = boss.y

    private var timer = 0.0.seconds

    fun update(delta: Duration, movement: BeakMovement) {
        if (Game.players.isHost) {
            movement.updateBeak(delta, this)

            timer += delta
            beakAngle = 30.0.degrees * (sin(timer.seconds * 3.0f) * 0.5f + 0.5f)

            Game.players.setGlobalState("boss1beak", beakAngle.degrees)
        } else
            beakAngle = (Game.players.getGlobalState("boss1beak") ?: 0.0f).degrees

        leftBeak.rotation = rotation + beakAngle * 0.5f
        rightBeak.rotation = rotation - beakAngle * 0.5f
    }
}