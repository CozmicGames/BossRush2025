package com.cozmicgames.events

import com.cozmicgames.Game

object Events {
    fun hit(id: String, x: Float, y: Float): String = "hit:$id,$x,$y"

    fun process(event: String) {
        when {
            event.startsWith("hit") -> {
                val data = event.substringAfter(":")
                val parts = data.split(",")
                if (parts.size == 3) {
                    val id = parts[0]
                    val x = parts[1].toFloatOrNull()
                    val y = parts[2].toFloatOrNull()

                    val hittable = Game.physics.hittables[id]
                    if (hittable != null && x != null && y != null)
                        hittable.onHit(x, y)
                }
            }
        }
    }
}