package com.cozmicgames.entities.worldObjects

import com.cozmicgames.graphics.Renderer
import kotlin.time.Duration

class World {
    private val objects = arrayListOf<WorldObject>()

    var shouldUpdate = false

    fun add(worldObject: WorldObject) {
        objects += worldObject
        worldObject.onAddToWorld()
    }

    fun remove(worldObject: WorldObject) {
        objects -= worldObject
        worldObject.onRemoveFromWorld()
    }

    fun update(delta: Duration) {
        if (!shouldUpdate)
            return

        for (entity in objects) {
            entity.update(delta)
        }
    }

    fun render(renderer: Renderer) {
        for (entity in objects) {
            entity.render(renderer)
        }
    }
}