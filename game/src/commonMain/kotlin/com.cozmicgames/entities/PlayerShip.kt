package com.cozmicgames.entities

import com.cozmicgames.Constants
import com.cozmicgames.Game
import com.cozmicgames.graphics.RenderLayers
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.multiplayer.Player
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.RectangleCollisionShape
import com.cozmicgames.weapons.StandardWeapon
import com.cozmicgames.weapons.TestWeapon
import com.cozmicgames.weapons.Weapon
import com.littlekt.graphics.*
import com.littlekt.math.clamp
import com.littlekt.math.geom.absoluteValue
import com.littlekt.math.geom.cosine
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.sine
import com.littlekt.math.isFuzzyZero
import com.littlekt.util.seconds
import kotlin.math.absoluteValue
import kotlin.math.sqrt
import kotlin.time.Duration

class PlayerShip(private val player: Player) : Entity(player.state.id) {
    var movementSpeed = 1.0f
    var rotationSpeed = 1.0f

    var primaryWeapon: Weapon? = StandardWeapon()
    var secondaryWeapon: Weapon? = TestWeapon()

    override val collider = Collider(RectangleCollisionShape(64.0f, 64.0f, 0.0f.degrees), this)

    private var firePrimaryCooldown = 0.0f
    private var fireSecondaryCooldown = 0.0f

    private var flySpeed = 0.0f
    private val lightColor = MutableColor()
    private val mainColor = MutableColor()
    private val darkColor = MutableColor()

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

        val moveX = Game.physics.scaleSpeedX(collider, deltaX) * movementSpeed * Constants.PLAYER_SHIP_BASE_MOVEMENT_SPEED
        val moveY = Game.physics.scaleSpeedY(collider, deltaY) * movementSpeed * Constants.PLAYER_SHIP_BASE_MOVEMENT_SPEED
        val moveRotation = deltaRotation.degrees * rotationSpeed * Constants.PLAYER_SHIP_BASE_ROTATION_SPEED

        x += moveX
        y += moveY
        rotation += moveRotation

        flySpeed = sqrt(moveX * moveX + moveY * moveY) + (moveRotation.degrees).absoluteValue

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

    override fun render(renderer: Renderer) {
        val baseTexture = when (flySpeed) {
            in 0.0f..1.0f -> Game.resources.playerShipBaseStill
            in 1.0f..3.0f -> Game.resources.playerShipBaseSlow
            else -> Game.resources.playerShipBaseFast
        }

        darkColor.set(player.state.color.lighten(-0.15f))
        mainColor.set(player.state.color)
        lightColor.set(player.state.color.lighten(0.15f))

        //darkColor.mul(color)
        //mainColor.mul(color)
        //lightColor.mul(color)

        renderer.submit(RenderLayers.PLAYER_BEGIN) {
            it.draw(baseTexture, x, y, Constants.PLAYER_SHIP_WIDTH * 0.5f, Constants.PLAYER_SHIP_HEIGHT * 0.5f, Constants.PLAYER_SHIP_WIDTH, Constants.PLAYER_SHIP_HEIGHT, scale, scale, rotation, color)
        }

        renderer.submit(RenderLayers.PLAYER_BEGIN + 1) {
            it.draw(Game.resources.playerShipTemplateDark, x, y, Constants.PLAYER_SHIP_WIDTH * 0.5f, Constants.PLAYER_SHIP_HEIGHT * 0.5f, Constants.PLAYER_SHIP_WIDTH, Constants.PLAYER_SHIP_HEIGHT, scale, scale, rotation, lightColor)
        }

        renderer.submit(RenderLayers.PLAYER_BEGIN + 2) {
            it.draw(Game.resources.playerShipTemplateMain, x, y, Constants.PLAYER_SHIP_WIDTH * 0.5f, Constants.PLAYER_SHIP_HEIGHT * 0.5f, Constants.PLAYER_SHIP_WIDTH, Constants.PLAYER_SHIP_HEIGHT, scale, scale, rotation, mainColor)
        }

        renderer.submit(RenderLayers.PLAYER_BEGIN + 3) {
            it.draw(Game.resources.playerShipTemplateLight, x, y, Constants.PLAYER_SHIP_WIDTH * 0.5f, Constants.PLAYER_SHIP_HEIGHT * 0.5f, Constants.PLAYER_SHIP_WIDTH, Constants.PLAYER_SHIP_HEIGHT, scale, scale, rotation, darkColor)
        }
    }

    fun primaryFire() {
        if (firePrimaryCooldown > 0.0f)
            return

        val weapon = primaryWeapon ?: return

        val state = Game.players.getMyPlayerState()

        val spawnX = x + rotation.cosine * Constants.PLAYER_SHIP_WIDTH * 0.47f
        val spawnY = y + rotation.sine * Constants.PLAYER_SHIP_HEIGHT * 0.47f
        val direction = rotation + weapon.spread * (Game.random.nextFloat() * 2.0f - 1.0f)

        state.setState("spawnProjectileType", weapon.projectileType.ordinal)
        state.setState("spawnProjectileX", spawnX)
        state.setState("spawnProjectileY", spawnY)
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

        val spawnX = x + rotation.cosine * Constants.PLAYER_SHIP_WIDTH * 0.47f
        val spawnY = y + rotation.sine * Constants.PLAYER_SHIP_HEIGHT * 0.47f
        val direction = rotation + weapon.spread * (Game.random.nextFloat() * 2.0f - 1.0f)

        state.setState("spawnProjectileType", weapon.projectileType.ordinal)
        state.setState("spawnProjectileX", spawnX)
        state.setState("spawnProjectileY", spawnY)
        state.setState("spawnProjectileDirectionX", direction.cosine)
        state.setState("spawnProjectileDirectionY", direction.sine)
        state.setState("spawnProjectileSpeed", weapon.projectileSpeed)

        fireSecondaryCooldown = weapon.fireRate
    }
}