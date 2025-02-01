package com.cozmicgames.bosses.tutorialBoss

import com.cozmicgames.Constants
import com.cozmicgames.Game
import com.cozmicgames.TutorialStage
import com.cozmicgames.bosses.Boss
import com.cozmicgames.entities.worldObjects.animations.HitAnimation
import com.cozmicgames.entities.worldObjects.animations.ParalyzeAnimation
import com.cozmicgames.entities.worldObjects.animations.WorldObjectAnimation
import com.cozmicgames.graphics.RenderLayers
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

class TutorialBoss : Boss {
    companion object {
        private val INVULNERABLE_TIME = 2.0.seconds
        private val PARALYZED_TIME = 5.0.seconds

        private const val HEAD_SCALE = 3.0f
        private const val EYES_SCALE = 3.0f
        private const val MOUTH_SCALE = 3.0f
        private const val BODY_SCALE = 3.0f
        private const val FIN_SCALE = 3.0f
        private const val TAIL_SCALE = 3.0f

        private const val HEAD_LAYER = RenderLayers.TUTORIAL_BOSS + 20
        private const val EYES_LAYER = RenderLayers.TUTORIAL_BOSS + 21
        private const val MOUTH_LAYER = RenderLayers.TUTORIAL_BOSS + 7
        private const val HEART_LAYER = RenderLayers.TUTORIAL_BOSS + 5
        private const val BODY_LAYER = RenderLayers.TUTORIAL_BOSS + 19
        private const val BACK_FIN_LAYER = RenderLayers.TUTORIAL_BOSS + 5
        private const val BODY_FIN_LAYER = RenderLayers.TUTORIAL_BOSS + 25
        private const val TAIL_LAYER = RenderLayers.TUTORIAL_BOSS + 5

        private const val BACK_FIN_BODY_PART_INDEX = (Constants.BOSS_TUTORIAL_BODY_PARTS * 0.35f).toInt()
        private const val BODY_FIN_BODY_PART_INDEX = (Constants.BOSS_TUTORIAL_BODY_PARTS * 0.25f).toInt()
        private const val TAIL_BODY_PART_INDEX = Constants.BOSS_TUTORIAL_BODY_PARTS - 1
    }

    override val difficulty = Difficulty.EASY
    override val fullHealth = 2
    override var health = fullHealth
    override var rotation = 0.0.degrees
    override var x = 0.0f
    override var y = 0.0f
    override val movementController = TutorialBossMovementController(this)

    override var isParalyzed = false

    var impulseX = 0.0f
    var impulseY = 0.0f
    var impulseSpin = 0.0f

    var isFlipped = false
        private set

    private val head = Head(this, HEAD_SCALE, HEAD_LAYER)
    private val eyes = Eyes(this, EYES_SCALE, EYES_LAYER)
    private val mouth = Mouth(this, MOUTH_SCALE, MOUTH_LAYER)
    private val heart = Heart(this, HEART_LAYER)
    private val body = Body(this, BODY_SCALE, BODY_LAYER)
    private val backFin = BackFin(this, FIN_SCALE, BACK_FIN_LAYER)
    private val bodyFin = BodyFin(this, FIN_SCALE, BODY_FIN_LAYER)
    private val tail = Tail(this, TAIL_SCALE, TAIL_LAYER)

    fun flip() {
        isFlipped = !isFlipped
    }

    override fun addToWorld() {
        Game.world.add(head)
        Game.world.add(eyes)
        Game.world.add(mouth)
        Game.world.add(heart)
        body.parts.forEach(Game.world::add)
        Game.world.add(backFin)
        Game.world.add(bodyFin)
        Game.world.add(tail)
    }

    override fun removeFromWorld() {
        Game.world.remove(head)
        Game.world.remove(eyes)
        Game.world.remove(mouth)
        Game.world.remove(heart)
        body.parts.forEach(Game.world::remove)
        Game.world.remove(backFin)
        Game.world.remove(bodyFin)
        Game.world.remove(tail)
    }

    override fun addToPhysics() {
        Game.physics.addCollider(head.collider)
        Game.physics.addHittable(head)

        Game.physics.addCollider(mouth.collider)

        Game.physics.addCollider(heart.collider)
        Game.physics.addHittable(heart)

        body.parts.forEach {
            it.collider.apply(Game.physics::addCollider)
        }

        Game.physics.addHittable(body)

        Game.physics.addCollider(mouth.collider)
        Game.physics.addCollider(tail.collider)

        Game.physics.addCollider(backFin.collider)
        Game.physics.addCollider(bodyFin.collider)
    }

    override fun removeFromPhysics() {
        Game.physics.removeCollider(head.collider)
        Game.physics.removeHittable(head)

        Game.physics.removeCollider(mouth.collider)

        Game.physics.removeCollider(heart.collider)
        Game.physics.removeHittable(heart)

        body.parts.forEach {
            it.collider.apply(Game.physics::removeCollider)
        }

        Game.physics.removeHittable(body)

        Game.physics.removeCollider(backFin.collider)
        Game.physics.removeCollider(bodyFin.collider)
        Game.physics.removeCollider(tail.collider)
    }

