package com.cozmicgames.weapons

import com.cozmicgames.Game
import com.littlekt.graphics.Color
import com.littlekt.graphics.g2d.SpriteBatch

enum class AreaEffectType {
    SHOCKWAVE {
        private val BASE_COLOR = Color(0.6f, 0.7f, 0.9f, 1.0f)

        override fun render(batch: SpriteBatch, x: Float, y: Float, radius: Float, strength: Float) {
            val texture = when (radius) {
                in 0.0f..127.0f -> Game.resources.shockwave0
                in 128.0f..255.0f -> Game.resources.shockwave1
                else -> Game.resources.shockwave2
            }

            val color = Color(BASE_COLOR.r, BASE_COLOR.g, BASE_COLOR.b, strength * 0.6f)
            batch.draw(texture, x, y, originX = radius, originY = radius, width = radius * 2.0f, height = radius * 2.0f, color = color)
        }
    },
    SHOCKWAVE_WITH_DAMAGE {
        private val BASE_COLOR = Color(0.8f, 0.3f, 0.2f, 1.0f)

        override fun render(batch: SpriteBatch, x: Float, y: Float, radius: Float, strength: Float) {
            val texture = when (radius) {
                in 0.0f..127.0f -> Game.resources.shockwave0
                in 128.0f..255.0f -> Game.resources.shockwave1
                else -> Game.resources.shockwave2
            }

            val color = Color(BASE_COLOR.r, BASE_COLOR.g, BASE_COLOR.b, strength * 0.6f)
            batch.draw(texture, x, y, originX = radius, originY = radius, width = radius * 2.0f, height = radius * 2.0f, color = color)
        }
    },
    GRAVITY_WAVE {
        private val BASE_COLOR = Color(0.3f, 0.9f, 0.6f, 1.0f)

        override fun render(batch: SpriteBatch, x: Float, y: Float, radius: Float, strength: Float) {
            val texture = when (radius) {
                in 0.0f..127.0f -> Game.resources.shockwave0
                in 128.0f..255.0f -> Game.resources.shockwave1
                else -> Game.resources.shockwave2
            }

            val color = Color(BASE_COLOR.r, BASE_COLOR.g, BASE_COLOR.b, strength * 0.6f)
            batch.draw(texture, x, y, originX = radius, originY = radius, width = radius * 2.0f, height = radius * 2.0f, color = color)
        }
    };

    abstract fun render(batch: SpriteBatch, x: Float, y: Float, radius: Float, strength: Float)
}