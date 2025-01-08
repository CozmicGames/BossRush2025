package com.cozmicgames.states.boss1

import com.cozmicgames.Game
import com.cozmicgames.entities.animations.EntityAnimation
import com.cozmicgames.graphics.RenderLayers
import com.littlekt.Releasable
import com.littlekt.math.geom.degrees
import kotlin.time.Duration

class Boss1 : Releasable {
    private companion object {
        private val HEAD_WIDTH = 256.0f
        private val HEAD_HEIGHT = 256.0f

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
    }

    var x = 0.0f
    var y = 0.0f

    private val head = Head(HEAD_LAYER)
    private val tentacles: List<Tentacle>
    private val beak = Beak(BEAK_LAYER)

    val movementController = MovementController()

    init {
        val tentacles = arrayListOf<Tentacle>()

        repeat(8) {
            val tentacle = Tentacle(it > 3, TENTACLE_LAYERS[it])
            tentacle.rotation = TENTACLE_ANGLES[it]
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

    fun update(delta: Duration) {
        movementController.update(delta)

        tentacles.forEachIndexed { index, tentacle ->
            val (offsetX, offsetY) = TENTACLE_OFFSETS[index]

            tentacle.x = x + offsetX * HEAD_WIDTH * 0.5f
            tentacle.y = y + offsetY * HEAD_HEIGHT * 0.5f

            tentacle.update(delta, movementController.tentacleMovement)
        }

        beak.x = x
        beak.y = y - 90.0f

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