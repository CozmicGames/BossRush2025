package com.cozmicgames.physics

interface Hittable {
    val id: String

    val x: Float

    val y: Float

    val canHit: Boolean get() = true

    fun onDamageHit() {}

    fun onImpulseHit(x: Float, y: Float, strength: Float) {}
}