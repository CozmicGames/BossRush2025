package com.cozmicgames.weapons

import com.littlekt.math.geom.degrees

class EnergyHarpoon : Weapon() {
    override val name = "Energy Harpoon"
    override val fireRate = 0.5f
    override val canContinoousFire = true
    override val projectileType = ProjectileType.ENERGY_BEAM
    override val projectileCount = 1
    override val projectileSpeed = 1500.0f
    override val spread = 0.0.degrees
    override val isRandomSpread = false
}