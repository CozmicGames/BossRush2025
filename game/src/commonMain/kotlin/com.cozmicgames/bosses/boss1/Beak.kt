package com.cozmicgames.bosses.boss1

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.littlekt.math.geom.degrees
import kotlin.time.Duration

class Beak(private val boss: Boss1, layer: Int): PlayerDamageSource {
    override val id = "boss1beak"

    val leftBeak = BeakPart(this, true, layer)
    val rightBeak = BeakPart(this, false, layer)

    var x = 0.0f
    var y = 0.0f
    var rotation = 0.0.degrees
    var beakAngle = 0.0.degrees

    override val damageSourceX get() = boss.x
    override val damageSourceY get() = boss.y

    fun update(delta: Duration, movement: BeakMovement) {
        if (Game.players.isHost) {
            movement.updateBeak(delta, this)
            Game.players.setGlobalState("boss1beak", beakAngle.degrees)
        } else
            beakAngle = (Game.players.getGlobalState("boss1beak") ?: 0.0f).degrees

        leftBeak.rotation = rotation - beakAngle * 0.5f
        rightBeak.rotation = rotation + beakAngle * 0.5f
    }
}