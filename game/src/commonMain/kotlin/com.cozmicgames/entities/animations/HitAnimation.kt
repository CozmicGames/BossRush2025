package com.cozmicgames.entities.animations

import com.littlekt.graphics.Color
import kotlin.math.pow
import kotlin.time.Duration

class HitAnimation(duration: Duration) : EntityAnimation(duration, HIT_COLOR, 1.05f) {
    companion object {
        private val HIT_COLOR = Color(0.9f, 0.3f, 0.2f, 1.0f)
    }

    override fun getFactor(progress: Float): Float {
        return progress.pow(3.0f)
    }
}