package com.cozmicgames.weapons

class StandardWeapon: Weapon() {
    override val name = "Energy Harpoon"
    override val damage = 10
    override val fireRate = 0.2f
    override val canContinoousFire = true
    override val projectileType = ProjectileType.ENERGY_BALL
    override val projectileSpeed = 500.0f
}