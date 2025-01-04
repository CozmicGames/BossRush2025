package com.cozmicgames.entities

import com.cozmicgames.Constants
import com.cozmicgames.Game
import com.cozmicgames.multiplayer.Player
import com.cozmicgames.physics.Collider
import com.cozmicgames.weapons.StandardWeapon
import com.cozmicgames.weapons.Weapon
import com.littlekt.graphics.g2d.SpriteBatch
import com.littlekt.math.geom.cos
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.sin
import com.littlekt.util.seconds
import kotlin.time.Duration

class PlayerShip(val player: Player) {
    var deltaX = 0.0f
    var deltaY = 0.0f
    var deltaRotation = 0.0f

    var x = 0.0f
    var y = 0.0f
    var rotation = 0.0f.degrees

    var movementSpeed = 1.0f
    var rotationSpeed = 1.0f

    var primaryWeapon = StandardWeapon()
    var secondaryWeapon: Weapon? = null

    val collider = Collider()

    private var firePrimaryCooldown = 0.0f
    private var fireSecondaryCooldown = 0.0f

    init {
        collider.radius = 32.0f
        Game.physics.addCollider(collider)
    }

    fun update(delta: Duration) {
        x += Game.physics.scaleSpeedX(collider, deltaX) * movementSpeed * Constants.PLAYER_SHIP_BASE_MOVEMENT_SPEED
        y += Game.physics.scaleSpeedY(collider, deltaY) * movementSpeed * Constants.PLAYER_SHIP_BASE_MOVEMENT_SPEED

        rotation += (deltaRotation * rotationSpeed * Constants.PLAYER_SHIP_BASE_ROTATION_SPEED).degrees
        //TODO: Fix Flipping

        collider.x = x
        collider.y = y
        Game.physics.updateCollider(collider)
        x = collider.x
        y = collider.y

        firePrimaryCooldown -= delta.seconds
        if (firePrimaryCooldown < 0.0f)
            firePrimaryCooldown = 0.0f

        fireSecondaryCooldown -= delta.seconds
        if (fireSecondaryCooldown < 0.0f)
            fireSecondaryCooldown = 0.0f
    }

    fun render(batch: SpriteBatch) {
        batch.draw(Game.resources.testTexture, x, y, originX = 32.0f, originY = 32.0f, rotation = rotation, width = 64.0f, height = 64.0f)
    }

    fun primaryFire() {
        if (firePrimaryCooldown > 0.0f)
            return

        val weapon = primaryWeapon ?: return

        val state = Game.players.getMyPlayerState()

        state.setState("spawnProjectileType", weapon.projectileType.ordinal)
        state.setState("spawnProjectileX", x)
        state.setState("spawnProjectileY", y)
        state.setState("spawnProjectileDirectionX", cos(rotation))
        state.setState("spawnProjectileDirectionY", sin(rotation))
        state.setState("spawnProjectileSpeed", weapon.projectileSpeed)

        firePrimaryCooldown = weapon.fireRate
    }

    fun secondaryFire() {
        if (fireSecondaryCooldown > 0.0f)
            return

        val weapon = secondaryWeapon ?: return

        val state = Game.players.getMyPlayerState()

        state.setState("spawnProjectileType", weapon.projectileType)
        state.setState("spawnProjectileX", x)
        state.setState("spawnProjectileY", y)
        state.setState("spawnProjectileDirectionX", cos(rotation))
        state.setState("spawnProjectileDirectionY", sin(rotation))

        fireSecondaryCooldown = weapon.fireRate
    }
}