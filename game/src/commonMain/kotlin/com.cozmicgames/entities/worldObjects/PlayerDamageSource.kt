package com.cozmicgames.entities.worldObjects

interface PlayerDamageSource {
    val id: String
    val damageSourceX: Float
    val damageSourceY: Float
    val canDamage: Boolean get() = true

    fun onHit() {}
}