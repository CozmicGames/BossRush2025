package com.cozmicgames.entities

import com.cozmicgames.entities.animations.EntityAnimation
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.physics.Collider
import com.littlekt.graphics.Color
import com.littlekt.graphics.MutableColor
import com.littlekt.math.geom.degrees
import kotlin.reflect.KClass
import kotlin.time.Duration

abstract class Entity(val id: String) {
    var x = 0.0f
    var y = 0.0f
    var rotation = 0.0.degrees

    open val collider: Collider? = null

    private val animations = arrayListOf<EntityAnimation>()
    val color = MutableColor(Color.WHITE)
    var scale = 1.0f

    fun update(delta: Duration) {
        val animationsToRemove = arrayListOf<EntityAnimation>()

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

        updateEntity(delta)
    }

    protected abstract fun updateEntity(delta: Duration)

    abstract fun render(renderer: Renderer)

    open fun addEntityAnimation(animation: EntityAnimation) {
        if (animation.isUnique)
            animations.removeAll { it::class == animation::class }

        animations.add(animation)
    }

    inline fun <reified T : EntityAnimation> cancelEntityAnimation() {
        cancelEntityAnimation(T::class)
    }

    open fun <T : EntityAnimation> cancelEntityAnimation(type: KClass<T>) {
        animations.removeAll { it::class == type }
    }

    open fun onAddToEntities() {}

    open fun onRemoveFromEntities() {}

    open fun onAddToPhysics() {}

    open fun onRemoveFromPhysics() {}

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Entity)
            return false

        return id == other.id
    }
}