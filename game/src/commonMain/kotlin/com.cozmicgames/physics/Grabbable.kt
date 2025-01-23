package com.cozmicgames.physics

interface Grabbable: Hittable {
    val isGrabbed: Boolean

    fun onGrabbed(id: String)

    fun onReleased(impulseX: Float, impulseY: Float)
}