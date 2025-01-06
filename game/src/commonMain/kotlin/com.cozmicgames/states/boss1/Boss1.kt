package com.cozmicgames.states.boss1

import com.cozmicgames.entities.animations.EntityAnimation
import com.littlekt.Releasable
import com.littlekt.graphics.g2d.SpriteBatch
import com.littlekt.math.geom.degrees
import kotlin.time.Duration

class Boss1 : Releasable {
    private companion object {
        private val TENTACLE_OFFSETS = arrayOf(
            64.0f to 48.0f,
            96.0f to 0.0f,
            96.0f to -48.0f,
            64.0f to -96.0f,
            -64.0f to 48.0f,
            -96.0f to 0.0f,
            -96.0f to -48.0f,
            -64.0f to -96.0f
        )

        private val TENTACLE_ANGLES = arrayOf(
            20.0.degrees,
            10.0.degrees,
            0.0.degrees,
            (-10.0).degrees,
            (-20.0).degrees,
            (-10.0).degrees,
            0.0.degrees,
            10.0.degrees
        )
    }

    private val tentacles: List<Tentacle>

    val movementController = MovementController()

    var x = 0.0f
    var y = 0.0f

    init {
        val tentacles = arrayListOf<Tentacle>()

        repeat(8) {
            val tentacle = Tentacle(it, it > 3)
            tentacle.rotation = TENTACLE_ANGLES[it]
            tentacles += tentacle
        }

        this.tentacles = tentacles
    }

    fun update(delta: Duration) {
        movementController.update(delta)

        tentacles.forEachIndexed { index, tentacle ->
            val (offsetX, offsetY) = TENTACLE_OFFSETS[index]

            tentacle.x = x + offsetX
            tentacle.y = y + offsetY

            tentacle.update(delta, movementController.movement)
        }
    }

    fun render(batch: SpriteBatch) {
        tentacles.forEach {
            it.render(batch)
        }
    }

    fun addEntityAnimation(animation: EntityAnimation) {
        tentacles.forEach {
            it.addEntityAnimation(animation)
        }
    }

    override fun release() {
        tentacles.forEach {
            it.release()
        }
    }
}