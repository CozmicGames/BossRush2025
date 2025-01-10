package com.cozmicgames.physics

interface Hittable {
    val id: String

    val canHit: Boolean get() = true

    fun onHit(x: Float, y: Float)
}