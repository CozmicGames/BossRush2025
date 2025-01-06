package com.cozmicgames.events

import com.cozmicgames.Game
import com.cozmicgames.entities.Entity
import com.cozmicgames.entities.animations.HitAnimation

object Events {
    fun hit(entity: Entity, x: Float, y: Float): String = "hit:${entity.id},$x,$y"

    fun process(event: String) {
        when {
            event.startsWith("hit") -> {
                val data = event.substringAfter(":")
                val parts = data.split(",")
                if (parts.size == 3) {
                    val id = parts[0]
                    val x = parts[1].toFloatOrNull()
                    val y = parts[2].toFloatOrNull()
                    val entity = Game.entities.findEntityById(id)

                    if (entity != null && x != null && y != null) {
                        //TODO: Use impact position
                        entity.addEntityAnimation(HitAnimation())
                    }
                }
            }
        }
    }
}