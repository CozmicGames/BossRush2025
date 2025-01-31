package com.cozmicgames.weapons

import com.cozmicgames.Game
import com.littlekt.graphics.Color
import com.littlekt.graphics.g2d.SpriteBatch

enum class AreaEffectType {
    BAIT {
        private val BASE_COLOR = Color.fromHex("ffd59b")

        override fun render(batch: SpriteBatch, x: Float, y: Float, radius: Float, alpha: Float) {
            val color = Color(BASE_COLOR.r, BASE_COLOR.g, BASE_COLOR.b, alpha * 0.6f)
            batch.draw(Game.textures.shockwave, x, y, originX = radius, originY = radius, width = radius * 2.0f, height = radius * 2.0f, color = color)
        }
    },
    SHOCKWAVE {
        private val BASE_COLOR = Color.fromHex("5ac54f")

        override fun render(batch: SpriteBatch, x: Float, y: Float, radius: Float, alpha: Float) {
            val color = Color(BASE_COLOR.r, BASE_COLOR.g, BASE_COLOR.b, alpha * 0.6f)
            batch.draw(Game.textures.shockwave, x, y, originX = radius, originY = radius, width = radius * 2.0f, height = radius * 2.0f, color = color)
        }
    },
    SHOCKWAVE_WITH_DAMAGE {
        private val BASE_COLOR = Color.fromHex("c42430")

        override fun render(batch: SpriteBatch, x: Float, y: Float, radius: Float, alpha: Float) {
            val color = Color(BASE_COLOR.r, BASE_COLOR.g, BASE_COLOR.b, alpha * 0.6f)
            batch.draw(Game.textures.shockwave, x, y, originX = radius, originY = radius, width = radius * 2.0f, height = radius * 2.0f, color = color)
        }
    },
    GRAVITY_WAVE {
        private val BASE_COLOR = Color.fromHex("ca52c9")

        override fun render(batch: SpriteBatch, x: Float, y: Float, radius: Float, alpha: Float) {
            val color = Color(BASE_COLOR.r, BASE_COLOR.g, BASE_COLOR.b, alpha * 0.6f)
            batch.draw(Game.textures.shockwave, x, y, originX = radius, originY = radius, width = radius * 2.0f, height = radius * 2.0f, color = color)
        }
    };

    abstract fun render(batch: SpriteBatch, x: Float, y: Float, radius: Float, alpha: Float)
}