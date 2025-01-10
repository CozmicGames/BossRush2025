package com.cozmicgames.entities

class EntityManager {
    private val objects = arrayListOf<Entity>()

    fun add(entity: Entity) {
        objects += entity
    }

    fun remove(entity: Entity) {
        objects -= entity
    }
}