package com.cozmicgames.bosses.boss4

import com.cozmicgames.Game
import com.cozmicgames.bosses.Boss
import com.cozmicgames.entities.Entity
import com.cozmicgames.entities.worldObjects.AreaEffectSource
import com.cozmicgames.entities.worldObjects.animations.DeadAnimation
import com.cozmicgames.entities.worldObjects.animations.WorldObjectAnimation
import com.cozmicgames.entities.worldObjects.animations.HitAnimation
import com.cozmicgames.entities.worldObjects.animations.ParalyzeAnimation
import com.cozmicgames.graphics.RenderLayers
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.particles.effects.DeathSplatterEffect
import com.cozmicgames.utils.Difficulty
import com.littlekt.graphics.Color
import com.littlekt.graphics.MutableColor
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

class Boss4(override val difficulty: Difficulty, val isFinalBattle: Boolean = false) : Entity("boss1"), AreaEffectSource, Boss {
    companion object {
        const val FULL_HEALTH = 3

        private val INVULNERABLE_TIME = 2.0.seconds
        private val PARALYZED_TIME = 5.0.seconds
        private val CAMOUFLAGE_TIME = 2.0.seconds

        private const val HEAD_SCALE = 3.0f
        private const val HEAD_LAYER = RenderLayers.BOSS4 + 10

        private const val BODY_SCALE = 3.0f
        private const val BODY_LAYER = RenderLayers.BOSS4 + 10

        private const val EYES_SCALE = 3.0f
        private const val EYES_LAYER = RenderLayers.BOSS4 + 10

        private const val WING_SCALE = 3.0f
        private const val WING_LAYER = RenderLayers.BOSS4 + 10

        private const val TAIL_SCALE = 3.0f
        private const val TAIL_LAYER = RenderLayers.BOSS4 + 10

        private const val BEAK_SCALE = 2.0f
        private const val BEAK_LAYER = RenderLayers.BOSS4 + 8

        private const val HEART_SCALE = 2.0f
        private const val HEART_LAYER = RenderLayers.BOSS4 + 5
    }

    override val fullHealth = FULL_HEALTH

    override var health = FULL_HEALTH

    override val effectSourceX get() = beak.x
    override val effectSourceY get() = beak.y

    override var x = 0.0f
    override var y = 0.0f
    override var rotation = 0.0.degrees

    var impulseX = 0.0f
    var impulseY = 0.0f
    var impulseSpin = 0.0f

    override val movementController = Boss4MovementController(this)

    val isInvulnerable get() = isInvulnerableTimer > 0.0.seconds || isTeleporting

    override val isParalyzed get() = isParalyzedTimer > 0.0.seconds

    val camouflageColor = MutableColor(Color.WHITE)
    val eyesCamouflageColor = MutableColor(Color.WHITE)
    var camouflageFactor = 0.0f

    val isCamouflaged get() = camouflageFactor > 0.0f

    val isVortexOpen get() = vortex.size > 0.0f

    var bossScale = 1.0f
    var isTeleporting = false
    val vortex = Vortex()

    private val head = Head(this, HEAD_SCALE, HEAD_LAYER)
    private val eyes = Eyes(this, EYES_SCALE, EYES_LAYER)
    private val body = Body(this, BODY_SCALE, BODY_LAYER)
    private val tail = Tail(this, TAIL_SCALE, TAIL_LAYER)
    private val leftWing = Wing(this, true, WING_SCALE, WING_LAYER)
    private val rightWing = Wing(this, false, WING_SCALE, WING_LAYER)
    private val beak = Beak(this, BEAK_SCALE, BEAK_LAYER)
    private val heart = Heart(this, HEART_SCALE, HEART_LAYER)
    private var isInvulnerableTimer = 0.0.seconds
    private var isParalyzedTimer = 0.0.seconds
    private var camouflageTimer = 0.0.seconds
    private var camouflageDirection = -1.0f
    private var vortexTimer = 0.0.seconds
    private var vortexDirection = -1.0f
    private var vortexOpenCloseTime = 0.0.seconds
    private var vortexTargetSize = 0.0f
    private var vortexCallback: () -> Unit = {}

