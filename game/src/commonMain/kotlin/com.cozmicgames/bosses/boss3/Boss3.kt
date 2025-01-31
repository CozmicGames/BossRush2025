package com.cozmicgames.bosses.boss3

import com.cozmicgames.Game
import com.cozmicgames.bosses.Boss
import com.cozmicgames.entities.worldObjects.AreaEffectSource
import com.cozmicgames.entities.worldObjects.ProjectileSource
import com.cozmicgames.entities.worldObjects.animations.DeadAnimation
import com.cozmicgames.entities.worldObjects.animations.HitAnimation
import com.cozmicgames.entities.worldObjects.animations.ParalyzeAnimation
import com.cozmicgames.entities.worldObjects.animations.WorldObjectAnimation
import com.cozmicgames.graphics.RenderLayers
import com.cozmicgames.graphics.particles.effects.DeathSplatterEffect
import com.cozmicgames.utils.Difficulty
import com.littlekt.graphics.g2d.shape.ShapeRenderer
import com.littlekt.graphics.slice
import com.littlekt.input.Key
import com.littlekt.math.geom.cosine
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.sine
import com.littlekt.math.isFuzzyZero
import com.littlekt.util.seconds
import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class Boss3(override val difficulty: Difficulty, val isFinalBattle: Boolean = false) : Boss, ProjectileSource, AreaEffectSource {
    companion object {
        const val FULL_HEALTH = 3

        private val INVULNERABLE_TIME = 2.0.seconds
        private val PARALYZED_TIME = 5.0.seconds

        private val LEG_OFFSETS = arrayOf(
            0.54f to -0.45f,
            0.51f to -0.56f,
            0.45f to -0.7f,
            -0.54f to -0.45f,
            -0.51f to -0.56f,
            -0.45f to -0.7f,
        )

        private val LEG_ANGLES = arrayOf(
            (10.0).degrees,
            (-5.0).degrees,
            (-20.0).degrees,
            -(10.0).degrees,
            5.0.degrees,
            20.0.degrees,
        )

        private val LEG_SCALES = arrayOf(
            2.0f,
            2.3f,
            2.7f,
            2.0f,
            2.3f,
            2.7f,
        )

        private val LEG_LAYERS = arrayOf(
            RenderLayers.BOSS3 + 10,
            RenderLayers.BOSS3 + 20,
            RenderLayers.BOSS3 + 30,
            RenderLayers.BOSS3 + 10,
            RenderLayers.BOSS3 + 20,
            RenderLayers.BOSS3 + 30,
        )

        private val ARM_OFFSETS = arrayOf(
            0.6f to -0.37f,
            -0.6f to -0.37f,
        )

        private val ARM_ANGLES = arrayOf(
            (-20.0).degrees,
            20.0.degrees,
        )

        private val ARM_SCALES = arrayOf(
            2.5f,
            2.5f,
        )

        private val ARM_LAYERS = arrayOf(
            RenderLayers.BOSS3 + 50,
            RenderLayers.BOSS3 + 50,
        )

        private const val HEAD_SCALE = 3.0f
        private const val HEAD_LAYER = RenderLayers.BOSS3 + 40

        private const val HEART_SCALE = 1.8f
        private const val HEART_LAYER = RenderLayers.BOSS3 + 35

        private const val BEAK_SCALE = 1.8f
        private const val BEAK_LAYER = RenderLayers.BOSS3 + 38
    }

    override val fullHealth = FULL_HEALTH

    override var health = FULL_HEALTH
    override var x = 0.0f
    override var y = 0.0f
    override var rotation = 0.0.degrees

    var impulseX = 0.0f
    var impulseY = 0.0f
    var impulseSpin = 0.0f

    override val muzzleX = 0.0f
    override val muzzleY = 0.0f
    override val muzzleRotation = 0.0.degrees
    override val projectileSourceId = "boss3"
    override val isStunMode = false

    override val effectSourceX get() = beak.x
    override val effectSourceY get() = beak.y

    val isInvulnerable get() = isInvulnerableTimer > 0.0.seconds

    override val isParalyzed get() = isParalyzedTimer > 0.0.seconds

    private var isInvulnerableTimer = 0.0.seconds
    private var isParalyzedTimer = 0.0.seconds

    override val movementController = Boss3MovementController(this)

    val head = Head(this, HEAD_SCALE, HEAD_LAYER)
    val legs: List<Leg>
    val arms: List<Arm>
    val heart = Heart(this, HEART_SCALE, HEART_LAYER)
    val beak = Beak(this, BEAK_SCALE, BEAK_LAYER)

    init {
        val legs = arrayListOf<Leg>()

        repeat(6) {
            legs += Leg(this, it, it >= 3, LEG_LAYERS[it], LEG_ANGLES[it], LEG_SCALES[it])
        }

        this.legs = legs

        val arms = arrayListOf<Arm>()

        arms += Arm(this, 0, false, ARM_LAYERS[0], ARM_ANGLES[0], ARM_SCALES[0])
        arms += Arm(this, 1, true, ARM_LAYERS[1], ARM_ANGLES[1], ARM_SCALES[1])

        this.arms = arms
    }

    override fun addToWorld() {
        Game.world.add(head)
        legs.forEach { it.parts.forEach(Game.world::add) }
        arms.forEach {
            it.parts.forEach(Game.world::add)
            (it.parts.last() as? Claw)?.let { claw ->
                Game.world.add(claw.upperClawPart)
                Game.world.add(claw.lowerClawPart)
            }
        }
        Game.world.add(heart)
        Game.world.add(beak.leftBeak)
        Game.world.add(beak.rightBeak)
    }

    override fun removeFromWorld() {
        Game.world.remove(head)
        legs.forEach { it.parts.forEach(Game.world::remove) }
        arms.forEach {
            it.parts.forEach(Game.world::remove)
            (it.parts.last() as? Claw)?.let { claw ->
                Game.world.remove(claw.upperClawPart)
                Game.world.remove(claw.lowerClawPart)
            }
        }
        Game.world.remove(heart)
        Game.world.remove(beak.leftBeak)
        Game.world.remove(beak.rightBeak)
    }

    override fun addToPhysics() {
        Game.physics.addCollider(head.collider)
        Game.physics.addCollider(head.blockingCollider)
        Game.physics.addHittable(head)

        Game.physics.addCollider(beak.leftBeak.collider)
        Game.physics.addCollider(beak.rightBeak.collider)

        Game.physics.addCollider(heart.collider)
        Game.physics.addHittable(heart)

        legs.forEach {
            it.parts.forEach { part ->
                Game.physics.addCollider(part.collider)
            }

            Game.physics.addHittable(it)
        }

        arms.forEach {
            it.parts.forEach { part ->
                Game.physics.addCollider(part.collider)
            }

            Game.physics.addHittable(it)
            Game.physics.addGrabbingObject(it.claw)
        }
    }

    override fun removeFromPhysics() {
        Game.physics.removeCollider(head.collider)
        Game.physics.removeCollider(head.blockingCollider)
        Game.physics.removeHittable(head)

        Game.physics.removeCollider(beak.leftBeak.collider)
        Game.physics.removeCollider(beak.rightBeak.collider)

        Game.physics.removeCollider(heart.collider)
        Game.physics.removeHittable(heart)

        legs.forEach {
            it.parts.forEach { part ->
                Game.physics.removeCollider(part.collider)
            }

            Game.physics.removeHittable(it)
        }

        arms.forEach {
            it.parts.forEach { part ->
                Game.physics.removeCollider(part.collider)
            }

            Game.physics.removeHittable(it)
            Game.physics.removeGrabbingObject(it.claw)
        }
    }

    override fun update(delta: Duration, isFighting: Boolean) {
        if (Game.input.isKeyJustPressed(Key.H))
            movementController.performAttack(GrabAttack())

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

        movementController.update(delta, isFighting)
        beak.update(delta, movementController.movement.beakMovement)
        legs.forEach {
            it.update(delta, movementController.movement.legMovement)
        }
        arms.forEach {
            it.update(delta, movementController.movement.armMovement)
        }

        val cos = rotation.cosine
        val sin = rotation.sine

        head.x = x
        head.y = y
        head.rotation = rotation
        head.collider.update(head.x, head.y)

        legs.forEachIndexed { index, leg ->
            val (offsetX, offsetY) = LEG_OFFSETS[index]

            val tentacleOffsetX = offsetX * head.width * 0.5f
            val tentacleOffsetY = offsetY * head.height * 0.5f

            leg.x = x + cos * tentacleOffsetX - sin * tentacleOffsetY
            leg.y = y + sin * tentacleOffsetX + cos * tentacleOffsetY
            leg.rotation = rotation
        }

        arms.forEachIndexed { index, arm ->
            val (offsetX, offsetY) = ARM_OFFSETS[index]

            val armOffsetX = offsetX * head.width * 0.5f
            val armOffsetY = offsetY * head.height * 0.5f

            arm.x = x + cos * armOffsetX - sin * armOffsetY
            arm.y = y + sin * armOffsetX + cos * armOffsetY
            arm.rotation = rotation
        }

        val beakOffsetX = 0.0f
        val beakOffsetY = -0.4f * head.height

        beak.x = x + cos * beakOffsetX - sin * beakOffsetY
        beak.y = y + sin * beakOffsetX + cos * beakOffsetY
        beak.rotation = rotation

        val heartOffsetX = 0.0f
        val heartOffsetY = -0.4f * head.height

        heart.x = x + cos * heartOffsetX - sin * heartOffsetY
        heart.y = y + sin * heartOffsetX + cos * heartOffsetY
        heart.rotation = rotation
    }

    fun paralyze() {
        if (isInvulnerable)
            return

        addEntityAnimation { ParalyzeAnimation(PARALYZED_TIME, 0.7f) }

        legs.forEach {
            it.paralyze(PARALYZED_TIME, false)
        }

        arms.forEach {
            it.paralyze(PARALYZED_TIME, false)
        }

        movementController.onParalyze()
        isParalyzedTimer = PARALYZED_TIME
    }

    fun hit() {
        if (isInvulnerable || health <= 0)
            return

        Game.audio.hitEnemySound.play(0.5f)

        cancelEntityAnimation<ParalyzeAnimation>()
        addEntityAnimation { HitAnimation(INVULNERABLE_TIME) }

        health--
        if (health < 0) health = 0

        movementController.onHit()

        if (health <= 0) {
            removeFromPhysics()

            if (isFinalBattle) {
                head.texture = Game.textures.boss3headDead.slice()
                arms.forEach {
                    it.claw.lowerClawPart.texture = Game.textures.boss3clawLowerDead.slice()
                }

                Game.world.remove(heart)
                Game.particles.add(DeathSplatterEffect(heart.x, heart.y, heart.rotation - 90.0.degrees))
                addEntityAnimation { DeadAnimation() }
            }
        } else {
            isInvulnerableTimer = INVULNERABLE_TIME
            isParalyzedTimer = 0.0.seconds
        }

        legs.forEach {
            it.unparalyze()
        }

        arms.forEach {
            it.unparalyze()
        }
    }

    override fun shouldHitWithAreaEffect(id: String): Boolean {
        return !id.startsWith("boss3")
    }

    fun addEntityAnimation(block: () -> WorldObjectAnimation) {
        head.addEntityAnimation(block())

        legs.forEach {
            it.parts.forEach { part ->
                part.addEntityAnimation(block())
            }
        }

        arms.forEach {
            it.parts.forEach { part ->
                part.addEntityAnimation(block())
            }

            it.claw.addEntityAnimation(block())
            it.claw.upperClawPart.addEntityAnimation(block())
            it.claw.lowerClawPart.addEntityAnimation(block())
        }
    }

    inline fun <reified T : WorldObjectAnimation> cancelEntityAnimation() {
        cancelEntityAnimation(T::class)
    }

    fun <T : WorldObjectAnimation> cancelEntityAnimation(type: KClass<T>) {
        head.cancelEntityAnimation(type)

        legs.forEach {
            it.parts.forEach { part ->
                part.cancelEntityAnimation(type)
            }
        }

        arms.forEach {
            it.parts.forEach { part ->
                part.cancelEntityAnimation(type)
            }

            it.claw.cancelEntityAnimation(type)
        }
    }

    override fun drawDebug(renderer: ShapeRenderer) {
        head.collider.drawDebug(renderer)
        head.blockingCollider.drawDebug(renderer)

        beak.leftBeak.collider.drawDebug(renderer)
        beak.rightBeak.collider.drawDebug(renderer)

        heart.collider.drawDebug(renderer)

        legs.forEach {
            it.parts.forEach { part ->
                part.collider.drawDebug(renderer)
            }
        }

        arms.forEach {
            it.parts.forEach { part ->
                part.collider.drawDebug(renderer)
            }

            it.claw.grabCollider.drawDebug(renderer)
        }
    }
}