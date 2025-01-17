package com.cozmicgames.bosses.boss1

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.RectangleCollisionShape
import com.littlekt.graphics.slice
import com.littlekt.math.geom.cosine
import com.littlekt.math.geom.sine
import kotlin.time.Duration

class BeakPart(val beak: Beak, private val left: Boolean, layer: Int) : EnemyPart("boss1beak${if (left) "left" else "right"}") {
    override val renderLayer = layer

    override val texture = Game.resources.boss1beak.slice()

    override val width get() = Game.resources.boss1beak.width * 2.0f

    override val height get() = Game.resources.boss1beak.height * 2.0f

    override val flipX get() = !left

    override val collider = Collider(getRectangleCollisionShape(scaleX = 0.5f), beak)

    override fun updateWorldObject(delta: Duration, fightStarted: Boolean) {
        val pivotX = beak.x + (if (left) -width else width) * 0.1f
        val pivotY = beak.y

        val cos = rotation.cosine
        val sin = rotation.sine

        val xOffset = if (left) -width * 0.25f else width * 0.25f
        val yOffset = -height * 0.4f

        x = pivotX + cos * xOffset - sin * yOffset
        y = pivotY + sin * xOffset + cos * yOffset

        (collider.shape as? RectangleCollisionShape)?.angle = rotation
        collider.update(x,y)
    }
}