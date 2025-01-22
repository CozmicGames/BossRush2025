package com.cozmicgames.bosses.boss3

import com.cozmicgames.Game
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.sin
import com.littlekt.util.seconds
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class Claw(arm: Arm, parent: ArmPart, flip: Boolean, index: Int, partScale: Float, layer: Int) : ArmPart(arm, parent, flip, index, Game.resources.boss3clawBase, partScale, layer) {
    val upperClawPart = ClawPart(this, flip, true, partScale, layer)
    val lowerClawPart = ClawPart(this, flip, false, partScale, layer)

    var clawAngle = 0.0.degrees
    var timer = 0.0.seconds

    override fun updateWorldObject(delta: Duration, fightStarted: Boolean) {
        super.updateWorldObject(delta, fightStarted)

        timer += delta
        clawAngle = 30.0.degrees * kotlin.math.sin(timer.seconds * 2.0)

        upperClawPart.rotation = rotation - clawAngle * 0.2f
        lowerClawPart.rotation = rotation + clawAngle * 0.8f
    }

    fun update(delta: Duration, movement: ClawMovement) {
        if (Game.players.isHost) {
            movement.updateClaw(delta, this)
            Game.players.setGlobalState("boss3clawAngle", clawAngle.degrees)
        } else
            clawAngle = (Game.players.getGlobalState("boss3clawAngle") ?: 0.0f).degrees
    }
}