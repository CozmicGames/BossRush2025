package com.cozmicgames.entities.worldObjects.animations

import com.littlekt.graphics.Color
import com.littlekt.graphics.MutableColor
import com.littlekt.util.seconds
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

abstract class WorldObjectAnimation(val duration: Duration, val startColor: Color, val startScale: Float) {
    open val isUnique get() = true

    private var timer = 0.0.seconds

    val color = MutableColor(startColor)

    var scale = startScale
        private set

    abstract fun getFactor(progress: Float): Float

    fun update(delta: Duration, baseColor: Color): Boolean {
        timer += delta

        val factor = getFactor(timer.seconds / duration.seconds)

        startColor.mix(baseColor, factor, color)
        scale = startScale + (1.0f - startScale) * factor

        return timer >= duration
    }
}


