package com.cozmicgames.entities.worldObjects.animations

import com.littlekt.graphics.Color
import kotlin.time.Duration.Companion.days

class DeadAnimation : WorldObjectAnimation(10000.0.days, DEAD_COLOR, 1.0f) {
    companion object {
        private val DEAD_COLOR = Color(0.7f, 0.9f, 0.9f, 1.0f)
    }

    override fun getFactor(progress: Float): Float {
        return 0.0f
    }
}