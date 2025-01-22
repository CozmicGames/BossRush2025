package com.cozmicgames.bosses.boss3

import kotlin.time.Duration

sealed interface ArmMovement {
    fun updateParts(delta: Duration, arm: Arm)

    fun reset() {}
}

