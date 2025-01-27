package com.cozmicgames.graphics.particles

import com.cozmicgames.Game
import com.cozmicgames.utils.lerp
import com.littlekt.graphics.Color
import com.littlekt.graphics.MutableColor
import com.littlekt.graphics.g2d.SpriteBatch
import com.littlekt.input.Pointer
import com.littlekt.math.geom.Angle
import com.littlekt.math.geom.cosine
import com.littlekt.math.geom.sine
import com.littlekt.resources.Textures
import com.littlekt.util.seconds
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

abstract class ParticleEffect() {
    var id = ""

    open val duration: Duration? = null

    val particles = arrayListOf<Particle>()
    val updaters = arrayListOf<ParticleUpdater>()

    private val particlesToRemove = arrayListOf<Particle>()

    var shouldBeRemoved = false
        private set

    fun setShouldBeRemoved() {
        shouldBeRemoved = true
    }

    protected abstract fun updateSystem(delta: Duration)

    fun update(delta: Duration) {
        updateSystem(delta)

        particlesToRemove.clear()

        for (particle in particles) {
            particle.life -= delta

            particle.x += particle.directionX * particle.speed * delta.seconds
            particle.y += particle.directionY * particle.speed * delta.seconds
            if (particle.lifeTime > 0.0.seconds) {
                particle.size = lerp(particle.size, 0.0f, 1.0f - (particle.life / particle.lifeTime).toFloat())
                particle.color.a = lerp(particle.color.a, 0.0f, 1.0f - (particle.life / particle.lifeTime).toFloat())
            }

            if (particle.life <= 0.0.seconds)
                particlesToRemove.add(particle)
        }

        particles -= particlesToRemove

        for (updater in updaters)
            updater.update(delta, particles)
    }

    abstract fun writeUpdateData()

    abstract fun readUpdateData()

    fun render(batch: SpriteBatch) {
        particles.forEach {
            batch.draw(Textures.white, it.x, it.y, it.size * 0.5f, it.size * 0.5f, it.size, it.size, rotation = it.rotation, color = it.color)
        }
    }

    fun spawn(x: Float, y: Float, direction: Angle, speed: Float, lifeTime: Duration, size: Float, rotation: Angle, color: Color) {
        val particle = Particle()
        particle.x = x
        particle.y = y
        particle.directionX = direction.cosine
        particle.directionY = direction.sine
        particle.speed = speed
        particle.lifeTime = lifeTime
        particle.life = lifeTime
        particle.size = size
        particle.rotation = rotation
        particle.color.set(color)
        particles.add(particle)
    }
}