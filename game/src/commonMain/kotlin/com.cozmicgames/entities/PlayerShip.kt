package com.cozmicgames.entities

import com.cozmicgames.Constants
import com.cozmicgames.Game
import com.cozmicgames.graphics.RenderLayers
import com.cozmicgames.multiplayer.Player
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.RectangleCollisionShape
import com.cozmicgames.weapons.StandardWeapon
import com.cozmicgames.weapons.Weapon
import com.littlekt.graphics.g2d.SpriteBatch
import com.littlekt.math.geom.cosine
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.sine
import com.littlekt.util.seconds
import kotlin.time.Duration

class PlayerShip(private val player: Player) : Entity(player.state.id) {
    override val renderLayer = RenderLayers.PLAYER_BEGIN

    var movementSpeed = 1.0f
    var rotationSpeed = 1.0f

    var primaryWeapon = StandardWeapon()
    var secondaryWeapon: Weapon? = null

    override val collider = Collider(RectangleCollisionShape(64.0f, 64.0f, 0.0f.degrees), this)

    private var firePrimaryCooldown = 0.0f
    private var fireSecondaryCooldown = 0.0f

    init {
        Game.physics.addCollider(collider)
    }

    override fun updateEntity(delta: Duration) {
        var deltaX = 0.0f
        var deltaY = 0.0f
        var deltaRotation = 0.0f

        player.state.getState<Float>("inputX")?.let {
            deltaX = it
        }

        player.state.getState<Float>("inputY")?.let {
            deltaY = it
        }

        player.state.getState<Float>("inputRotation")?.let {
            deltaRotation = it
        }

        player.state.getState<Boolean>("inputUsePrimary")?.let {
            if (it)
                primaryFire()
        }

        player.state.getState<Boolean>("inputUseSecondary")?.let {
            if (it)
                secondaryFire()
        }

        x += Game.physics.scaleSpeedX(collider, deltaX) * movementSpeed * Constants.PLAYER_SHIP_BASE_MOVEMENT_SPEED
        y += Game.physics.scaleSpeedY(collider, deltaY) * movementSpeed * Constants.PLAYER_SHIP_BASE_MOVEMENT_SPEED

        rotation += deltaRotation.degrees * rotationSpeed * Constants.PLAYER_SHIP_BASE_ROTATION_SPEED

        collider.x = x
        collider.y = y
        (collider.shape as RectangleCollisionShape).angle = rotation
        Game.physics.updatePlayerCollider(collider)
        x = collider.x
        y = collider.y

        firePrimaryCooldown -= delta.seconds
        if (firePrimaryCooldown < 0.0f)
            firePrimaryCooldown = 0.0f

        fireSecondaryCooldown -= delta.seconds
        if (fireSecondaryCooldown < 0.0f)
            fireSecondaryCooldown = 0.0f
    }

    override fun render(batch: SpriteBatch) {
        batch.draw(Game.resources.testPlayer, x, y, 32.0f, 32.0f, 64.0f, 64.0f, scale, scale, rotation, color, false, false)
    }

    fun primaryFire() {
        if (firePrimaryCooldown > 0.0f)
            return

        val weapon = primaryWeapon ?: return

        val state = Game.players.getMyPlayerState()

        val direction = rotation + weapon.spread * (Game.random.nextFloat() * 2.0f - 1.0f)

        state.setState("spawnProjectileType", weapon.projectileType.ordinal)
        state.setState("spawnProjectileX", x)
        state.setState("spawnProjectileY", y)
        state.setState("spawnProjectileDirectionX", direction.cosine)
        state.setState("spawnProjectileDirectionY", direction.sine)
        state.setState("spawnProjectileSpeed", weapon.projectileSpeed)

        firePrimaryCooldown = weapon.fireRate
    }

    fun secondaryFire() {
        if (fireSecondaryCooldown > 0.0f)
            return

        val weapon = secondaryWeapon ?: return

        val state = Game.players.getMyPlayerState()

        val direction = rotation + weapon.spread * (Game.random.nextFloat() * 2.0f - 1.0f)

        state.setState("spawnProjectileType", weapon.projectileType.ordinal)
        state.setState("spawnProjectileX", x)
        state.setState("spawnProjectileY", y)
        state.setState("spawnProjectileDirectionX", direction.cosine)
        state.setState("spawnProjectileDirectionY", direction.sine)
        state.setState("spawnProjectileSpeed", weapon.projectileSpeed)

        fireSecondaryCooldown = weapon.fireRate
    }
}