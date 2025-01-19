package com.cozmicgames.bosses

import com.cozmicgames.Game
import com.cozmicgames.utils.Difficulty
import com.littlekt.graphics.g2d.shape.ShapeRenderer
import com.littlekt.math.geom.Angle
import kotlin.time.Duration

class TodoBossDesc: BossDesc {
    override val name = "Todo Boss"

    override val reward = 300

    override val preview = Game.resources.todoPreview

    override val fullHealth get() = 4

    override fun createBoss(difficulty: Difficulty): Boss {
        return object : Boss {
            override val health: Int
                get() = TODO("Not yet implemented")
            override var x: Float
                get() = TODO("Not yet implemented")
                set(value) {}
            override var y: Float
                get() = TODO("Not yet implemented")
                set(value) {}
            override var rotation: Angle
                get() = TODO("Not yet implemented")
                set(value) {}
            override val movementController: BossMovementController
                get() = TODO("Not yet implemented")

            override fun addToWorld() {
                TODO("Not yet implemented")
            }

            override fun removeFromWorld() {
                TODO("Not yet implemented")
            }

            override fun addToPhysics() {
                TODO("Not yet implemented")
            }

            override fun removeFromPhysics() {
                TODO("Not yet implemented")
            }

            override fun update(delta: Duration) {
                TODO("Not yet implemented")
            }

            override fun drawDebug(renderer: ShapeRenderer) {
                TODO("Not yet implemented")
            }

        }
    }
}