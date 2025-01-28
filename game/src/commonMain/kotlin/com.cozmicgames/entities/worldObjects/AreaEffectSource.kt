package com.cozmicgames.entities.worldObjects

interface AreaEffectSource {
    val effectSourceX: Float
    val effectSourceY: Float

    fun shouldHitWithAreaEffect(id: String): Boolean = true
}