package com.cozmicgames.physics

interface Grabbable: Hittable {
    val x: Float
    val y: Float
    val isGrabbed: Boolean

    fun onGrabbed(id: String)

    fun onReleased(impulseX: Float, impulseY: Float)
}