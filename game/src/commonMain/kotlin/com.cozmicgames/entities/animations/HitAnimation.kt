package com.cozmicgames.entities.animations

import com.littlekt.graphics.Color
import kotlin.time.Duration.Companion.seconds

class HitAnimation : EntityAnimation(0.2.seconds, Color.RED, 1.05f) {
    override fun getFactor(progress: Float): Float {
        return progress * progress
    }
}