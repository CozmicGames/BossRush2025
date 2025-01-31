package com.cozmicgames.entities.worldObjects

import com.cozmicgames.Constants
import com.cozmicgames.Game
import com.cozmicgames.TutorialStage
import com.cozmicgames.bosses.BossTarget
import com.cozmicgames.entities.worldObjects.animations.HitAnimation
import com.cozmicgames.graphics.RenderLayers
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.particles.effects.TrailEffect
import com.cozmicgames.Player
import com.cozmicgames.physics.*
import com.cozmicgames.utils.Difficulty
import com.cozmicgames.weapons.*
import com.littlekt.graphics.*
import com.littlekt.math.clamp
import com.littlekt.math.geom.*
import com.littlekt.math.isFuzzyZero
import com.littlekt.util.seconds
import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class PlayerShip(private val player: Player) : WorldObject("player"), ProjectileSource, AreaEffectSource, BossTarget, Grabbable {
    companion object {
        private val PLAYER_SHIP_INVULNERABILITY_TIME = 2.0.seconds
    }

    override val projectileSourceId get() = id

    var health = 0
    val isDead get() = health <= 0

    var movementSpeed = 1.0f
    var rotationSpeed = 1.0f

    val isInvulnerable get() = invulnerabilityTimer > 0.0.seconds || isGrabbed

    var primaryWeapon: Weapon? = Weapons.REELGUN
    var secondaryWeapon: Weapon? = Weapons.REELGUN

    val invulnerabilityFactor get() = 1.0f - (invulnerabilityTimer / invulnerabilityTime).toFloat().clamp(0.0f, 1.0f)
    val primaryCooldownFactor get() = if (firePrimaryCooldownTime == 0.0.seconds) 0.0f else 1.0f - (firePrimaryCooldown / firePrimaryCooldownTime).toFloat().clamp(0.0f, 1.0f)
    val secondaryCooldownFactor get() = if (fireSecondaryCooldownTime == 0.0.seconds) 0.0f else 1.0f - (fireSecondaryCooldown / fireSecondaryCooldownTime).toFloat().clamp(0.0f, 1.0f)

    var tryUsePrimaryWeapon = false
        private set

    var tryUseSecondaryWeapon = false
        private set

    private var triedUsePrimaryWeapon = false
    private var triedUseSecondaryWeapon = false

    override var isStunMode = true

    override val collider = Collider(RectangleCollisionShape(64.0f, 64.0f, 0.0f.degrees), this)

    override var isGrabbed = false
    private var grabbedBy: GrabbingObject? = null
    private var grabRotation = 0.0.degrees

    private var firePrimaryCooldownTime = 0.01.seconds
    private var firePrimaryCooldown = 0.0.seconds

    private var fireSecondaryCooldownTime = 0.01.seconds
    private var fireSecondaryCooldown = 0.0.seconds

    private var invulnerabilityTime = 0.01.seconds
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

    private val leftTrailEffect = TrailEffect(id, true)
    private val rightTrailEffect = TrailEffect(id, false)
    private var isEngineSoundPlaying = false

    override fun updateWorldObject(delta: Duration, isFighting: Boolean) {
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

        if (impulseSpin.isFuzzyZero())
            impulseSpin = 0.0f

        tryUsePrimaryWeapon = false
        tryUseSecondaryWeapon = false

        if (isFighting && !isGrabbed) {
            deltaX += player.inputFrame.deltaX
            deltaY += player.inputFrame.deltaY

            if (!Game.player.isTutorialMode || Game.player.tutorialStage >= TutorialStage.LOOKING.ordinal)
                deltaRotation += player.inputFrame.deltaRotation

            if (!Game.player.isTutorialMode || Game.player.tutorialStage >= TutorialStage.SHOOTING_PRIMARY.ordinal) {
                val usePrimary = player.inputFrame.usePrimary

                if (usePrimary && !triedUsePrimaryWeapon)
                    tryUsePrimaryWeapon = true

                triedUsePrimaryWeapon = usePrimary

                if (firePrimaryCooldown <= 0.0.seconds)
                    primaryWeapon?.let { weapon ->
                        if (usePrimary)
                            fireWeapon(weapon) { setPrimaryCooldown(it) }
                        else
                            stopFiringWeapon(weapon) { setPrimaryCooldown(it) }
                    }
            }

            if (!Game.player.isTutorialMode || Game.player.tutorialStage >= TutorialStage.SHOOTING_SECONDARY.ordinal) {
                val useSecondary = player.inputFrame.useSecondary

                if (useSecondary && !triedUseSecondaryWeapon)
                    tryUseSecondaryWeapon = true

                triedUseSecondaryWeapon = useSecondary

                if (fireSecondaryCooldown <= 0.0.seconds) {
                    secondaryWeapon?.let { weapon ->
                        if (useSecondary)
                            fireWeapon(weapon) { setSecondaryCooldown(it) }
                        else
                            stopFiringWeapon(weapon) { setSecondaryCooldown(it) }
                    }
                }
            }
        }

        val speedScaleX = Game.physics.getSpeedScaleX(collider, deltaX)
        val speedScaleY = Game.physics.getSpeedScaleY(collider, deltaY)

        val minSpeedScale = min(speedScaleX, speedScaleY)
        player.indicatorColor.set(Constants.INDICATOR_COLOR_BORDER)
        player.indicatorColor.a = 1.0f - minSpeedScale

        deltaX *= speedScaleX
        deltaY *= speedScaleY

        val moveX = deltaX * movementSpeed * Constants.PLAYER_SHIP_BASE_MOVEMENT_SPEED
        val moveY = deltaY * movementSpeed * Constants.PLAYER_SHIP_BASE_MOVEMENT_SPEED
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

        if (isFighting)
            player.ship.checkCollision()
    }

    fun checkCollision() {
        if (isInvulnerable)
            return

        Game.physics.checkCollision(collider, { it != collider }) {
            if (it.userData is PlayerDamageSource && it.userData.canDamage) {
                onDamageHit()

                if (!Game.player.isTutorialMode)
                    onDamageHit()

                val impulseX = (x - it.userData.damageSourceX) * 0.05f
                val impulseY = (y - it.userData.damageSourceY) * 0.05f

                onImpulseHit(impulseX, impulseY, 20.0f)
            }
        }
    }

    override fun render(renderer: Renderer) {
        val baseTexture = when (flySpeed) {
            in 0.0f..1.0f -> Game.textures.playerShipBaseStill
            in 1.0f..3.0f -> Game.textures.playerShipBaseSlow
            else -> Game.textures.playerShipBaseFast
        }

        darkColor.set(player.color.lighten(-0.15f))
        mainColor.set(player.color)
        lightColor.set(player.color.lighten(0.15f))

        darkColor.mul(color)
        mainColor.mul(color)
        lightColor.mul(color)

        renderer.submit(RenderLayers.PLAYER) {
            it.draw(baseTexture, x, y, Constants.PLAYER_SHIP_WIDTH * 0.5f, Constants.PLAYER_SHIP_HEIGHT * 0.5f, Constants.PLAYER_SHIP_WIDTH, Constants.PLAYER_SHIP_HEIGHT, scale, scale, rotation, color)
        }

        renderer.submit(RenderLayers.PLAYER + 1) {
            it.draw(Game.textures.playerShipTemplate, x, y, Constants.PLAYER_SHIP_WIDTH * 0.5f, Constants.PLAYER_SHIP_HEIGHT * 0.5f, Constants.PLAYER_SHIP_WIDTH, Constants.PLAYER_SHIP_HEIGHT, scale, scale, rotation, lightColor)
        }

        renderer.submit(RenderLayers.PLAYER + 2) {
            it.draw(Game.textures.playerShipTemplate, x, y, Constants.PLAYER_SHIP_WIDTH * 0.5f, Constants.PLAYER_SHIP_HEIGHT * 0.5f, Constants.PLAYER_SHIP_WIDTH, Constants.PLAYER_SHIP_HEIGHT, scale, scale, rotation, mainColor)
        }

        renderer.submit(RenderLayers.PLAYER + 3) {
            it.draw(Game.textures.playerShipTemplate, x, y, Constants.PLAYER_SHIP_WIDTH * 0.5f, Constants.PLAYER_SHIP_HEIGHT * 0.5f, Constants.PLAYER_SHIP_WIDTH, Constants.PLAYER_SHIP_HEIGHT, scale, scale, rotation, darkColor)
        }
    }

    fun setPrimaryCooldown(cooldown: Duration) {
        firePrimaryCooldownTime = cooldown
        firePrimaryCooldown = cooldown
    }

    fun setSecondaryCooldown(cooldown: Duration) {
        fireSecondaryCooldownTime = cooldown
        fireSecondaryCooldown = cooldown
    }

    fun setInvulnerabilityTime(time: Duration) {
        invulnerabilityTime = time
        invulnerabilityTimer = time
    }

    private fun fireWeapon(weapon: Weapon, setCooldown: (Duration) -> Unit): Boolean {
        if (isInvulnerable)
            return false

        if (isBeamProjectileFiring)
            return false

        when (weapon.projectileType.baseType) {
            is BulletProjectileType -> {
                Game.audio.shootSound.play(0.5f)
                setCooldown(weapon.fireRate)
            }

            is BeamProjectileType -> {
                Game.audio.beamSound.play(0.8f)
                isBeamProjectileFiring = true
                setCooldown(0.0.seconds)
            }
        }

        repeat(weapon.projectileCount) {
            var direction = if (weapon.isRandomSpread)
                weapon.spread * (Game.random.nextFloat() * 2.0f - 1.0f)
            else
                weapon.spread * (it - weapon.projectileCount * 0.5f) / weapon.projectileCount

            if (weapon.projectileType.baseType is BulletProjectileType)
                direction += rotation

            Game.projectiles.spawnProjectile(this, weapon.projectileType, muzzleX, muzzleY, direction, weapon.projectileSpeed, weapon.projectileSpeedFalloff)
        }

        return true
    }

    private fun stopFiringWeapon(weapon: Weapon, setCooldown: (Duration) -> Unit) {
        if (weapon.projectileType.baseType is BeamProjectileType && isBeamProjectileFiring) {
            Game.projectiles.stopBeamProjectile(this)

            setCooldown(weapon.fireRate)

            isBeamProjectileFiring = false
            Game.audio.beamSound.stop()
        }
    }

    override fun onDamageHit() {
        if (isInvulnerable || isDead)
            return

        Game.audio.hitShipSound.play(0.5f)

        health--
        if (health <= 0) {
            health = 0
            onDeath()
        }

        addEntityAnimation(HitAnimation(PLAYER_SHIP_INVULNERABILITY_TIME))
        setInvulnerabilityTime(PLAYER_SHIP_INVULNERABILITY_TIME)
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

        Game.particles.add(leftTrailEffect)
        Game.particles.add(rightTrailEffect)
    }

    fun removeFromWorld() {
        Game.world.remove(this)

        Game.particles.remove(leftTrailEffect)
        Game.particles.remove(rightTrailEffect)
    }

    fun addToPhysics() {
        Game.physics.addCollider(collider)
        Game.physics.addHittable(this)
    }

    fun removeFromPhysics() {
        Game.physics.removeCollider(collider)
        Game.physics.removeHittable(this)
    }

    fun initialize(difficulty: Difficulty, spawnX: Float, spawnY: Float, spawnRotation: Angle, isFinalBattle: Boolean) {
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

        isStunMode = !isFinalBattle
    }
}