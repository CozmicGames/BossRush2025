package com.cozmicgames.weapons

import com.littlekt.math.geom.degrees

class EnergyShotgun : Weapon() {
    override val name = "Energy Shotgun"
    override val fireRate = 0.8f
    override val canContinoousFire = true
    override val projectileType = ProjectileType.ENERGY_BALL
    override val projectileCount = 5
    override val projectileSpeed = 300.0f
    override val spread = 30.0.degrees
    override val isRandomSpread = false
}