    override fun addToWorld() {
        Game.world.add(head)
        Game.world.add(eyes)
        Game.world.add(body)
        tail.parts.forEach {
            Game.world.add(it)
        }
        Game.world.add(leftWing)
        Game.world.add(rightWing)

        Game.world.add(beak.leftBeak)
        Game.world.add(beak.rightBeak)
        Game.world.add(heart)
    }

    override fun removeFromWorld() {
        Game.world.remove(head)
        Game.world.remove(eyes)
        Game.world.remove(body)
        tail.parts.forEach {
            Game.world.remove(it)
        }
        Game.world.remove(leftWing)
        Game.world.remove(rightWing)

        Game.world.remove(beak.leftBeak)
        Game.world.remove(beak.rightBeak)
        Game.world.remove(heart)
    }

    override fun addToPhysics() {
        Game.physics.addCollider(head.collider)
        Game.physics.addHittable(head)

        Game.physics.addCollider(body.collider)
        Game.physics.addCollider(body.centerCollider)
        Game.physics.addHittable(body)

        tail.parts.forEach {
            Game.physics.addCollider(it.collider)
        }
        Game.physics.addHittable(tail)

        Game.physics.addCollider(leftWing.collider)
        Game.physics.addCollider(leftWing.lowerCollider)
        Game.physics.addHittable(leftWing)

        Game.physics.addCollider(rightWing.collider)
        Game.physics.addCollider(rightWing.lowerCollider)
        Game.physics.addHittable(rightWing)

        Game.physics.addCollider(heart.collider)
        Game.physics.addHittable(heart)
    }

    override fun removeFromPhysics() {
        Game.physics.removeCollider(head.collider)
        Game.physics.removeHittable(head)

        Game.physics.removeCollider(body.collider)
        Game.physics.removeCollider(body.centerCollider)
        Game.physics.removeHittable(body)

        tail.parts.forEach {
            Game.physics.removeCollider(it.collider)
        }
        Game.physics.removeHittable(tail)

        Game.physics.removeCollider(leftWing.collider)
        Game.physics.removeCollider(leftWing.lowerCollider)
        Game.physics.removeHittable(leftWing)

        Game.physics.removeCollider(rightWing.collider)
        Game.physics.removeCollider(rightWing.lowerCollider)
        Game.physics.removeHittable(rightWing)

        Game.physics.removeCollider(heart.collider)
        Game.physics.removeHittable(heart)
    }

