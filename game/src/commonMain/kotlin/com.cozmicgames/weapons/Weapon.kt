package com.cozmicgames.weapons

abstract class Weapon {
    abstract val name: String
    abstract val damage: Int
    abstract val fireRate: Float

    fun fire() {
        println("Firing $name")
    }
}