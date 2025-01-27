package com.cozmicgames.entities.worldObjects

import com.cozmicgames.Constants
import com.cozmicgames.Game
import com.cozmicgames.bosses.BossTarget
import com.cozmicgames.entities.worldObjects.animations.HitAnimation
import com.cozmicgames.events.Events
import com.cozmicgames.graphics.RenderLayers
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.particles.effects.ContinuousShotEffect
import com.cozmicgames.graphics.particles.effects.SingleShotEffect
import com.cozmicgames.graphics.particles.effects.TrailEffect
import com.cozmicgames.multiplayer.Player
import com.cozmicgames.physics.*
import com.cozmicgames.utils.Difficulty
import com.cozmicgames.weapons.*
import com.littlekt.graphics.*
import com.littlekt.math.geom.*
import com.littlekt.math.isFuzzyZero
import com.littlekt.util.seconds
import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class PlayerShip(private val player: Player) : WorldObject(player.state.id), ProjectileSource, AreaEffectSource, BossTarget, Grabbable {
    companion object {
        private val PLAYER_SHIP_INVULNERABILITY_TIME = 2.0.seconds
    }

    override val projectileSourceId get() = player.state.id

    var health = 0
    val isDead get() = health <= 0

    var movementSpeed = 1.0f
    var rotationSpeed = 1.0f

    val isInvulnerable get() = invulnerabilityTimer > 0.0.seconds || isGrabbed

    var primaryWeapon: Weapon? = Weapons.REELGUN
    var secondaryWeapon: Weapon? = Weapons.REELGUN

    override var isStunMode = true

    override val collider = Collider(RectangleCollisionShape(64.0f, 64.0f, 0.0f.degrees), this)

    override var isGrabbed = false

    private var grabbedBy: GrabbingObject? = null
    private var grabRotation = 0.0.degrees

    private var firePrimaryCooldown = 0.0.seconds
    private var fireSecondaryCooldown = 0.0.seconds
    private var invulnerabilityTimer = 0.0.seconds

    private var flySpeed = 0.0f
    private val lightColor = MutableColor()
    private val mainColor = MutableColor()
    private val darkColor = MutableColor()

    private var impulseX = 0.0f
    private var impulseY = 0.0f
    private var impulseSpin = 0.0f

    override var muzzleX = 0.0f
        private set

    override var muzzleY = 0.0f
        private set

    override var muzzleRotation = 0.0.degrees
        private set

    override var effectSourceX = 0.0f
        private set

    override var effectSourceY = 0.0f
        private set

    private var isBeamProjectileFiring = false

    private val leftTrailEffect = TrailEffect(player.state.id, true)
    private val rightTrailEffect = TrailEffect(player.state.id, false)

    override fun updateWorldObject(delta: Duration, fightStarted: Boolean) {
        if (Game.players.isHost) {
            invulnerabilityTimer -= delta
            if (invulnerabilityTimer < 0.0.seconds)
                invulnerabilityTimer = 0.0.seconds

            var deltaX = impulseX * delta.seconds
            var deltaY = impulseY * delta.seconds
            var deltaRotation = 0.0f

            impulseX *= 1.0f - delta.seconds
            impulseY *= 1.0f - delta.seconds
            impulseSpin *= 1.0f - delta.seconds * 1.05f

            if (impulseX.isFuzzyZero())
                impulseX = 0.0f

            if (impulseY.isFuzzyZero())
                impulseY = 0.0f

            if(impulseSpin.isFuzzyZero())
                impulseSpin = 0.0f

            if (fightStarted && !isGrabbed) {
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
                    if (firePrimaryCooldown <= 0.0.seconds)
                        primaryWeapon?.let { weapon ->
                            if (it)
                                fireWeapon(weapon) { firePrimaryCooldown = it }
                            else
                                stopFiringWeapon(weapon) { firePrimaryCooldown = it }
                        }
                }

                player.state.getState<Boolean>("inputUseSecondary")?.let {
                    if (fireSecondaryCooldown <= 0.0.seconds) {
                        secondaryWeapon?.let { weapon ->
                            if (it)
                                fireWeapon(weapon) { fireSecondaryCooldown = it }
                            else
                                stopFiringWeapon(weapon) { fireSecondaryCooldown = it }
                        }
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
            rotation += moveRotation + impulseSpin.degrees * 5.0f

            if (isGrabbed) {
                grabbedBy?.let {
                    x = it.grabX
                    y = it.grabY
                    rotation = it.grabRotation + grabRotation
                }
            }

            flySpeed = sqrt(moveX * moveX + moveY * moveY) + (moveRotation.degrees).absoluteValue

            (collider.shape as RectangleCollisionShape).angle = rotation
            Game.physics.updatePlayerCollider(collider, x, y)
            x = collider.x
            y = collider.y

            muzzleX = x + rotation.cosine * Constants.PLAYER_SHIP_WIDTH * 0.47f
            muzzleY = y + rotation.sine * Constants.PLAYER_SHIP_HEIGHT * 0.47f
            muzzleRotation = rotation

            effectSourceX = x
            effectSourceY = y

            firePrimaryCooldown -= delta
            if (firePrimaryCooldown < 0.0.seconds)
                firePrimaryCooldown = 0.0.seconds

            fireSecondaryCooldown -= delta
            if (fireSecondaryCooldown < 0.0.seconds)
                fireSecondaryCooldown = 0.0.seconds

            if (fightStarted)
                player.ship.checkCollision()

            player.state.setState("x", x)
            player.state.setState("y", y)
            player.state.setState("rotation", rotation.degrees)
            player.state.setState("health", health)
            player.state.setState("shipColor", color.toRgba8888())
        } else {
            x = player.state.getState<Float>("x") ?: x
            y = player.state.getState<Float>("y") ?: y
            rotation = player.state.getState<Float>("rotation")?.degrees ?: rotation
            health = player.state.getState("health") ?: health
            color.setRgba8888(player.state.getState("shipColor") ?: color.toRgba8888())
        }
    }

    fun checkCollision() {
        if (!Game.players.isHost)
            return

        if (isInvulnerable)
            return

        Game.physics.checkCollision(collider, { it != collider }) {
            if (!isInvulnerable && it.userData is PlayerDamageSource) {
                health--

                if (health <= 0) {
                    health = 0
                    Game.events.addSendEvent(Events.playerDeath(projectileSourceId))
                } else {
                    invulnerabilityTimer = PLAYER_SHIP_INVULNERABILITY_TIME

                    Game.events.addSendEvent(Events.hit(projectileSourceId))

                    val impulseX = (x - it.userData.damageSourceX) * 0.05f
                    val impulseY = (y - it.userData.damageSourceY) * 0.05f

                    Game.events.addSendEvent(Events.impulseHit(projectileSourceId, impulseX, impulseY, 20.0f))
                }
            }
        }
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

    private fun fireWeapon(weapon: Weapon, setCooldown: (Duration) -> Unit) {
        if (isInvulnerable)
            return

        if (isBeamProjectileFiring)
            return

        val state = Game.players.getMyPlayerState()

        state.setState("spawnProjectileType", weapon.projectileType.ordinal)
        state.setState("spawnProjectileX", muzzleX)
        state.setState("spawnProjectileY", muzzleY)
        state.setState("spawnProjectileCount", weapon.projectileCount)

        repeat(weapon.projectileCount) {
            var direction = if (weapon.isRandomSpread)
                weapon.spread * (Game.random.nextFloat() * 2.0f - 1.0f)
            else
                weapon.spread * (it - weapon.projectileCount * 0.5f) / weapon.projectileCount

            if (weapon.projectileType.baseType is BulletProjectileType)
                direction += rotation

            state.setState("spawnProjectileDirection$it", direction.degrees)
        }

        state.setState("spawnProjectileSpeed", weapon.projectileSpeed)
        state.setState("spawnProjectileSpeedFalloff", weapon.projectileSpeedFalloff)

        when (weapon.projectileType.baseType) {
            is BulletProjectileType -> {
                setCooldown(weapon.fireRate)
            }

            is BeamProjectileType -> {
                isBeamProjectileFiring = true
            }
        }
    }

    private fun stopFiringWeapon(weapon: Weapon, setCooldown: (Duration) -> Unit) {
        if (weapon.projectileType.baseType is BeamProjectileType) {
            val state = Game.players.getMyPlayerState()

            state.setState("stopBeamProjectile", true)

            setCooldown(weapon.fireRate)

            isBeamProjectileFiring = false
        }
    }

    override fun onDamageHit() {
        addEntityAnimation(HitAnimation(PLAYER_SHIP_INVULNERABILITY_TIME))
    }

    override fun onImpulseHit(x: Float, y: Float, strength: Float) {
        val distance = sqrt(x * x + y * y)

        impulseX = x / distance * strength * 0.15f
        impulseY = y / distance * strength * 0.15f
        impulseSpin = strength * 0.15f
    }

    override fun onGrabbed(id: String) {
        val grabbingObject = Game.physics.grabbingObjects[id] ?: return

        isGrabbed = true
        grabbedBy = grabbingObject
        grabRotation = rotation.shortDistanceTo(grabbingObject.grabRotation)
    }

    override fun onReleased(impulseX: Float, impulseY: Float) {
        isGrabbed = false
        invulnerabilityTimer = PLAYER_SHIP_INVULNERABILITY_TIME
        this.impulseX = impulseX
        this.impulseY = impulseY

        val distance = sqrt(impulseX * impulseX + impulseY * impulseY)
        impulseSpin = distance * 0.15f
    }

    fun onDeath() {
        //removeFromWorld()
        //removeFromPhysics()

        //TODO: Play death animation
    }

    fun addToWorld() {
        Game.world.add(this)

        if (Game.players.isHost) {
            Game.particles.add(leftTrailEffect)
            Game.particles.add(rightTrailEffect)
        }
    }

    fun removeFromWorld() {
        Game.world.remove(this)

        if (Game.players.isHost) {
            Game.particles.remove(leftTrailEffect)
            Game.particles.remove(rightTrailEffect)
        }
    }

    fun addToPhysics() {
        Game.physics.addCollider(collider)
        Game.physics.addHittable(this)
    }

    fun removeFromPhysics() {
        Game.physics.removeCollider(collider)
        Game.physics.removeHittable(this)
    }

    fun initialize(difficulty: Difficulty, spawnX: Float, spawnY: Float, spawnRotation: Angle) {
        health = difficulty.basePlayerHealth
        x = spawnX
        y = spawnY
        rotation = spawnRotation
        (collider.shape as RectangleCollisionShape).angle = rotation
        Game.physics.updatePlayerCollider(collider, x, y)
        x = collider.x
        y = collider.y

        primaryWeapon = player.primaryWeapon
        secondaryWeapon = player.secondaryWeapon

        firePrimaryCooldown = 0.0.seconds
        fireSecondaryCooldown = 0.0.seconds
        invulnerabilityTimer = 0.0.seconds

        impulseX = 0.0f
        impulseY = 0.0f

        isBeamProjectileFiring = false
    }
}