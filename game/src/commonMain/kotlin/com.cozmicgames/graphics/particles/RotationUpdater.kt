package com.cozmicgames.graphics.particles

import com.littlekt.math.geom.Angle
import com.littlekt.math.geom.degrees
import com.littlekt.util.seconds
import kotlin.time.Duration

class RotationUpdater(var speed: Angle = 10.0.degrees) : ParticleUpdater {
    override fun update(delta: Duration, particles: List<Particle>) {
        for (particle in particles) {
            particle.rotation += speed * delta.seconds
        }
    }
}