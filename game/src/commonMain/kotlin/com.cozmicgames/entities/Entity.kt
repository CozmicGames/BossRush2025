package com.cozmicgames.entities

import com.cozmicgames.physics.Collider
import com.littlekt.graphics.g2d.SpriteBatch
import kotlin.time.Duration

abstract class Entity(val id: String) {
    abstract val renderLayer: Int

    abstract val collider: Collider
    abstract fun update(delta: Duration)
    abstract fun render(batch: SpriteBatch)
    abstract fun playHitAnimation()

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Entity)
            return false

        return id == other.id
    }
}