    override fun update(delta: Duration, isFighting: Boolean) {
        isInvulnerableTimer -= delta
        if (isInvulnerableTimer <= 0.0.seconds)
            isInvulnerableTimer = 0.0.seconds

        isParalyzedTimer -= delta
        if (isParalyzedTimer <= 0.0.seconds)
            isParalyzedTimer = 0.0.seconds

        camouflageTimer -= delta
        if (camouflageTimer <= 0.0.seconds)
            camouflageTimer = 0.0.seconds

        camouflageFactor += (1.0f - (camouflageTimer / CAMOUFLAGE_TIME).toFloat()) * camouflageDirection

        if (camouflageFactor < 0.0f)
            camouflageFactor = 0.0f
        else if (camouflageFactor > 1.0f)
            camouflageFactor = 1.0f

        vortexTimer -= delta
        if (vortexTimer <= 0.0.seconds) {
            vortexTimer = 0.0.seconds
            vortexCallback()
            vortexCallback = {}
        }

        if (vortexOpenCloseTime > 0.0.seconds)
            vortex.size += (1.0f - (vortexTimer / vortexOpenCloseTime).toFloat()) * vortexDirection * vortexTargetSize

        if (vortex.size < 0.0f)
            vortex.size = 0.0f
        else if (vortex.size > vortexTargetSize)
            vortex.size = vortexTargetSize

        Color.WHITE.mix(Color.CLEAR, camouflageFactor * 0.9f, camouflageColor)
        Color.WHITE.mix(Color.CLEAR, camouflageFactor * 0.5f, eyesCamouflageColor)

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
        tail.update(delta, movementController.movement.tailMovement)

        val cos = rotation.cosine
        val sin = rotation.sine

        head.x = x
        head.y = y
        head.rotation = rotation

        eyes.x = head.x
        eyes.y = head.y
        eyes.rotation = head.rotation

        val bodyPivotOffsetX = 0.0f
        val bodyPivotOffsetY = -head.height * 0.5f

        val bodyPivotX = x + cos * bodyPivotOffsetX - sin * bodyPivotOffsetY
        val bodyPivotY = y + sin * bodyPivotOffsetX + cos * bodyPivotOffsetY

        val bodyOffsetX = 0.0f
        val bodyOffsetY = -body.height * 0.5f

        val bodyCos = body.rotation.cosine
        val bodySin = body.rotation.sine

        body.x = bodyPivotX + bodyCos * bodyOffsetX - bodySin * bodyOffsetY
        body.y = bodyPivotY + bodySin * bodyOffsetX + bodyCos * bodyOffsetY
        body.rotation = rotation

        val tailPivotOffsetX = 0.0f
        val tailPivotOffsetY = -body.height * 0.5f

        val tailPivotX = body.x + bodyCos * tailPivotOffsetX - bodySin * tailPivotOffsetY
        val tailPivotY = body.y + bodySin * tailPivotOffsetX + bodyCos * tailPivotOffsetY

        val tailOffsetX = 0.0f
        val tailOffsetY = 0.0f

        val tailCos = tail.tailAngle.cosine
        val tailSin = tail.tailAngle.sine

        tail.x = tailPivotX + tailCos * tailOffsetX - tailSin * tailOffsetY
        tail.y = tailPivotY + tailSin * tailOffsetX + tailCos * tailOffsetY
        tail.rotation = rotation

        val leftWingPivotOffsetX = -head.width * 0.5f
        val leftWingPivotOffsetY = -head.height * 0.5f

        val leftWingPivotX = x + cos * leftWingPivotOffsetX - sin * leftWingPivotOffsetY
        val leftWingPivotY = y + sin * leftWingPivotOffsetX + cos * leftWingPivotOffsetY

        val leftWingOffsetX = -leftWing.width * 0.5f
        val leftWingOffsetY = 0.0f

        val leftWingCos = leftWing.rotation.cosine
        val leftWingSin = leftWing.rotation.sine

        leftWing.x = leftWingPivotX + leftWingCos * leftWingOffsetX - leftWingSin * leftWingOffsetY
        leftWing.y = leftWingPivotY + leftWingSin * leftWingOffsetX + leftWingCos * leftWingOffsetY
        leftWing.rotation = rotation

        val rightWingPivotOffsetX = head.width * 0.5f
        val rightWingPivotOffsetY = -head.height * 0.5f

        val rightWingPivotX = x + cos * rightWingPivotOffsetX - sin * rightWingPivotOffsetY
        val rightWingPivotY = y + sin * rightWingPivotOffsetX + cos * rightWingPivotOffsetY

        val rightWingOffsetX = rightWing.width * 0.5f
        val rightWingOffsetY = 0.0f

        val rightWingCos = rightWing.rotation.cosine
        val rightWingSin = rightWing.rotation.sine

        rightWing.x = rightWingPivotX + rightWingCos * rightWingOffsetX - rightWingSin * rightWingOffsetY
        rightWing.y = rightWingPivotY + rightWingSin * rightWingOffsetX + rightWingCos * rightWingOffsetY
        rightWing.rotation = rotation

        val beakOffsetX = 0.0f
        val beakOffsetY = head.height * 0.3f

        beak.x = head.x + cos * beakOffsetX - sin * beakOffsetY
        beak.y = head.y + sin * beakOffsetX + cos * beakOffsetY
        beak.rotation = rotation

        val heartOffsetX = 0.0f
        val heartOffsetY = head.height * 0.38f

        heart.x = head.x + cos * heartOffsetX - sin * heartOffsetY
        heart.y = head.y + sin * heartOffsetX + cos * heartOffsetY
        heart.rotation = rotation
    }

    override fun renderSpecials(delta: Duration, renderer: Renderer) {
        vortex.render(delta, renderer)
    }

    override fun paralyze() {
        if (isInvulnerable)
            return

        addEntityAnimation { ParalyzeAnimation(PARALYZED_TIME, 0.7f) }
        decamouflage(true)

        tail.paralyze(PARALYZED_TIME, false)
        movementController.onParalyze()
        isParalyzedTimer = PARALYZED_TIME
    }

