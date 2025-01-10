package com.cozmicgames.utils

import com.littlekt.math.PI2_F
import com.littlekt.math.PI_F
import com.littlekt.math.geom.Angle
import com.littlekt.math.geom.radians


fun normalizeAngle(angle: Float): Float {
    var normalized = angle % PI2_F
    if (normalized < 0.0f) {
        normalized += PI2_F
    }
    return normalized
}

fun shortestAngleDifference(angleFrom: Float, angleTo: Float): Float {
    val normalizedFrom = normalizeAngle(angleFrom)
    val normalizedTo = normalizeAngle(angleTo)
    var diff = (normalizedTo - normalizedFrom + PI_F) % PI2_F - PI_F
    if (diff > PI_F)
        diff -= PI2_F
    else if (diff < -PI_F)
        diff += PI2_F
    return diff
}

fun lerpAngle(from: Angle, to: Angle, alpha: Float): Angle {
    val radiansFrom = from.radians
    val radiansTo = to.radians
    return (radiansFrom + (radiansTo - radiansFrom) * alpha).radians
}

fun lerp(from: Float, to: Float, alpha: Float): Float {
    return from + (to - from) * alpha
}