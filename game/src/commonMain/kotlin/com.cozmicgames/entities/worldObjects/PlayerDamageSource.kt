package com.cozmicgames.entities.worldObjects

interface PlayerDamageSource {
    val id: String
    val damageSourceX: Float
    val damageSourceY: Float

    fun onHit() {}
}