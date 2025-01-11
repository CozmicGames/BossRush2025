package com.cozmicgames.events

import com.cozmicgames.Game

object Events {
    fun hit(id: String): String = "hit:$id"

    fun impulseHit(id: String, x: Float, y: Float, strength: Float): String = "impulseHit:$id,$x,$y,$strength"

    fun playerDeath(id: String): String = "playerDeath$id"

    fun process(event: String) {
        when {
            event.startsWith("hit") -> {
                val id = event.substringAfter(":")
                val hittable = Game.physics.hittables[id]
                hittable?.onDamageHit()
            }

            event.startsWith("impulseHit") -> {
                val data = event.substringAfter(":")
                val parts = data.split(",")
                if (parts.size == 4) {
                    val id = parts[0]
                    val x = parts[1].toFloatOrNull()
                    val y = parts[2].toFloatOrNull()
                    val strength = parts[3].toFloatOrNull()

                    val hittable = Game.physics.hittables[id]
                    if (hittable != null && x != null && y != null && strength != null)
                        hittable.onImpulseHit(x, y, strength)
                }
            }

            event.startsWith("playerDeath") -> {
                val id = event.substringAfter("playerDeath")
                val player = Game.players.players.find { it.state.id == id }
                player?.ship?.onDeath()
            }
        }
    }
}