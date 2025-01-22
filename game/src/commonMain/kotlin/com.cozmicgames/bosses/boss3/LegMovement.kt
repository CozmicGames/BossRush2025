package com.cozmicgames.bosses.boss3

import kotlin.time.Duration

sealed interface LegMovement {
    fun updateParts(delta: Duration, leg: Leg)

    fun reset() {}
}

