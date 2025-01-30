package com.cozmicgames.bosses.boss2

import com.cozmicgames.Constants
import com.cozmicgames.Game
import com.cozmicgames.bosses.Boss
import com.cozmicgames.entities.worldObjects.PlayerShip
import com.cozmicgames.entities.worldObjects.ProjectileSource
import com.cozmicgames.entities.worldObjects.animations.HitAnimation
import com.cozmicgames.entities.worldObjects.animations.ParalyzeAnimation
import com.cozmicgames.entities.worldObjects.animations.WorldObjectAnimation
import com.cozmicgames.graphics.RenderLayers
import com.cozmicgames.graphics.particles.effects.DeathSplatterEffect
import com.cozmicgames.physics.RectangleCollisionShape
import com.cozmicgames.utils.Difficulty
import com.littlekt.graphics.g2d.shape.ShapeRenderer
import com.littlekt.graphics.slice
import com.littlekt.math.geom.cosine
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.sine
import com.littlekt.math.isFuzzyZero
import com.littlekt.util.seconds
import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class Boss2(override val difficulty: Difficulty, val isFinalBattle: Boolean = false) : Boss, ProjectileSource {
    companion object {
        const val FULL_HEALTH = 4

        private val INVULNERABLE_TIME = 2.0.seconds
        private val PARALYZED_TIME = 5.0.seconds

        private const val HEAD_SCALE = 2.0f
        private const val SWORD_SCALE = 2.0f
        private const val SHIELD_SCALE = 4.0f
        private const val BODY_SCALE = 2.0f
        private const val FIN_SCALE = 2.0f
        private const val TAIL_SCALE = 2.0f

        private const val HEAD_LAYER = RenderLayers.BOSS2 + 20
        private const val SWORD_LAYER = RenderLayers.BOSS2 + 9
        private const val SHIELD_LAYER = RenderLayers.BOSS2 + 7
        private const val HEART_LAYER = RenderLayers.BOSS2 + 5
        private const val BODY_LAYER = RenderLayers.BOSS2 + 10
        private const val DORSAL_FIN_LAYER = RenderLayers.BOSS2 + 5
        private const val SIDE_FIN_LAYER = RenderLayers.BOSS2 + 25
        private const val TAIL_LAYER = RenderLayers.BOSS2 + 5

        private const val DORSAL_FIN_BODY_PART_INDEX = (Constants.BOSS2_BODY_PARTS * 0.3f).toInt()
        private const val SIDE_FIN_BODY_PART_INDEX = (Constants.BOSS2_BODY_PARTS * 0.15f).toInt()
        private const val TAIL_BODY_PART_INDEX = Constants.BOSS2_BODY_PARTS - 1
    }

    override val fullHealth = FULL_HEALTH

    override val projectileSourceId = "boss2"
    override val muzzleX: Float
        get() {
            val xOffset = if (isFlipped) -sword.width * 0.4f else sword.width * 0.4f
            val yOffset = 0.0f
            val cos = rotation.cosine
            val sin = rotation.sine
            return sword.x + cos * xOffset - sin * yOffset
        }

    override val muzzleY: Float
        get() {
            val xOffset = if (isFlipped) -sword.width * 0.4f else sword.width * 0.4f
            val yOffset = 0.0f
            val cos = rotation.cosine
            val sin = rotation.sine
            return sword.y + sin * xOffset + cos * yOffset
        }
    override val muzzleRotation get() = if (isFlipped) rotation else rotation + 180.0.degrees

    override val isStunMode = false

    override var health = FULL_HEALTH
    override var x = 0.0f
    override var y = 0.0f
    override var rotation = 0.0.degrees

    var impulseX = 0.0f
    var impulseY = 0.0f
    var impulseSpin = 0.0f

    val isInvulnerable get() = isInvulnerableTimer > 0.0.seconds

    override val isParalyzed get() = isParalyzedTimer > 0.0.seconds

    var isFlipped = false
        private set

    private val head = Head(this, HEAD_SCALE, HEAD_LAYER)
    private val sword = Sword(this, SWORD_SCALE, SWORD_LAYER)
    private val shield = Shield(this, SHIELD_SCALE, SHIELD_LAYER)
    private val heart = Heart(this, HEART_LAYER)
    private val body = Body(this, BODY_SCALE, BODY_LAYER)
    private val dorsalFin = Fin(this, false, FIN_SCALE, DORSAL_FIN_LAYER)
    private val sideFin = Fin(this, true, FIN_SCALE, SIDE_FIN_LAYER)
    private val tail = Tail(this, TAIL_SCALE, TAIL_LAYER)
    private var isInvulnerableTimer = 0.0.seconds
    private var isParalyzedTimer = 0.0.seconds

    override val movementController = Boss2MovementController(this)

    fun flip() {
        isFlipped = !isFlipped
    }

    override fun addToWorld() {
        Game.world.add(head)
        Game.world.add(sword)
        Game.world.add(shield)
        Game.world.add(heart)
        body.parts.forEach(Game.world::add)
        Game.world.add(dorsalFin)
        Game.world.add(sideFin)
        Game.world.add(tail)
    }

    override fun removeFromWorld() {
        Game.world.remove(head)
        Game.world.remove(sword)
        Game.world.remove(shield)
        Game.world.remove(heart)
        body.parts.forEach(Game.world::remove)
        Game.world.remove(dorsalFin)
        Game.world.remove(sideFin)
        Game.world.remove(tail)
    }

    override fun addToPhysics() {
        Game.physics.addCollider(head.collider)
        Game.physics.addHittable(head)

        Game.physics.addCollider(sword.collider)
        Game.physics.addCollider(shield.collider)

        Game.physics.addCollider(heart.collider)
        Game.physics.addHittable(heart)

        body.parts.forEach {
            it.collider?.apply(Game.physics::addCollider)
        }

        Game.physics.addHittable(body)

        Game.physics.addCollider(dorsalFin.collider)
        Game.physics.addCollider(sideFin.collider)
        Game.physics.addCollider(tail.collider)
    }

    override fun removeFromPhysics() {
        Game.physics.removeCollider(head.collider)
        Game.physics.removeHittable(head)

        Game.physics.removeCollider(sword.collider)
        Game.physics.removeCollider(shield.collider)

        Game.physics.removeCollider(heart.collider)
        Game.physics.removeHittable(heart)

        body.parts.forEach {
            it.collider?.apply(Game.physics::removeCollider)
        }

        Game.physics.removeHittable(body)

        Game.physics.removeCollider(dorsalFin.collider)
        Game.physics.removeCollider(sideFin.collider)
        Game.physics.removeCollider(tail.collider)
    }

    override fun update(delta: Duration, fightStarted: Boolean) {
        isInvulnerableTimer -= delta
        if (isInvulnerableTimer <= 0.0.seconds)
            isInvulnerableTimer = 0.0.seconds

        isParalyzedTimer -= delta
        if (isParalyzedTimer <= 0.0.seconds)
            isParalyzedTimer = 0.0.seconds

        impulseX *= 1.0f - delta.seconds
        impulseY *= 1.0f - delta.seconds
        impulseSpin *= 1.0f - delta.seconds * 1.05f

        if (impulseX.isFuzzyZero())
            impulseX = 0.0f

        if (impulseY.isFuzzyZero())
            impulseY = 0.0f

        if (impulseSpin.isFuzzyZero())
            impulseSpin = 0.0f

        x += impulseX * delta.seconds * 300.0f
        y += impulseY * delta.seconds * 300.0f
        rotation += impulseSpin.degrees * delta.seconds * 200.0f

        val playerIsInView = if (isFlipped)
            Game.player.ship.x > x
        else
            Game.player.ship.x < x

        if (!isParalyzed && !movementController.isAttacking && !playerIsInView)
            flip()

        if (fightStarted) {
            movementController.update(delta)
            body.update(delta, movementController.movement.bodyMovement)
            shield.update(delta, movementController.movement.shieldMovement)
        }

        body.x = x
        body.y = y
        body.rotation = rotation

        val cos = rotation.cosine
        val sin = rotation.sine

        val headOffsetX = if (isFlipped) head.width * 0.5f else -head.width * 0.5f

        head.x = x + cos * headOffsetX
        head.y = y + sin * headOffsetX
        head.rotation = rotation
        head.collider.update(head.x, head.y)

        val swordOffsetX = if (isFlipped) head.width * 0.78f + sword.width * 0.5f else -(head.width * 0.78f + sword.width * 0.5f)

        sword.x = x + cos * swordOffsetX
        sword.y = y + sin * swordOffsetX
        sword.rotation = rotation
        (sword.collider.shape as RectangleCollisionShape).angle = rotation
        sword.collider.update(sword.x, sword.y)

        val shieldOffsetX = if (isFlipped) head.width * 0.25f else -head.width * 0.25f
        val shieldOffsetY = -head.height * 0.35f

        shield.x = x + cos * shieldOffsetX - sin * shieldOffsetY
        shield.y = y + sin * shieldOffsetX + cos * shieldOffsetY
        shield.rotation = rotation
        shield.collider.update(shield.x, shield.y)

        val heartOffsetX = if (isFlipped) head.width * 0.25f else -head.width * 0.25f
        val heartOffsetY = -head.height * 0.35f

        heart.x = x + cos * heartOffsetX - sin * heartOffsetY
        heart.y = y + sin * heartOffsetX + cos * heartOffsetY
        heart.rotation = rotation

        val dorsalFinBodyPart = body.parts[DORSAL_FIN_BODY_PART_INDEX]
        val dorsalFinOffsetX = dorsalFinBodyPart.width * 0.1f
        val dorsalFinOffsetY = dorsalFinBodyPart.height * 0.4f
        val dorsalFinAngle = dorsalFinBodyPart.rotation

        val dorsalFinCos = dorsalFinAngle.cosine
        val dorsalFinSin = dorsalFinAngle.sine

        dorsalFin.x = dorsalFinBodyPart.x + dorsalFinCos * dorsalFinOffsetX - dorsalFinSin * dorsalFinOffsetY
        dorsalFin.y = dorsalFinBodyPart.y + dorsalFinSin * dorsalFinOffsetX + dorsalFinCos * dorsalFinOffsetY
        dorsalFin.rotation = dorsalFinAngle - (if (isFlipped) -(10.0).degrees else 10.0.degrees)

        val sideFinBodyPart = body.parts[SIDE_FIN_BODY_PART_INDEX]
        val sideFinOffsetX = sideFinBodyPart.width * -0.1f
        val sideFinOffsetY = -sideFinBodyPart.height * 0.4f
        val sideFinAngle = sideFinBodyPart.rotation

        val sideFinCos = sideFinAngle.cosine
        val sideFinSin = sideFinAngle.sine

        sideFin.x = sideFinBodyPart.x + sideFinCos * sideFinOffsetX - sideFinSin * sideFinOffsetY
        sideFin.y = sideFinBodyPart.y + sideFinSin * sideFinOffsetX + sideFinCos * sideFinOffsetY
        sideFin.rotation = sideFinAngle - (if (isFlipped) -(8.0).degrees else 8.0.degrees)

        val tailBodyPart = body.parts[TAIL_BODY_PART_INDEX]
        val tailOffsetX = if (isFlipped) tailBodyPart.width * 0.4f else -tailBodyPart.width * 0.4f
        val tailAngle = tailBodyPart.rotation

        val tailCos = tailAngle.cosine
        val tailSin = tailAngle.sine

        tail.x = tailBodyPart.x + tailCos * tailOffsetX
        tail.y = tailBodyPart.y + tailSin * tailOffsetX
        tail.rotation = tailAngle
    }

    fun paralyze() {
        if (isInvulnerable)
            return

        addEntityAnimation { ParalyzeAnimation(PARALYZED_TIME, 0.7f) }

        movementController.onParalyze()
        isParalyzedTimer = PARALYZED_TIME
    }

    fun hit() {
        if (isInvulnerable || health <= 0)
            return

        Game.resources.hitEnemySound.play(0.5f)

        cancelEntityAnimation<ParalyzeAnimation>()
        addEntityAnimation { HitAnimation(INVULNERABLE_TIME) }

        health--
        if (health < 0) health = 0

        movementController.onHit()

        if (health <= 0) {
            removeFromPhysics()
            movementController.onDeath()

            if (isFinalBattle) {
                head.texture = Game.resources.boss2headDead.slice()
                sword.texture = Game.resources.boss2swordDead.slice()

                Game.world.remove(heart)
                Game.particles.add(DeathSplatterEffect(heart.x, heart.y, heart.rotation + 90.0.degrees))
            }
        } else {
            isInvulnerableTimer = INVULNERABLE_TIME
            isParalyzedTimer = 0.0.seconds
        }
    }

    fun addEntityAnimation(block: () -> WorldObjectAnimation) {
        head.addEntityAnimation(block())
        sword.addEntityAnimation(block())
        shield.addEntityAnimation(block())
        body.parts.forEach { it.addEntityAnimation(block()) }
        dorsalFin.addEntityAnimation(block())
        sideFin.addEntityAnimation(block())
        tail.addEntityAnimation(block())
    }

    inline fun <reified T : WorldObjectAnimation> cancelEntityAnimation() {
        cancelEntityAnimation(T::class)
    }

    fun <T : WorldObjectAnimation> cancelEntityAnimation(type: KClass<T>) {
        head.cancelEntityAnimation(type)
        sword.cancelEntityAnimation(type)
        shield.cancelEntityAnimation(type)
        body.parts.forEach { it.cancelEntityAnimation(type) }
        dorsalFin.cancelEntityAnimation(type)
        sideFin.cancelEntityAnimation(type)
        tail.cancelEntityAnimation(type)
    }

    override fun drawDebug(renderer: ShapeRenderer) {
        head.collider.drawDebug(renderer)
        sword.collider.drawDebug(renderer)
        shield.collider.drawDebug(renderer)
        heart.collider.drawDebug(renderer)
        body.parts.forEach { it.collider?.drawDebug(renderer) }
        dorsalFin.collider.drawDebug(renderer)
        tail.collider.drawDebug(renderer)
    }
}