package com.cozmicgames.bosses.boss1

import com.cozmicgames.Game
import com.cozmicgames.bosses.Boss
import com.cozmicgames.entities.Entity
import com.cozmicgames.entities.worldObjects.AreaEffectSource
import com.cozmicgames.entities.worldObjects.animations.WorldObjectAnimation
import com.cozmicgames.entities.worldObjects.animations.HitAnimation
import com.cozmicgames.entities.worldObjects.animations.ParalyzeAnimation
import com.cozmicgames.graphics.RenderLayers
import com.cozmicgames.graphics.particles.effects.DeathSplatterEffect
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

class Boss1(override val difficulty: Difficulty, val isFinalBattle: Boolean = false) : Entity("boss1"), AreaEffectSource, Boss {
    companion object {
        const val FULL_HEALTH = 3

        private val INVULNERABLE_TIME = 2.0.seconds
        private val PARALYZED_TIME = 5.0.seconds

        private const val HEAD_SIZE = 180.0f

        private const val HEAD_LAYER = RenderLayers.BOSS1 + 10

        private const val BEAK_LAYER = RenderLayers.BOSS1 + 8

        private const val HEART_LAYER = RenderLayers.BOSS1 + 5

        private val TENTACLE_OFFSETS = arrayOf(
            0.67f to -0.45f,
            0.62f to -0.56f,
            0.57f to -0.7f,
            0.36f to -0.74f,
            -0.67f to -0.45f,
            -0.62f to -0.56f,
            -0.57f to -0.7f,
            -0.36f to -0.74f,
        )

        private val TENTACLE_ANGLES = arrayOf(
            15.0.degrees,
            0.0.degrees,
            (-15.0).degrees,
            (-30.0).degrees,
            (-15.0).degrees,
            0.0.degrees,
            15.0.degrees,
            30.0.degrees
        )

        private val TENTACLE_SCALES = arrayOf(
            2.0f,
            2.3f,
            2.7f,
            3.0f,
            2.0f,
            2.3f,
            2.7f,
            3.0f
        )

        private val TENTACLE_LAYERS = arrayOf(
            RenderLayers.BOSS1 + 10,
            RenderLayers.BOSS1 + 20,
            RenderLayers.BOSS1 + 30,
            RenderLayers.BOSS1 + 40,
            RenderLayers.BOSS1 + 50,
            RenderLayers.BOSS1 + 60,
            RenderLayers.BOSS1 + 70,
            RenderLayers.BOSS1 + 80
        )

        private const val BEAK_OFFSET_X = 0.0f
        private const val BEAK_OFFSET_Y = -0.7f

        private const val HEART_OFFSET_X = 0.0f
        private const val HEART_OFFSET_Y = -0.8f
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

    override val movementController = Boss1MovementController(this)

    val isInvulnerable get() = isInvulnerableTimer > 0.0.seconds

    override val isParalyzed get() = isParalyzedTimer > 0.0.seconds

    private val head = Head(this, HEAD_SIZE, HEAD_LAYER)
    private val tentacles: List<Tentacle>
    private val beak = Beak(this, BEAK_LAYER)
    private val heart = Heart(this, HEART_LAYER)
    private var isInvulnerableTimer = 0.0.seconds
    private var isParalyzedTimer = 0.0.seconds

    init {
        val tentacles = arrayListOf<Tentacle>()

        repeat(8) {
            val tentacle = Tentacle(this, it, it >= 4, TENTACLE_LAYERS[it], TENTACLE_ANGLES[it], TENTACLE_SCALES[it] * 0.8f)
            tentacles += tentacle
        }

        this.tentacles = tentacles
    }

    override fun addToWorld() {
        Game.world.add(head)

        tentacles.forEach { tentacle ->
            tentacle.parts.forEach(Game.world::add)
        }

        Game.world.add(beak.leftBeak)
        Game.world.add(beak.rightBeak)
        Game.world.add(heart)
    }

    override fun removeFromWorld() {
        Game.world.remove(head)

        tentacles.forEach { tentacle ->
            tentacle.parts.forEach(Game.world::remove)
        }

        Game.world.remove(beak.leftBeak)
        Game.world.remove(beak.rightBeak)
        Game.world.remove(heart)
    }

    override fun addToPhysics() {
        Game.physics.addCollider(head.collider)
        Game.physics.addHittable(head)

        tentacles.forEach { tentacle ->
            tentacle.parts.forEach {
                Game.physics.addCollider(it.collider)
            }

            Game.physics.addHittable(tentacle)
        }

        Game.physics.addCollider(heart.collider)
        Game.physics.addHittable(heart)
    }

    override fun removeFromPhysics() {
        Game.physics.removeCollider(head.collider)
        Game.physics.removeHittable(head)

        tentacles.forEach { tentacle ->
            tentacle.parts.forEach {
                Game.physics.removeCollider(it.collider)
            }

            Game.physics.removeHittable(tentacle)
        }

        Game.physics.removeCollider(heart.collider)
        Game.physics.removeHittable(heart)
    }

    override fun update(delta: Duration) {
        if (Game.players.isHost) {
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

            movementController.update(delta)
            tentacles.forEach {
                it.update(delta, movementController.movement.tentacleMovement)
            }
            beak.update(delta, movementController.movement.beakMovement)

            val cos = rotation.cosine
            val sin = rotation.sine

            head.x = x
            head.y = y
            head.rotation = rotation
            head.collider.update(head.x, head.y)

            tentacles.forEachIndexed { index, tentacle ->
                val (offsetX, offsetY) = TENTACLE_OFFSETS[index]

                val tentacleOffsetX = offsetX * HEAD_SIZE * 0.5f
                val tentacleOffsetY = offsetY * HEAD_SIZE * 0.5f

                tentacle.x = x + cos * tentacleOffsetX - sin * tentacleOffsetY
                tentacle.y = y + sin * tentacleOffsetX + cos * tentacleOffsetY
                tentacle.rotation = rotation

                Game.players.setGlobalState("boss1tentacle${index}x", tentacle.x)
                Game.players.setGlobalState("boss1tentacle${index}y", tentacle.y)
                Game.players.setGlobalState("boss1tentacle${index}rotation", tentacle.rotation.degrees)
            }

            val beakOffsetX = BEAK_OFFSET_X * HEAD_SIZE * 0.5f
            val beakOffsetY = BEAK_OFFSET_Y * HEAD_SIZE * 0.5f

            beak.x = x + cos * beakOffsetX - sin * beakOffsetY
            beak.y = y + sin * beakOffsetX + cos * beakOffsetY
            beak.rotation = rotation

            Game.players.setGlobalState("boss1beakx", beak.x)
            Game.players.setGlobalState("boss1beaky", beak.y)
            Game.players.setGlobalState("boss1beakrotation", beak.rotation.degrees)

            val heartOffsetX = HEART_OFFSET_X * HEAD_SIZE * 0.5f
            val heartOffsetY = HEART_OFFSET_Y * HEAD_SIZE * 0.5f

            heart.x = x + cos * heartOffsetX - sin * heartOffsetY
            heart.y = y + sin * heartOffsetX + cos * heartOffsetY
            heart.rotation = rotation

            Game.players.setGlobalState("boss1heartx", heart.x)
            Game.players.setGlobalState("boss1hearty", heart.y)
            Game.players.setGlobalState("boss1heartrotation", heart.rotation.degrees)
        } else {
            x = Game.players.getGlobalState("boss1x") ?: head.x
            y = Game.players.getGlobalState("boss1y") ?: head.y
            rotation = (Game.players.getGlobalState("boss1rotation") ?: 0.0f).degrees

            tentacles.forEachIndexed { index, tentacle ->
                tentacle.x = Game.players.getGlobalState("boss1tentacle${index}x") ?: 0.0f
                tentacle.y = Game.players.getGlobalState("boss1tentacle${index}y") ?: 0.0f
                tentacle.rotation = (Game.players.getGlobalState("boss1tentacle${index}rotation") ?: 0.0f).degrees
            }

            beak.x = Game.players.getGlobalState("boss1beakx") ?: 0.0f
            beak.y = Game.players.getGlobalState("boss1beaky") ?: 0.0f
            beak.rotation = (Game.players.getGlobalState("boss1beakrotation") ?: 0.0f).degrees

            heart.x = Game.players.getGlobalState("boss1heartx") ?: 0.0f
            heart.y = Game.players.getGlobalState("boss1hearty") ?: 0.0f
            heart.rotation = (Game.players.getGlobalState("boss1heartrotation") ?: 0.0f).degrees
        }
    }

    fun paralyze() {
        if (isInvulnerable)
            return

        addEntityAnimation { ParalyzeAnimation(PARALYZED_TIME, 0.7f) }

        if (Game.players.isHost) {
            tentacles.forEach {
                it.paralyze(PARALYZED_TIME, false)
            }

            movementController.onParalyze()
            isParalyzedTimer = PARALYZED_TIME
        }
    }

    fun hit() {
        if (isInvulnerable || health <= 0)
            return

        cancelEntityAnimation<ParalyzeAnimation>()
        addEntityAnimation { HitAnimation(INVULNERABLE_TIME) }

        if (Game.players.isHost) {
            health--
            if (health < 0) health = 0

            movementController.onHit()

            if (health <= 0) {
                removeFromPhysics()
                movementController.onDeath()

                if (isFinalBattle) {
                    head.texture = Game.resources.boss1headDead.slice()

                    Game.world.remove(heart)
                    Game.particles.add(DeathSplatterEffect(heart.x, heart.y, heart.rotation + 90.0.degrees))
                }
            } else {
                tentacles.forEach {
                    it.unparalyze()
                }

                isInvulnerableTimer = INVULNERABLE_TIME
                isParalyzedTimer = 0.0.seconds
            }
        }
    }

    fun addEntityAnimation(block: () -> WorldObjectAnimation) {
        head.addEntityAnimation(block())

        tentacles.forEach {
            it.parts.forEach { part ->
                part.addEntityAnimation(block())
            }
        }

        beak.leftBeak.addEntityAnimation(block())
        beak.rightBeak.addEntityAnimation(block())

        heart.addEntityAnimation(block())
    }

    inline fun <reified T : WorldObjectAnimation> cancelEntityAnimation() {
        cancelEntityAnimation(T::class)
    }

    fun <T : WorldObjectAnimation> cancelEntityAnimation(type: KClass<T>) {
        head.cancelEntityAnimation(type)

        tentacles.forEach {
            it.parts.forEach { part ->
                part.cancelEntityAnimation(type)
            }
        }

        beak.leftBeak.cancelEntityAnimation(type)
        beak.rightBeak.cancelEntityAnimation(type)

        heart.cancelEntityAnimation(type)
    }

    override fun drawDebug(renderer: ShapeRenderer) {
        head.collider.drawDebug(renderer)

        tentacles.forEach {
            it.parts.forEach { part ->
                part.collider.drawDebug(renderer)
            }
        }

        beak.leftBeak.collider.drawDebug(renderer)
        beak.rightBeak.collider.drawDebug(renderer)

        heart.collider.drawDebug(renderer)
    }
}