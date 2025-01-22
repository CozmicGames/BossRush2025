package com.cozmicgames.bosses.boss3

import com.cozmicgames.Game
import com.cozmicgames.bosses.Boss
import com.cozmicgames.bosses.boss1.Boss1
import com.cozmicgames.entities.worldObjects.animations.HitAnimation
import com.cozmicgames.entities.worldObjects.animations.ParalyzeAnimation
import com.cozmicgames.entities.worldObjects.animations.WorldObjectAnimation
import com.cozmicgames.graphics.RenderLayers
import com.cozmicgames.utils.Difficulty
import com.littlekt.graphics.g2d.shape.ShapeRenderer
import com.littlekt.math.geom.cosine
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.sine
import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class Boss3(override val difficulty: Difficulty) : Boss {
    companion object {
        const val FULL_HEALTH = 4

        private val INVULNERABLE_TIME = 2.0.seconds
        private val PARALYZED_TIME = 5.0.seconds

        private val LEG_OFFSETS = arrayOf(
            0.58f to -0.45f,
            0.53f to -0.56f,
            0.45f to -0.7f,
            -0.58f to -0.45f,
            -0.53f to -0.56f,
            -0.45f to -0.7f,
        )

        private val LEG_ANGLES = arrayOf(
            (-25.0).degrees,
            (-35.0).degrees,
            (-50.0).degrees,
            25.0.degrees,
            35.0.degrees,
            50.0.degrees,
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
            RenderLayers.ENEMY_BEGIN + 10,
            RenderLayers.ENEMY_BEGIN + 20,
            RenderLayers.ENEMY_BEGIN + 30,
            RenderLayers.ENEMY_BEGIN + 10,
            RenderLayers.ENEMY_BEGIN + 20,
            RenderLayers.ENEMY_BEGIN + 30,
        )

        private val ARM_OFFSETS = arrayOf(
            0.38f to -0.56f,
            -0.38f to -0.56f,
        )

        private val ARM_ANGLES = arrayOf(
            (-20.0).degrees,
            20.0.degrees,
        )

        private val ARM_SCALES = arrayOf(
            2.0f,
            2.0f,
        )

        private val ARM_LAYERS = arrayOf(
            RenderLayers.ENEMY_BEGIN + 50,
            RenderLayers.ENEMY_BEGIN + 50,
        )

        private const val HEAD_SCALE = 3.0f
        private const val HEAD_LAYER = RenderLayers.ENEMY_BEGIN + 40

        private const val HEART_SCALE = 1.8f
        private const val HEART_LAYER = RenderLayers.ENEMY_BEGIN + 35

        private const val BEAK_SCALE = 1.8f
        private const val BEAK_LAYER = RenderLayers.ENEMY_BEGIN + 38
    }

    override var health = FULL_HEALTH
    override var x = 0.0f
    override var y = 0.0f
    override var rotation = 0.0.degrees

    val isInvulnerable get() = isInvulnerableTimer > 0.0.seconds

    override val isParalyzed get() = isParalyzedTimer > 0.0.seconds

    private var isInvulnerableTimer = 0.0.seconds
    private var isParalyzedTimer = 0.0.seconds

    override val movementController = Boss3MovementController(this)

    private val head = Head(this, HEAD_SCALE, HEAD_LAYER)
    private val legs: List<Leg>
    private val arms: List<Arm>
    private val heart = Heart(this, HEART_SCALE, HEART_LAYER)
    private val beak = Beak(this, BEAK_SCALE, BEAK_LAYER)

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
    }

    override fun removeFromPhysics() {

    }

    override fun update(delta: Duration) {
        if (Game.players.isHost) {
            isInvulnerableTimer -= delta
            if (isInvulnerableTimer <= 0.0.seconds)
                isInvulnerableTimer = 0.0.seconds

            isParalyzedTimer -= delta
            if (isParalyzedTimer <= 0.0.seconds)
                isParalyzedTimer = 0.0.seconds

            movementController.update(delta)
            beak.update(delta, movementController.movement.beakMovement)

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

                Game.players.setGlobalState("boss3leg${index}x", leg.x)
                Game.players.setGlobalState("boss3leg${index}y", leg.y)
                Game.players.setGlobalState("boss3leg${index}rotation", leg.rotation.degrees)
            }

            arms.forEachIndexed { index, arm ->
                val (offsetX, offsetY) = ARM_OFFSETS[index]

                val armOffsetX = offsetX * head.width * 0.5f
                val armOffsetY = offsetY * head.height * 0.5f

                arm.x = x + cos * armOffsetX - sin * armOffsetY
                arm.y = y + sin * armOffsetX + cos * armOffsetY
                arm.rotation = rotation

                Game.players.setGlobalState("boss3arm${index}x", arm.x)
                Game.players.setGlobalState("boss3arm${index}y", arm.y)
                Game.players.setGlobalState("boss3arm${index}rotation", arm.rotation.degrees)
            }

            val beakOffsetX = 0.0f
            val beakOffsetY = -0.4f * head.height

            beak.x = x + cos * beakOffsetX - sin * beakOffsetY
            beak.y = y + sin * beakOffsetX + cos * beakOffsetY
            beak.rotation = rotation

            Game.players.setGlobalState("boss3beakx", beak.x)
            Game.players.setGlobalState("boss3beaky", beak.y)
            Game.players.setGlobalState("boss3beakrotation", beak.rotation.degrees)

            val heartOffsetX = 0.0f
            val heartOffsetY = -0.4f * head.height

            heart.x = x + cos * heartOffsetX - sin * heartOffsetY
            heart.y = y + sin * heartOffsetX + cos * heartOffsetY
            heart.rotation = rotation

            Game.players.setGlobalState("boss3heartx", heart.x)
            Game.players.setGlobalState("boss3hearty", heart.y)
            Game.players.setGlobalState("boss3heartrotation", heart.rotation.degrees)
        } else {
            x = Game.players.getGlobalState("boss3x") ?: head.x
            y = Game.players.getGlobalState("boss3y") ?: head.y
            rotation = (Game.players.getGlobalState("boss3rotation") ?: 0.0f).degrees

            legs.forEachIndexed { index, tentacle ->
                tentacle.x = Game.players.getGlobalState("boss3leg${index}x") ?: 0.0f
                tentacle.y = Game.players.getGlobalState("boss3leg${index}y") ?: 0.0f
                tentacle.rotation = (Game.players.getGlobalState("boss3leg${index}rotation") ?: 0.0f).degrees
            }

            arms.forEachIndexed { index, arm ->
                arm.x = Game.players.getGlobalState("boss3arm${index}x") ?: 0.0f
                arm.y = Game.players.getGlobalState("boss3arm${index}y") ?: 0.0f
                arm.rotation = (Game.players.getGlobalState("boss3arm${index}rotation") ?: 0.0f).degrees
            }

            beak.x = Game.players.getGlobalState("boss3beakx") ?: 0.0f
            beak.y = Game.players.getGlobalState("boss3beaky") ?: 0.0f
            beak.rotation = (Game.players.getGlobalState("boss3beakrotation") ?: 0.0f).degrees

            heart.x = Game.players.getGlobalState("boss3heartx") ?: 0.0f
            heart.y = Game.players.getGlobalState("boss3hearty") ?: 0.0f
            heart.rotation = (Game.players.getGlobalState("boss3heartrotation") ?: 0.0f).degrees
        }
    }

    fun paralyze() {
        if (isInvulnerable)
            return

        addEntityAnimation { ParalyzeAnimation(PARALYZED_TIME, 0.7f) }

        if (Game.players.isHost) {
            movementController.onParalyze()
            isParalyzedTimer = PARALYZED_TIME
        }
    }

    fun hit() {
        if (isInvulnerable)
            return

        cancelEntityAnimation<ParalyzeAnimation>()
        addEntityAnimation { HitAnimation(INVULNERABLE_TIME) }

        if (Game.players.isHost) {
            health--
            movementController.onHit()

            if (health <= 0) {
                //TODO: Handle boss death
            } else {
                isInvulnerableTimer = INVULNERABLE_TIME
                isParalyzedTimer = 0.0.seconds
            }
        }
    }

    fun addEntityAnimation(block: () -> WorldObjectAnimation) {

    }

    inline fun <reified T : WorldObjectAnimation> cancelEntityAnimation() {
        cancelEntityAnimation(T::class)
    }

    fun <T : WorldObjectAnimation> cancelEntityAnimation(type: KClass<T>) {

    }

    override fun drawDebug(renderer: ShapeRenderer) {

    }
}