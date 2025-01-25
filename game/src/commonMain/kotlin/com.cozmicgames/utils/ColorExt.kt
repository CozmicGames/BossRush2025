package com.cozmicgames.utils

import com.littlekt.graphics.Color

fun Color.toHsv(): Triple<Float, Float, Float> {
    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)

    val h = when (max) {
        min -> 0.0f
        r -> 60 * ((g - b) / (max - min) + 0)
        g -> 60 * ((b - r) / (max - min) + 2)
        else -> 60 * ((r - g) / (max - min) + 4)
    }

    val s = if (max == 0.0f) 0.0f else (max - min) / max
    val v = max

    return Triple(h, s, v)
}
