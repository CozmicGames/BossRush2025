package com.cozmicgames.entities

import com.littlekt.math.geom.Angle

interface ProjectileSource {
    val muzzleX: Float
    val muzzleY: Float
    val muzzleRotation: Angle
}