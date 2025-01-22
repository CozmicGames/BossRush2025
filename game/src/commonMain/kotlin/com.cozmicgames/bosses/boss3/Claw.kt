package com.cozmicgames.bosses.boss3

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.ProjectileSource
import com.littlekt.math.geom.cosine
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.sin
import com.littlekt.math.geom.sine
import com.littlekt.util.seconds
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class Claw(arm: Arm, parent: ArmPart, flip: Boolean, index: Int, partScale: Float, layer: Int) : ArmPart(arm, parent, flip, index, Game.resources.boss3clawBase, partScale, layer), ProjectileSource {
    override val muzzleX: Float
        get() {
            val xOffset = if (flip) -width * 0.4f else width * 0.4f
            val yOffset = -0.1f * height
            val cos = rotation.cosine
            val sin = rotation.sine
            return x + cos * xOffset - sin * yOffset
        }

    override val muzzleY: Float
        get() {
            val xOffset = if (flip) -width * 0.4f else width * 0.4f
            val yOffset = -0.1f * height
            val cos = rotation.cosine
            val sin = rotation.sine
            return y + sin * xOffset + cos * yOffset
        }

    override val muzzleRotation get() = if (flip) rotation + 180.0.degrees else rotation
    override val projectileSourceId = "boss3"

    val upperClawPart = ClawPart(this, flip, true, partScale, layer)
    val lowerClawPart = ClawPart(this, flip, false, partScale, layer)

    var clawAngle = 0.0.degrees

    override fun updateWorldObject(delta: Duration, fightStarted: Boolean) {
        super.updateWorldObject(delta, fightStarted)

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