package com.cozmicgames.weapons

import com.cozmicgames.Game
import com.littlekt.graphics.Color
import com.littlekt.graphics.g2d.SpriteBatch

enum class AreaEffectType {
    BAIT {
        private val BASE_COLOR = Color.fromHex("ffd59b")

        override fun render(batch: SpriteBatch, x: Float, y: Float, radius: Float, alpha: Float) {
            val texture = when (radius) {
                in 0.0f..127.0f -> Game.resources.shockwave0
                in 128.0f..255.0f -> Game.resources.shockwave1
                else -> Game.resources.shockwave2
            }

            val color = Color(BASE_COLOR.r, BASE_COLOR.g, BASE_COLOR.b, alpha * 0.6f)
            batch.draw(texture, x, y, originX = radius, originY = radius, width = radius * 2.0f, height = radius * 2.0f, color = color)
        }
    },
    SHOCKWAVE {
        private val BASE_COLOR = Color.fromHex("fdd2ed")

        override fun render(batch: SpriteBatch, x: Float, y: Float, radius: Float, alpha: Float) {
            val texture = when (radius) {
                in 0.0f..127.0f -> Game.resources.shockwave0
                in 128.0f..255.0f -> Game.resources.shockwave1
                else -> Game.resources.shockwave2
            }

            val color = Color(BASE_COLOR.r, BASE_COLOR.g, BASE_COLOR.b, alpha * 0.6f)
            batch.draw(texture, x, y, originX = radius, originY = radius, width = radius * 2.0f, height = radius * 2.0f, color = color)
        }
    },
    SHOCKWAVE_WITH_DAMAGE {
        private val BASE_COLOR = Color.fromHex("c42430")

        override fun render(batch: SpriteBatch, x: Float, y: Float, radius: Float, alpha: Float) {
            val texture = when (radius) {
                in 0.0f..127.0f -> Game.resources.shockwave0
                in 128.0f..255.0f -> Game.resources.shockwave1
                else -> Game.resources.shockwave2
            }

            val color = Color(BASE_COLOR.r, BASE_COLOR.g, BASE_COLOR.b, alpha * 0.6f)
            batch.draw(texture, x, y, originX = radius, originY = radius, width = radius * 2.0f, height = radius * 2.0f, color = color)
        }
    },
    GRAVITY_WAVE {
        private val BASE_COLOR = Color.fromHex("33984b")

        override fun render(batch: SpriteBatch, x: Float, y: Float, radius: Float, alpha: Float) {
            val texture = when (radius) {
                in 0.0f..127.0f -> Game.resources.shockwave0
                in 128.0f..255.0f -> Game.resources.shockwave1
                else -> Game.resources.shockwave2
            }

            val color = Color(BASE_COLOR.r, BASE_COLOR.g, BASE_COLOR.b, alpha * 0.6f)
            batch.draw(texture, x, y, originX = radius, originY = radius, width = radius * 2.0f, height = radius * 2.0f, color = color)
        }
    };

    abstract fun render(batch: SpriteBatch, x: Float, y: Float, radius: Float, alpha: Float)
}