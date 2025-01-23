package com.cozmicgames.events

import com.cozmicgames.Game
import com.cozmicgames.physics.Grabbable

object Events {
    fun hit(id: String): String = "hit:$id"

    fun impulseHit(id: String, x: Float, y: Float, strength: Float): String = "impulseHit:$id,$x,$y,$strength"

    fun playerDeath(id: String): String = "playerDeath$id"

    fun grab(id: String, fromId: String): String = "grab:$id,$fromId"

    fun release(id: String, impulseX: Float = 0.0f, impulseY: Float = 0.0f): String = "release:$id,$impulseX,$impulseY"

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
                val id = event.substringAfter(":")
                val player = Game.players.players.find { it.state.id == id }
                player?.ship?.onDeath()
            }

            event.startsWith("grab") -> {
                val data = event.substringAfter(":")
                val parts = data.split(",")
                if (parts.size == 2) {
                    val id = parts[0]
                    val fromId = parts[1]

                    val hittable = Game.physics.hittables[id]
                    if (hittable is Grabbable)
                        hittable.onGrabbed(fromId)
                }
            }

            event.startsWith("release") -> {
                val data = event.substringAfter(":")
                val parts = data.split(",")

                if (parts.size == 3) {
                    val id = parts[0]
                    val impulseX = parts[1].toFloatOrNull()
                    val impulseY = parts[2].toFloatOrNull()

                    val hittable = Game.physics.hittables[id]
                    if (hittable is Grabbable && impulseX != null && impulseY != null)
                        hittable.onReleased(impulseX, impulseY)
                }
            }
        }
    }
}