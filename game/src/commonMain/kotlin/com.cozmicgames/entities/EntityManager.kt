package com.cozmicgames.entities

import com.cozmicgames.graphics.Renderer
import kotlin.time.Duration

class EntityManager {
    private val entities = arrayListOf<Entity>()

    fun findEntityById(id: String) = entities.find { it.id == id }

    fun add(entity: Entity) {
        entities += entity
    }

    fun remove(entity: Entity) {
        entities -= entity
    }

    fun update(delta: Duration) {
        for (entity in entities) {
            entity.update(delta)
        }
    }

    fun render(renderer: Renderer) {
        for (entity in entities) {
            renderer.submit(entity.renderLayer) {
                entity.render(it)
            }
        }
    }
}