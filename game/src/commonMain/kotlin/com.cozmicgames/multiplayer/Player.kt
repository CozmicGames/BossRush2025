package com.cozmicgames.multiplayer

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.PlayerShip
import com.cozmicgames.weapons.Weapons
import com.littlekt.graphics.MutableColor
import com.littlekt.graphics.OrthographicCamera

class Player(val state: PlayerState) {
    var primaryWeapon = Weapons.ENERGY_GUN
    var secondaryWeapon = Weapons.ENERGY_GUN

    val ship = PlayerShip(this)
    val indicatorColor = MutableColor(1.0f, 1.0f, 1.0f, 0.0f)

    val camera by lazy { OrthographicCamera(Game.graphics.width.toFloat(), Game.graphics.height.toFloat()) }
}