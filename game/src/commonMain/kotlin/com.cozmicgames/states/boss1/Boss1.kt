package com.cozmicgames.states.boss1

import com.cozmicgames.Game
import com.cozmicgames.entities.animations.EntityAnimation
import com.cozmicgames.graphics.RenderLayers
import com.littlekt.Releasable
import com.littlekt.math.geom.cosine
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.sine
import com.littlekt.util.seconds
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class Boss1 : Releasable {
    //TODO: Ideas
    // - Make it spin as a special attack
    // - Add movement patterns
    // - Add stages
    // - Make it shoot projectiles
    // - Make it spawn enemies
    // - Obstacles, asteroids

    private companion object {
        private const val HEAD_WIDTH = 256.0f
        private const val HEAD_HEIGHT = 256.0f

        private const val HEAD_LAYER = RenderLayers.ENEMY_BEGIN + 10

        private const val BEAK_LAYER = RenderLayers.ENEMY_BEGIN + 5

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
            10.0.degrees,
            0.0.degrees,
            (-10.0).degrees,
            (-30.0).degrees,
            (-10.0).degrees,
            0.0.degrees,
            10.0.degrees,
            30.0.degrees
        )

        private val TENTACLE_LAYERS = arrayOf(
            RenderLayers.ENEMY_BEGIN + 10,
            RenderLayers.ENEMY_BEGIN + 20,
            RenderLayers.ENEMY_BEGIN + 30,
            RenderLayers.ENEMY_BEGIN + 40,
            RenderLayers.ENEMY_BEGIN + 50,
            RenderLayers.ENEMY_BEGIN + 60,
            RenderLayers.ENEMY_BEGIN + 70,
            RenderLayers.ENEMY_BEGIN + 80
        )

        private const val BEAK_OFFSET_X = 0.0f
        private const val BEAK_OFFSET_Y = -0.7f
    }

    var x = 0.0f
    var y = 0.0f
    var rotation = 0.0.degrees

    private val head = Head(HEAD_LAYER)
    private val tentacles: List<Tentacle>
    private val beak = Beak(BEAK_LAYER)

    val movementController = MovementController()

    init {
        val tentacles = arrayListOf<Tentacle>()

        repeat(8) {
            val tentacle = Tentacle(it, it > 3, TENTACLE_LAYERS[it], TENTACLE_ANGLES[it])
            tentacles += tentacle
        }

        this.tentacles = tentacles
    }

    fun addToEntities() {
        Game.entities.add(head)

        tentacles.forEach { tentacle ->
            tentacle.parts.forEach(Game.entities::add)
        }

        Game.entities.add(beak.leftBeak)
        Game.entities.add(beak.rightBeak)
    }

    fun removeFromEntities() {
        Game.entities.remove(head)

        tentacles.forEach { tentacle ->
            tentacle.parts.forEach(Game.entities::remove)
        }

        Game.entities.remove(beak.leftBeak)
        Game.entities.remove(beak.rightBeak)
    }

    fun addToPhysics() {
        Game.physics.addCollider(head.collider)

        tentacles.forEach { tentacle ->
            tentacle.parts.forEach {
                Game.physics.addCollider(it.collider)
            }
        }

        Game.physics.addCollider(beak.leftBeak.collider)
        Game.physics.addCollider(beak.rightBeak.collider)
    }

    fun removeFromPhysics() {
        Game.physics.removeCollider(head.collider)

        tentacles.forEach { tentacle ->
            tentacle.parts.forEach {
                Game.physics.removeCollider(it.collider)
            }
        }

        Game.physics.removeCollider(beak.leftBeak.collider)
        Game.physics.removeCollider(beak.rightBeak.collider)
    }

    var t = 0.0.seconds

    fun update(delta: Duration) {
        if (Game.players.isHost) {
            t += delta

            rotation = 10.0.degrees * kotlin.math.sin(t.seconds * 2.0)

            x = 100.0f * kotlin.math.cos(t.seconds)
            y = 100.0f * kotlin.math.sin(t.seconds)

            movementController.update(delta)

            val cos = rotation.cosine
            val sin = rotation.sine

            head.x = x
            head.y = y
            head.rotation = rotation
            head.collider.x = head.x
            head.collider.y = head.y
            head.collider.update()

            tentacles.forEachIndexed { index, tentacle ->
                val (offsetX, offsetY) = TENTACLE_OFFSETS[index]

                val tentacleOffsetX = offsetX * HEAD_WIDTH * 0.5f
                val tentacleOffsetY = offsetY * HEAD_HEIGHT * 0.5f

                tentacle.x = x + cos * tentacleOffsetX - sin * tentacleOffsetY
                tentacle.y = y + sin * tentacleOffsetX + cos * tentacleOffsetY
                tentacle.rotation = rotation

                Game.players.setGlobalState("boss1tentacle${index}x", tentacle.x)
                Game.players.setGlobalState("boss1tentacle${index}y", tentacle.y)
                Game.players.setGlobalState("boss1tentacle${index}rotation", tentacle.rotation.degrees)
            }

            val beakOffsetX = BEAK_OFFSET_X * HEAD_WIDTH * 0.5f
            val beakOffsetY = BEAK_OFFSET_Y * HEAD_HEIGHT * 0.5f

            beak.x = x + cos * beakOffsetX - sin * beakOffsetY
            beak.y = y + sin * beakOffsetX + cos * beakOffsetY
            beak.rotation = rotation

            Game.players.setGlobalState("boss1beakx", beak.x)
            Game.players.setGlobalState("boss1beaky", beak.y)
            Game.players.setGlobalState("boss1beakrotation", beak.rotation.degrees)
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
        }

        tentacles.forEach {
            it.update(delta, movementController.tentacleMovement)
        }
        beak.update(delta, movementController.beakMovement)
    }

    fun addEntityAnimation(animation: EntityAnimation) {
        head.addEntityAnimation(animation)

        tentacles.forEach {
            it.parts.forEach { part ->
                part.addEntityAnimation(animation)
            }
        }

        beak.leftBeak.addEntityAnimation(animation)
        beak.rightBeak.addEntityAnimation(animation)
    }

    override fun release() {
    }
}