    override fun update(delta: Duration, isFighting: Boolean) {
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

        if (isFighting) {
            movementController.update(delta, isFighting)
            body.update(delta, movementController.movement.bodyMovement)
            mouth.update(delta, movementController.movement.mouthMovement)
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

        eyes.x = head.x
        eyes.y = head.y
        eyes.rotation = head.rotation

        val mouthPivotOffsetX = head.width * 0.48f * (if (isFlipped) -1.0f else 1.0f)
        val mouthPivotOffsetY = -head.height * 0.25f

        val mouthPivotX = head.x + cos * mouthPivotOffsetX - sin * mouthPivotOffsetY
        val mouthPivotY = head.y + sin * mouthPivotOffsetX + cos * mouthPivotOffsetY

        val mouthOffsetX = mouth.width * -0.5f * (if (isFlipped) -1.0f else 1.0f)
        val mouthOffsetY = 0.0f

        val mouthAngle = head.rotation + mouth.mouthAngle
        val mouthCos = mouthAngle.cosine
        val mouthSin = mouthAngle.sine

        mouth.x = mouthPivotX + mouthCos * mouthOffsetX - mouthSin * mouthOffsetY
        mouth.y = mouthPivotY + mouthSin * mouthOffsetX + mouthCos * mouthOffsetY
        mouth.rotation = rotation + mouth.mouthAngle
        mouth.collider.update(mouth.x, mouth.y)

        val heartOffsetX = if (isFlipped) head.width * 0.35f else -head.width * 0.35f
        val heartOffsetY = -head.height * 0.25f

        heart.x = x + cos * heartOffsetX - sin * heartOffsetY
        heart.y = y + sin * heartOffsetX + cos * heartOffsetY
        heart.rotation = rotation

        val backFinBodyPart = body.parts[BACK_FIN_BODY_PART_INDEX]
        val backFinOffsetX = backFinBodyPart.width * 0.1f
        val backFinOffsetY = backFinBodyPart.height * 0.6f
        val backFinAngle = backFinBodyPart.rotation

        val backFinCos = backFinAngle.cosine
        val backFinSin = backFinAngle.sine

        backFin.x = backFinBodyPart.x + backFinCos * backFinOffsetX - backFinSin * backFinOffsetY
        backFin.y = backFinBodyPart.y + backFinSin * backFinOffsetX + backFinCos * backFinOffsetY
        backFin.rotation = backFinAngle - (if (isFlipped) -(10.0).degrees else 10.0.degrees)

        val bodyFinBodyPart = body.parts[BODY_FIN_BODY_PART_INDEX]
        val bodyFinOffsetX = bodyFinBodyPart.width * -0.1f
        val bodyFinOffsetY = -bodyFinBodyPart.height * 0.6f
        val bodyFinAngle = bodyFinBodyPart.rotation

        val bodyFinCos = bodyFinAngle.cosine
        val bodyFinSin = bodyFinAngle.sine

        bodyFin.x = bodyFinBodyPart.x + bodyFinCos * bodyFinOffsetX - bodyFinSin * bodyFinOffsetY
        bodyFin.y = bodyFinBodyPart.y + bodyFinSin * bodyFinOffsetX + bodyFinCos * bodyFinOffsetY
        bodyFin.rotation = bodyFinAngle - (if (isFlipped) -(8.0).degrees else 8.0.degrees)

        val tailBodyPart = body.parts[TAIL_BODY_PART_INDEX]
        val tailOffsetX = if (isFlipped) -tailBodyPart.width * 1.0f else tailBodyPart.width * 1.0f
        val tailOffsetY = -tailBodyPart.height * 0.02f
        val tailAngle = tailBodyPart.rotation

        val tailCos = tailAngle.cosine
        val tailSin = tailAngle.sine

        tail.x = tailBodyPart.x + tailCos * tailOffsetX - tailSin * tailOffsetY
        tail.y = tailBodyPart.y + tailSin * tailOffsetX + tailCos * tailOffsetY
        tail.rotation = tailAngle
    }

    override fun paralyze() {
        if (Game.player.tutorialStage < TutorialStage.PARALYZE.ordinal)
            return

        isParalyzed = true

        addEntityAnimation { ParalyzeAnimation(PARALYZED_TIME, 0.7f) }

        movementController.onParalyze()
    }

    override fun hit() {
        if (Game.player.tutorialStage < TutorialStage.HIT.ordinal)
            return

        if (health <= 0)
            return

        Game.audio.hitEnemySound.play(0.5f)

        cancelEntityAnimation<ParalyzeAnimation>()
        addEntityAnimation { HitAnimation(INVULNERABLE_TIME) }

        health--
        if (health < 0) health = 0

        eyes.texture = Game.textures.bossTutorialEyesDead.slice()

        movementController.onHit()

        if (health <= 0)
            removeFromPhysics()
    }

    fun addEntityAnimation(block: () -> WorldObjectAnimation) {
        head.addEntityAnimation(block())
        mouth.addEntityAnimation(block())
        body.parts.forEach { it.addEntityAnimation(block()) }
        backFin.addEntityAnimation(block())
        bodyFin.addEntityAnimation(block())
        tail.addEntityAnimation(block())
    }

    inline fun <reified T : WorldObjectAnimation> cancelEntityAnimation() {
        cancelEntityAnimation(T::class)
    }

    fun <T : WorldObjectAnimation> cancelEntityAnimation(type: KClass<T>) {
        head.cancelEntityAnimation(type)
        mouth.cancelEntityAnimation(type)
        body.parts.forEach { it.cancelEntityAnimation(type) }
        backFin.cancelEntityAnimation(type)
        bodyFin.cancelEntityAnimation(type)
        tail.cancelEntityAnimation(type)
    }

    override fun drawDebug(renderer: ShapeRenderer) {
        head.collider.drawDebug(renderer)
        mouth.collider.drawDebug(renderer)
        heart.collider.drawDebug(renderer)
        body.parts.forEach { it.collider.drawDebug(renderer) }
        backFin.collider.drawDebug(renderer)
        bodyFin.collider.drawDebug(renderer)
        tail.collider.drawDebug(renderer)
    }
}