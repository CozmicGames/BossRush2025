package com.cozmicgames.physics

import com.littlekt.math.geom.Angle

interface GrabbingObject {
    val grabbingId: String
    val grabX: Float
    val grabY: Float
    val grabRotation: Angle
}