package com.cozmicgames.physics

interface Grabbable: Hittable {
    fun onGrabbed(id: String)

    fun onReleased(impulseX: Float, impulseY: Float)
}