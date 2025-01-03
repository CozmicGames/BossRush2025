package com.cozmicgames.entities

import com.cozmicgames.Game
import com.cozmicgames.weapons.Weapon
import com.littlekt.graphics.g2d.SpriteBatch
import com.littlekt.math.geom.degrees

class PlayerShip {
    var x = 0.0f
    var y = 0.0f
    var rotation = 0.0f

    var movementSpeed = 25.0f
    var rotationSpeed = 10.0f

    var primaryWeapon: Weapon? = null
    var secondaryWeapon: Weapon? = null

    fun render(batch: SpriteBatch) {
        batch.draw(Game.resources.testTexture, x, y, originX = 32.0f, originY = 32.0f, rotation = rotation.degrees, width = 64.0f, height = 64.0f)
    }

    fun primaryFire() {
    }

    fun secondaryFire() {
    }
}