    override fun hit() {
        if (isInvulnerable || health <= 0)
            return

        Game.audio.hitEnemySound.play(0.5f)

        cancelEntityAnimation<ParalyzeAnimation>()
        addEntityAnimation { HitAnimation(INVULNERABLE_TIME) }
        decamouflage(true)

        health--
        if (health < 0) health = 0

        movementController.onHit()

        if (health <= 0) {
            removeFromPhysics()
            decamouflage()
            closeVortex(0.5.seconds)

            if (isFinalBattle) {
                eyes.texture = Game.textures.boss4eyesDead.slice()

                Game.world.remove(heart)
                Game.particles.add(DeathSplatterEffect(heart.x, heart.y, heart.rotation + 90.0.degrees))
                addEntityAnimation { DeadAnimation() }
            }
        } else {
            tail.unparalyze()
            isInvulnerableTimer = INVULNERABLE_TIME
            isParalyzedTimer = 0.0.seconds
        }
    }

    fun addEntityAnimation(block: () -> WorldObjectAnimation) {
        head.addEntityAnimation(block())
        body.addEntityAnimation(block())
        tail.parts.forEach {
            it.addEntityAnimation(block())
        }
        leftWing.addEntityAnimation(block())
        rightWing.addEntityAnimation(block())

        beak.leftBeak.addEntityAnimation(block())
        beak.rightBeak.addEntityAnimation(block())

        heart.addEntityAnimation(block())
    }

    inline fun <reified T : WorldObjectAnimation> cancelEntityAnimation() {
        cancelEntityAnimation(T::class)
    }

    fun <T : WorldObjectAnimation> cancelEntityAnimation(type: KClass<T>) {
        head.cancelEntityAnimation(type)
        body.cancelEntityAnimation(type)
        tail.parts.forEach {
            it.cancelEntityAnimation(type)
        }
        leftWing.cancelEntityAnimation(type)
        rightWing.cancelEntityAnimation(type)

        beak.leftBeak.cancelEntityAnimation(type)
        beak.rightBeak.cancelEntityAnimation(type)

        heart.cancelEntityAnimation(type)
    }

    fun camouflage() {
        if (isCamouflaged)
            return

        camouflageTimer = CAMOUFLAGE_TIME
        camouflageDirection = 1.0f
    }

    fun decamouflage(immidiate: Boolean = false) {
        //if (!isCamouflaged)
        //    return

        if (immidiate) {
            camouflageTimer = 0.0.seconds
            camouflageFactor = 0.0f
        } else {
            camouflageTimer = CAMOUFLAGE_TIME
            camouflageDirection = -1.0f
        }
    }

    fun openVortex(x: Float, y: Float, size: Float, duration: Duration, callback: () -> Unit = {}) {
        //if (isVortexOpen)
        //    return

        vortex.x = x
        vortex.y = y
        vortexTimer = duration
        vortexOpenCloseTime = duration
        vortexDirection = 1.0f
        vortexTargetSize = size
        vortexCallback = callback
    }

    fun closeVortex(duration: Duration) {
        //if (!isVortexOpen)
        //    return

        if (duration == 0.0.seconds) {
            vortexTimer = 0.0.seconds
            vortex.size = 0.0f
        } else {
            vortexTimer = duration
            vortexOpenCloseTime = duration
            vortexDirection = -1.0f
        }
    }

    override fun drawDebug(renderer: ShapeRenderer) {
        head.collider.drawDebug(renderer)
        body.collider.drawDebug(renderer)
        body.centerCollider.drawDebug(renderer)
        tail.parts.forEach {
            it.collider.drawDebug(renderer)
        }
        leftWing.collider.drawDebug(renderer)
        leftWing.lowerCollider.drawDebug(renderer)
        rightWing.collider.drawDebug(renderer)
        rightWing.lowerCollider.drawDebug(renderer)

        beak.leftBeak.collider.drawDebug(renderer)
        beak.rightBeak.collider.drawDebug(renderer)

        heart.collider.drawDebug(renderer)
    }
}