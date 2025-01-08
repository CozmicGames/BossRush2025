package com.cozmicgames.physics

interface Hittable {
    val id: String

    fun onHit(x: Float, y: Float)
}