package com.cozmicgames.graphics.particles

import com.littlekt.graphics.MutableColor
import com.littlekt.graphics.g2d.SpriteBatch
import com.littlekt.math.geom.Angle
import com.littlekt.resources.Textures
import com.littlekt.util.seconds
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class ParticleSystem {
    val particles = arrayListOf<Particle>()
    val updaters = arrayListOf<ParticleUpdater>()

    private val particlesToRemove = arrayListOf<Particle>()

    fun update(delta: Duration) {
        particlesToRemove.clear()

        for (particle in particles) {
            particle.life -= delta

            particle.x += particle.directionX * particle.speed * delta.seconds
            particle.y += particle.directionY * particle.speed * delta.seconds
            if (particle.lifeTime > 0.0.seconds)
                particle.size = (particle.life / particle.lifeTime).toFloat()

            if (particle.life <= 0.0.seconds)
                particlesToRemove.add(particle)
        }

        particles -= particlesToRemove

        for (updater in updaters)
            updater.update(delta, particles)
    }

    fun render(batch: SpriteBatch) {
        particles.forEach {
            batch.draw(Textures.white, it.x, it.y, it.size * 0.5f, it.size * 0.5f, it.size, it.size, rotation = it.rotation, color = it.color)
        }
    }

    fun spawn(x: Float, y: Float, directionX: Float, directionY: Float, speed: Float, lifeTime: Duration, size: Float, rotation: Angle, color: MutableColor) {
        val particle = Particle()
        particle.x = x
        particle.y = y
        particle.directionX = directionX
        particle.directionY = directionY
        particle.speed = speed
        particle.lifeTime = lifeTime
        particle.life = lifeTime
        particle.size = size
        particle.rotation = rotation
        particle.color.set(color)
        particles.add(particle)
    }
}