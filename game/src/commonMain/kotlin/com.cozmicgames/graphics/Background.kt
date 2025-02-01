package com.cozmicgames.graphics

import com.cozmicgames.Game
import com.littlekt.graphics.g2d.TextureSlice
import com.littlekt.util.seconds
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.time.Duration

class Background(private val texture: TextureSlice) {
    companion object {
        private const val TILE_SIZE = 1024.0f
        private const val PARALLAX_FACTOR = 0.3f
    }

    fun render(delta: Duration, renderer: Renderer) {
        val scrollX = (Game.upTime.seconds * 25.0f) % TILE_SIZE
        val scrollY = (Game.upTime.seconds * 10.0f) % TILE_SIZE

        renderer.submit(RenderLayers.BACKGROUND) { batch ->
            val camera = Game.player.camera

            val startX = (camera.position.x * PARALLAX_FACTOR) - camera.virtualWidth * 0.5f
            val startY = (camera.position.y * PARALLAX_FACTOR) - camera.virtualHeight * 0.5f

            val alignedStartX = floor(startX / TILE_SIZE) * TILE_SIZE - TILE_SIZE * 2
            val alignedStartY = floor(startY / TILE_SIZE) * TILE_SIZE - TILE_SIZE * 2

            val tilesX = ceil(camera.virtualWidth / TILE_SIZE).toInt() + 4
            val tilesY = ceil(camera.virtualHeight / TILE_SIZE).toInt() + 4

            repeat(tilesX) { xx ->
                repeat(tilesY) { yy ->
                    batch.draw(texture, scrollX + alignedStartX + xx * TILE_SIZE, scrollY + alignedStartY + yy * TILE_SIZE, width = TILE_SIZE, height = TILE_SIZE)
                }
            }
        }
    }
}