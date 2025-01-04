package com.cozmicgames.multiplayer

import com.cozmicgames.entities.PlayerShip

class Player(val state: PlayerState) {
    val ship = PlayerShip(this)
}