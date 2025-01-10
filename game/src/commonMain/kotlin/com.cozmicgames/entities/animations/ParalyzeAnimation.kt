package com.cozmicgames.entities.animations

import com.littlekt.graphics.Color
import com.littlekt.graphics.MutableColor
import kotlin.math.pow
import kotlin.time.Duration

class ParalyzeAnimation(duration: Duration, strength: Float = 1.0f) : EntityAnimation(duration, MutableColor(Color.WHITE).mix(PARALYZED_COLOR, strength), 1.0f) {
    companion object {
        private val PARALYZED_COLOR = Color(0.3f, 0.4f, 1.0f, 1.0f)
    }

    override fun getFactor(progress: Float): Float {
        return progress.pow(3.0f)
    }
}