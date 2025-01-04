package com.cozmicgames.weapons

abstract class Weapon {
    abstract val name: String
    abstract val damage: Int
    abstract val fireRate: Float
    abstract val canContinoousFire: Boolean
    abstract val projectileType: ProjectileType
    abstract val projectileSpeed: Float
}