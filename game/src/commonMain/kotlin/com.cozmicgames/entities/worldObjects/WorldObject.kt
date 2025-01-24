package com.cozmicgames.entities.worldObjects

import com.cozmicgames.entities.worldObjects.animations.WorldObjectAnimation
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.physics.Collider
import com.littlekt.graphics.Color
import com.littlekt.graphics.MutableColor
import com.littlekt.math.geom.degrees
import kotlin.reflect.KClass
import kotlin.time.Duration

abstract class WorldObject(val id: String) {
    var x = 0.0f
    var y = 0.0f
    var rotation = 0.0.degrees

    var bodyAngle = 0.0.degrees

    open val collider: Collider? = null

    private val animations = arrayListOf<WorldObjectAnimation>()
    val color = MutableColor(Color.WHITE)
    var scale = 1.0f

    fun update(delta: Duration, fightStarted: Boolean) {
        val animationsToRemove = arrayListOf<WorldObjectAnimation>()

        animations.forEach {
            if (it.update(delta))
                animationsToRemove += it
        }

        animations -= animationsToRemove

        if (animations.size == 1) {
            color.set(animations[0].color)
            scale = animations[0].scale
        } else if (animations.size > 1) {
            color.set(animations[0].color)
            scale = animations[0].scale

            for (i in 1 until animations.size) {
                color.mix(animations[i].color, 0.5f, color)
                scale *= animations[i].scale
            }
        } else {
            color.set(Color.WHITE)
            scale = 1.0f
        }

        updateWorldObject(delta, fightStarted)
    }

    protected abstract fun updateWorldObject(delta: Duration, fightStarted: Boolean)

    abstract fun render(renderer: Renderer)

    open fun addEntityAnimation(animation: WorldObjectAnimation) {
        if (animation.isUnique)
            animations.removeAll { it::class == animation::class }

        animations.add(animation)
    }

    inline fun <reified T : WorldObjectAnimation> cancelEntityAnimation() {
        cancelEntityAnimation(T::class)
    }

    open fun <T : WorldObjectAnimation> cancelEntityAnimation(type: KClass<T>) {
        animations.removeAll { it::class == type }
    }

    open fun onAddToWorld() {}

    open fun onRemoveFromWorld() {}

    open fun onAddToPhysics() {}

    open fun onRemoveFromPhysics() {}

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is WorldObject)
            return false

        return id == other.id
    }
}