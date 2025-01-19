package com.cozmicgames.entities.worldObjects

import com.littlekt.math.geom.Angle

interface ProjectileSource {
    val projectileSourceId: String
    val muzzleX: Float
    val muzzleY: Float
    val muzzleRotation: Angle
}