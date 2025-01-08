package com.cozmicgames.states.boss1

import com.cozmicgames.Constants
import com.littlekt.math.geom.degrees
import kotlin.time.Duration

class Tentacle(val flip: Boolean, val layer: Int) {
    var x = 0.0f
    var y = 0.0f
    var rotation = 0.0.degrees

    val parts: List<TentaclePart>

    init {
        val parts = arrayListOf<TentaclePart>()

        repeat(Constants.BOSS1_TENTACLE_PARTS) {
            parts.add(TentaclePart(this, if (it > 0) parts[it - 1] else null, flip, it, layer))
        }

        this.parts = parts
    }

    fun update(delta: Duration, movement: TentacleMovement) {
        movement.updateParts(delta, parts)
    }
}