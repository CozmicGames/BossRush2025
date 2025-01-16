package com.cozmicgames.utils

import com.littlekt.math.PI_F
import com.littlekt.math.clamp
import kotlin.math.asin
import kotlin.math.pow
import kotlin.math.sin

enum class Easing(private val f: (Float) -> Float) {
    LINEAR({ it }),
    QUAD_IN({ it * it }),
    QUAD_OUT({ it * (2.0f - it) }),
    QUAD_IN_OUT({ if (it < 0.5f) 2.0f * it * it else -1.0f + (4.0f - 2.0f * it) * it }),
    CUBIC_IN({ it * it * it }),
    CUBIC_OUT({ (it - 1.0f) * (it - 1.0f) * (it - 1.0f) + 1.0f }),
    CUBIC_IN_OUT({ if (it < 0.5f) 4.0f * it * it * it else (it - 1.0f) * (2.0f * it - 2.0f) * (2.0f * it - 2.0f) + 1.0f }),
    QUART_IN({ it * it * it * it }),
    QUART_OUT({ 1.0f - (it - 1.0f) * (it - 1.0f) * (it - 1.0f) * (it - 1.0f) }),
    QUART_IN_OUT({ if (it < 0.5f) 8.0f * it * it * it * it else 1.0f - 8.0f * (it - 1.0f) * (it - 1.0f) * (it - 1.0f) * (it - 1.0f) }),
    BOUNCE_IN({ bounceIn(it) }),
    BOUNCE_OUT({ bounceOut(it) }),
    BOUNCE_IN_OUT({ bounceInOut(it) }),
    ELASTIC_IN({ elasticIn(it) }),
    ELASTIC_OUT({ elasticOut(it) }),
    ELASTIC_IN_OUT({ elasticInOut(it) });

    operator fun invoke(value: Float) = f(value).clamp(0.0f, 1.0f)
}


private fun bounceIn(t: Float): Float {
    return 1.0f - bounceOut(1.0f - t)
}

private fun bounceOut(t: Float): Float {
    return when {
        t < 1 / 2.75 -> (7.5625 * t * t).toFloat()
        t < 2 / 2.75 -> {
            val o = t - 1.5 / 2.75
            (7.5625 * o * o + 0.75).toFloat()
        }

        t < 2.5 / 2.75 -> {
            val o = t - 2.25 / 2.75
            (7.5625 * o * o + 0.9375).toFloat()
        }

        else -> {
            val o = t - 2.625 / 2.75
            (7.5625 * o * o + 0.984375).toFloat()
        }
    }
}

private fun bounceInOut(t: Float): Float {
    if (t < 0.5f) {
        val tt = 1.0f - 2.0f * t
        return when {
            tt < (1.0f / 2.75f) -> (1.0f - (7.5625f * tt * tt)) * 0.5f
            tt < (2.0f / 2.75f) -> (1.0f - (7.5625f * (tt - (1.5f / 2.75f)) * (tt - (1.5f / 2.75f)) + 0.75f)) * 0.5f
            tt < (2.5f / 2.75f) -> (1.0f - (7.5625f * (tt - (2.25f / 2.75f)) * (tt - (2.25f / 2.75f)) + 0.9375f)) * 0.5f
            else -> (1.0f - (7.5625f * (tt - (2.625f / 2.75f)) * (tt - (2.625f / 2.75f)) + 0.984375f)) * 0.5f
        }
    } else {
        val tt = 2.0f * t - 1.0f
        return when {
            tt < (1.0f / 2.75f) -> 0.5f + (7.5625f * tt * tt) * 0.5f
            tt < (2.0f / 2.75f) -> 0.5f + (7.5625f * (tt - (1.5f / 2.75f)) * (tt - (1.5f / 2.75f)) + 0.75f) * 0.5f
            tt < (2.5f / 2.75f) -> 0.5f + (7.5625f * (tt - (2.25f / 2.75f)) * (tt - (2.25f / 2.75f)) + 0.9375f) * 0.5f
            else -> 0.5f + (7.5625f * (tt - (2.625f / 2.75f)) * (tt - (2.625f / 2.75f)) + 0.984375f) * 0.5f
        }
    }
}

private fun elasticIn(t: Float): Float {
    if (t == 0.0f || t == 1.0f) return t
    val pi2 = PI_F * 2.0f
    val s = 0.3f / pi2 * asin(1.0f)
    val o = t - 1.0f
    return (-2.0f).pow(10.0f * o) * sin((o - s) * pi2 / 0.3f)
}

private fun elasticOut(t: Float): Float {
    if (t == 0.0f || t == 1.0f) return t
    val pi2 = PI_F * 2.0f
    val s = 0.3f / pi2 * asin(1.0f)
    return (2.0f.pow(-10.0f * t) * sin((t - s) * pi2 / 0.3f) + 1.0f)
}

private fun elasticInOut(t: Float): Float {
    val pi2 = PI_F * 2.0f
    val s = 0.45f / pi2 * asin(1.0f)
    var o = t * 2.0f
    return if (o < 1.0f) {
        o -= 1.0f
        -0.5f * (2.0f.pow(10.0f * o) * sin((o - s) * pi2 / 0.45f))
    } else {
        o -= 1.0f
        2.0f.pow(-10.0f * o) * sin((o - s) * pi2 / 0.45f) * 0.5f + 1.0f
    }
}