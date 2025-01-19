package com.cozmicgames.bosses.boss2

import com.cozmicgames.Constants
import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.cozmicgames.physics.Hittable
import com.littlekt.math.geom.degrees
import kotlin.time.Duration

class Body(val boss: Boss2, val scale: Float, layer: Int) : Hittable, PlayerDamageSource {
    override val id = "boss2body"

    val parts: List<BodyPart>

    var x = 0.0f
    var y = 0.0f
    var rotation = 0.0.degrees

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
        if (Game.players.isHost)
            movement.updateParts(delta, this)
    }

    override fun onDamageHit() {
        boss.paralyze()
    }
}