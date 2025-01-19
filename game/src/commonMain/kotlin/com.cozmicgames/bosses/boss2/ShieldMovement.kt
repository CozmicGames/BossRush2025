package com.cozmicgames.bosses.boss2

import com.cozmicgames.utils.lerp
import kotlin.time.Duration

sealed interface ShieldMovement {
    fun updateMouth(delta: Duration, shield: Shield)

}

class IdleShieldMovement : ShieldMovement {
    override fun updateMouth(delta: Duration, shield: Shield) {
        shield.intensity = lerp(shield.intensity, 1.0f, 0.1f)
    }
}

class DeactivatedShieldMovement : ShieldMovement {
    override fun updateMouth(delta: Duration, shield: Shield) {
        shield.intensity = lerp(shield.intensity, 0.0f, 0.1f)
    }
}
