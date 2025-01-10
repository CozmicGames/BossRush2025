package com.cozmicgames.events

import com.cozmicgames.Game

object Events {
    fun hit(id: String, x: Float, y: Float): String = "hit:$id"

    fun shockwaveHit(id: String, x: Float, y: Float, strength: Float): String = "shockwaveHit:$id,$x,$y,$strength"

    fun process(event: String) {
        when {
            event.startsWith("hit") -> {
                val id = event.substringAfter(":")
                val hittable = Game.physics.hittables[id]
                hittable?.onDamageHit()
            }

            event.startsWith("shockwaveHit") -> {
                val data = event.substringAfter(":")
                val parts = data.split(",")
                if (parts.size == 4) {
                    val id = parts[0]
                    val x = parts[1].toFloatOrNull()
                    val y = parts[2].toFloatOrNull()
                    val strength = parts[3].toFloatOrNull()

                    val hittable = Game.physics.hittables[id]
                    if (hittable != null && x != null && y != null && strength != null)
                        hittable.onShockwaveHit(x, y, strength)

                }
            }
        }
    }
}