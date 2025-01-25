package com.cozmicgames.graphics.particles

import kotlin.time.Duration

interface ParticleUpdater {
    fun update(delta: Duration, particles: List<Particle>)
}