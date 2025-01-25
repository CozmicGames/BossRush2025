package com.cozmicgames.bosses.boss4

import com.cozmicgames.Game
import com.cozmicgames.graphics.RenderLayers
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.utils.toHsv
import com.littlekt.graphics.Color
import com.littlekt.graphics.Texture
import com.littlekt.math.clamp
import com.littlekt.math.geom.degrees
import com.littlekt.util.seconds
import kotlin.math.pow
import kotlin.random.Random
import kotlin.time.Duration

class Vortex {
    companion object {
        private val START_COLOR = Color.fromHex("0c0293")
        private val END_COLOR = Color.fromHex("db3ffd")

        private const val MIN_SATURATION = 0.7f
        private const val MAX_SATURATION = 1.0f

        private const val MIN_BRIGHTNESS = 0.3f
        private const val MAX_BRIGHTNESS = 1.2f

        private const val MIN_ALPHA = 0.3f
        private const val MAX_ALPHA = 0.6f

        private const val MIN_ROTATION_SPEED = 0.1f
        private const val MAX_ROTATION_SPEED = 0.2f

        private val BASE_COLOR = Color.fromHex("0a0215")
    }

    private class Layer(val layer: Int, val texture: Texture, val color: Color, val rotationSpeed: Float, val scale: Float) {
        var rotation = 0.0.degrees
    }

    var x = 0.0f
    var y = 0.0f

    private var size = 500.0f
    private val layers = arrayListOf<Layer>()

    init {
        val random = Random("Vortex".hashCode())
        val numLayers = 35

        val textures = arrayOf(
            Game.resources.vortex0,
            Game.resources.vortex1,
            Game.resources.vortex2,
            Game.resources.vortex3
        )

        repeat(numLayers) {
            val baseColor = START_COLOR.mix(END_COLOR, random.nextFloat())
            val (h, s, v) = baseColor.toHsv()
            val saturation = s * (MIN_SATURATION + (MAX_SATURATION - MIN_SATURATION) * random.nextFloat())
            val brightness = v * (MIN_BRIGHTNESS + (MAX_BRIGHTNESS - MIN_BRIGHTNESS) * random.nextFloat())
            val alpha = MIN_ALPHA + (MAX_ALPHA - MIN_ALPHA) * random.nextFloat()
            val color = Color.fromHsv(h, saturation, brightness, alpha)

            val rotationSpeed = MIN_ROTATION_SPEED + (MAX_ROTATION_SPEED - MIN_ROTATION_SPEED) * random.nextFloat()
            val scale = (it / numLayers.toFloat()).pow(1.5f) * 1.2f + 0.05f

            val textureIndex = (random.nextInt(textures.size) * (it / (numLayers - 1).toFloat()).pow(2.0f)).toInt().clamp(0, textures.lastIndex)

            val texture = textures[textureIndex]

            layers += Layer(it, texture, color, rotationSpeed, scale)
        }
    }

    fun render(delta: Duration, renderer: Renderer) {
        if (size > 0.0f) {
            renderer.submit(RenderLayers.VORTEX_BEGIN) {
                val baseSize = size * 0.7f
                it.draw(Game.resources.vortexBase, x, y, baseSize * 0.5f, baseSize * 0.5f, baseSize, baseSize, color = BASE_COLOR)
            }

            for (layer in layers) {
                layer.rotation += 360.0.degrees * layer.rotationSpeed * delta.seconds
                val layerSize = this.size * layer.scale

                renderer.submit(RenderLayers.VORTEX_BEGIN + layer.layer) {
                    it.draw(layer.texture, x, y, layerSize * 0.5f, layerSize * 0.5f, layerSize, layerSize, rotation = layer.rotation, color = layer.color)
                }
            }
        }
    }
}