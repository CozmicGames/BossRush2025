package com.cozmicgames.entities

import com.cozmicgames.Constants
import com.cozmicgames.Game
import com.cozmicgames.physics.Collider
import com.cozmicgames.weapons.Weapon
import com.littlekt.graphics.g2d.SpriteBatch
import com.littlekt.math.geom.degrees
import kotlin.time.Duration

class PlayerShip {
    var deltaX = 0.0f
    var deltaY = 0.0f
    var deltaRotation = 0.0f

    var x = 0.0f
    var y = 0.0f
    var rotation = 0.0f.degrees

    var movementSpeed = 1.0f
    var rotationSpeed = 1.0f

    var primaryWeapon: Weapon? = null
    var secondaryWeapon: Weapon? = null

    val collider = Collider()

    init {
        collider.radius = 32.0f
        Game.physics.addCollider(collider)
    }

    fun update(delta: Duration) {
        x += Game.physics.scaleSpeedX(collider, deltaX) * movementSpeed * Constants.PLAYER_SHIP_BASE_MOVEMENT_SPEED
        y += Game.physics.scaleSpeedY(collider, deltaY) * movementSpeed * Constants.PLAYER_SHIP_BASE_MOVEMENT_SPEED
        rotation += (deltaRotation * rotationSpeed * Constants.PLAYER_SHIP_BASE_ROTATION_SPEED).degrees

        collider.x = x
        collider.y = y
        Game.physics.updateCollider(collider)
        x = collider.x
        y = collider.y
    }

    fun render(batch: SpriteBatch) {
        batch.draw(Game.resources.testTexture, x, y, originX = 32.0f, originY = 32.0f, rotation = rotation, width = 64.0f, height = 64.0f)
    }

    fun primaryFire() {
    }

    fun secondaryFire() {
    }
}