package com.cozmicgames.bosses.boss3

import com.cozmicgames.Game
import com.cozmicgames.utils.lerpAngle
import com.littlekt.math.geom.degrees
import com.littlekt.util.seconds
import kotlin.math.sin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

interface ClawMovement {
    fun updateClaw(delta: Duration, claw: Claw)
}
