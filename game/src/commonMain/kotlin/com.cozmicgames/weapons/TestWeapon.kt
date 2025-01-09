package com.cozmicgames.weapons

import com.littlekt.math.geom.degrees

class TestWeapon : Weapon() {
    override val name = "Energy Harpoon"
    override val damage = 10
    override val fireRate = 0.5f
    override val canContinoousFire = true
    override val projectileType = ProjectileType.ENERGY_BEAM
    override val projectileSpeed = 1000.0f
    override val spread = 0.0.degrees
}