package com.cozmicgames.entities.worldObjects

import com.cozmicgames.Constants
import com.cozmicgames.Game
import com.cozmicgames.graphics.RenderLayers
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.multiplayer.Player
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.Hittable
import com.cozmicgames.physics.RectangleCollisionShape
import com.cozmicgames.weapons.*
import com.littlekt.graphics.*
import com.littlekt.math.geom.cosine
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.sine
import com.littlekt.math.isFuzzyZero
import com.littlekt.util.seconds
import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.time.Duration

class PlayerShip(private val player: Player) : WorldObject(player.state.id), ProjectileSource, AreaEffectSource, Hittable {
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

    private var impulseX = 0.0f
    private var impulseY = 0.0f

    override var muzzleX = 0.0f
    override var muzzleY = 0.0f
    override var muzzleRotation = 0.0.degrees

    override var effectSourceX = 0.0f
    override var effectSourceY = 0.0f

    private var isBeamProjectileFiring = false

    override fun updateWorldObject(delta: Duration) {
        var deltaX = impulseX * delta.seconds
        var deltaY = impulseY * delta.seconds
        var deltaRotation = 0.0f

        impulseX *= 0.9f
        impulseY *= 0.9f

        if (impulseX.isFuzzyZero())
            impulseX = 0.0f

        if (impulseY.isFuzzyZero())
            impulseY = 0.0f

        player.state.getState<Float>("inputX")?.let {
            deltaX += it
        }

        player.state.getState<Float>("inputY")?.let {
            deltaY += it
        }

        player.state.getState<Float>("inputRotation")?.let {
            deltaRotation = it
        }

        player.state.getState<Boolean>("inputUsePrimary")?.let {
            if (firePrimaryCooldown <= 0.0f)
                primaryWeapon?.let { weapon ->
                    if (it)
                        fireWeapon(weapon) { firePrimaryCooldown = it }
                    else
                        stopFiringWeapon(weapon) { firePrimaryCooldown = it }
                }
        }

        player.state.getState<Boolean>("inputUseSecondary")?.let {
            if (fireSecondaryCooldown <= 0.0f) {
                secondaryWeapon?.let { weapon ->
                    if (it)
                        fireWeapon(weapon) { fireSecondaryCooldown = it }
                    else
                        stopFiringWeapon(weapon) { fireSecondaryCooldown = it }
                }
            }
        }

        val speedScaleX = Game.physics.getSpeedScaleX(collider, deltaX)
        val speedScaleY = Game.physics.getSpeedScaleY(collider, deltaY)

        val minSpeedScale = min(speedScaleX, speedScaleY)
        player.indicatorColor.set(Constants.INDICATOR_COLOR_BORDER)
        player.indicatorColor.a = 1.0f - minSpeedScale

        val moveX = speedScaleX * deltaX * movementSpeed * Constants.PLAYER_SHIP_BASE_MOVEMENT_SPEED
        val moveY = speedScaleY * deltaY * movementSpeed * Constants.PLAYER_SHIP_BASE_MOVEMENT_SPEED
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

        muzzleX = x + rotation.cosine * Constants.PLAYER_SHIP_WIDTH * 0.47f
        muzzleY = y + rotation.sine * Constants.PLAYER_SHIP_HEIGHT * 0.47f
        muzzleRotation = rotation

        effectSourceX = x
        effectSourceY = y

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

        darkColor.mul(color)
        mainColor.mul(color)
        lightColor.mul(color)

        renderer.submit(RenderLayers.PLAYER_BEGIN) {
            it.draw(baseTexture, x, y, Constants.PLAYER_SHIP_WIDTH * 0.5f, Constants.PLAYER_SHIP_HEIGHT * 0.5f, Constants.PLAYER_SHIP_WIDTH, Constants.PLAYER_SHIP_HEIGHT, scale, scale, rotation, color)
        }

        renderer.submit(RenderLayers.PLAYER_BEGIN + 1) {
            it.draw(Game.resources.playerShipTemplate, x, y, Constants.PLAYER_SHIP_WIDTH * 0.5f, Constants.PLAYER_SHIP_HEIGHT * 0.5f, Constants.PLAYER_SHIP_WIDTH, Constants.PLAYER_SHIP_HEIGHT, scale, scale, rotation, lightColor)
        }

        renderer.submit(RenderLayers.PLAYER_BEGIN + 2) {
            it.draw(Game.resources.playerShipTemplate, x, y, Constants.PLAYER_SHIP_WIDTH * 0.5f, Constants.PLAYER_SHIP_HEIGHT * 0.5f, Constants.PLAYER_SHIP_WIDTH, Constants.PLAYER_SHIP_HEIGHT, scale, scale, rotation, mainColor)
        }

        renderer.submit(RenderLayers.PLAYER_BEGIN + 3) {
            it.draw(Game.resources.playerShipTemplate, x, y, Constants.PLAYER_SHIP_WIDTH * 0.5f, Constants.PLAYER_SHIP_HEIGHT * 0.5f, Constants.PLAYER_SHIP_WIDTH, Constants.PLAYER_SHIP_HEIGHT, scale, scale, rotation, darkColor)
        }
    }

    private fun fireWeapon(weapon: Weapon, setCooldown: (Float) -> Unit) {
        if (isBeamProjectileFiring)
            return

        val state = Game.players.getMyPlayerState()

        var direction = weapon.spread * (Game.random.nextFloat() * 2.0f - 1.0f)

        if (weapon.projectileType.baseType is BulletProjectileType)
            direction += rotation

        state.setState("spawnProjectileType", weapon.projectileType.ordinal)
        state.setState("spawnProjectileX", muzzleX)
        state.setState("spawnProjectileY", muzzleY)
        state.setState("spawnProjectileDirection", direction.degrees)
        state.setState("spawnProjectileSpeed", weapon.projectileSpeed)

        if (weapon.projectileType.baseType is BulletProjectileType)
            setCooldown(weapon.fireRate)

        if (weapon.projectileType.baseType is BeamProjectileType)
            isBeamProjectileFiring = true
    }

    private fun stopFiringWeapon(weapon: Weapon, setCooldown: (Float) -> Unit) {
        if (weapon.projectileType.baseType is BeamProjectileType) {
            val state = Game.players.getMyPlayerState()

            state.setState("stopBeamProjectile", true)

            setCooldown(weapon.fireRate)

            isBeamProjectileFiring = false
        }
    }

    override fun onDamageHit() {
//TODO
    }

    override fun onShockwaveHit(x: Float, y: Float, strength: Float) {
        val dx = this.x - x
        val dy = this.y - y
        val distance = sqrt(dx * dx + dy * dy)

        impulseX = dx / distance * strength * 0.15f
        impulseY = dy / distance * strength * 0.15f
    }

    fun addToWorld() {
        Game.world.add(this)
    }

    fun removeFromWorld() {
        Game.world.remove(this)
    }

    fun addToPhysics() {
        Game.physics.addCollider(collider)
        Game.physics.addHittable(this)
    }

    fun removeFromPhysics() {
        Game.physics.removeCollider(collider)
        Game.physics.removeHittable(this)
    }
}