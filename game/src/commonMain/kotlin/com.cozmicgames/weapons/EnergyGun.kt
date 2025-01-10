package com.cozmicgames.weapons

import com.littlekt.math.geom.degrees

class EnergyGun : Weapon() {
    override val name = "Energy Gun"
    override val fireRate = 0.2f
    override val canContinoousFire = true
    override val projectileType = ProjectileType.ENERGY_BALL
    override val projectileCount = 1
    override val projectileSpeed = 500.0f
    override val spread = 10.0.degrees
    override val isRandomSpread = true
}