package com.cozmicgames.graphics

import com.littlekt.graphics.g2d.SpriteBatch

class Renderer {
    private class Renderable {
        var layer = 0
        var block: (SpriteBatch) -> Unit = {}
    }

    private val renderables = arrayListOf<Renderable>()
    private var currentIndex = 0

    fun submit(layer: Int, block: (SpriteBatch) -> Unit) {
        if (currentIndex >= renderables.size)
            renderables.add(Renderable())

        renderables[currentIndex].layer = layer
        renderables[currentIndex].block = block
        currentIndex++
    }

    fun render(batch: SpriteBatch) {
        renderables.sortBy { it.layer }
        renderables.forEach { it.block(batch) }
        currentIndex = 0
    }

    fun clear() {
        currentIndex = 0
    }
}