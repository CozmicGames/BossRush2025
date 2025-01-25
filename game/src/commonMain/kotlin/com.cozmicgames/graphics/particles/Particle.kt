package com.cozmicgames.graphics.particles

import com.littlekt.graphics.MutableColor
import com.littlekt.math.geom.degrees
import kotlin.time.Duration.Companion.seconds

class Particle {
    var lifeTime = 0.0.seconds
    var life = 0.0.seconds
    var x = 0.0f
    var y = 0.0f
    var directionX = 0.0f
    var directionY = 0.0f
    var speed = 0.0f
    var size = 0.0f
    var rotation = 0.0.degrees
    val color = MutableColor()
}