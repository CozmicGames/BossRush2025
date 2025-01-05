package com.cozmicgames.entities.animations

import com.littlekt.graphics.Color
import com.littlekt.graphics.MutableColor
import com.littlekt.util.seconds
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

abstract class EntityAnimation(val duration: Duration, val startColor: Color, val startScale: Float) {
    private var progress = 0.0.seconds

    val color = MutableColor(startColor)

    var scale = startScale
        private set

    abstract fun getFactor(progress: Float): Float

    fun update(delta: Duration): Boolean {
        progress += delta

        val factor = getFactor(progress.seconds / duration.seconds)

        startColor.mix(Color.WHITE, factor, color)
        scale = startScale + (1.0f - startScale) * factor

        return progress >= duration
    }
}