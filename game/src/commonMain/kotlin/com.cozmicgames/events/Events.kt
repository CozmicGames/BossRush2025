package com.cozmicgames.events

import com.cozmicgames.Game
import com.cozmicgames.entities.Entity

object Events {
    fun hit(entity: Entity): String = "hit:${entity.id}"

    fun process(event: String) {
        when {
            event.startsWith("hit") -> {
                val id = event.substringAfter(":")
                val entity = Game.entities.findEntityById(id)
                entity?.playHitAnimation()
            }
        }
    }
}