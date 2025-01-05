package com.cozmicgames.graphics

import com.littlekt.graphics.g2d.SpriteBatch

class Renderer {
    private class Renderable(val layer: Int, val block: (SpriteBatch) -> Unit)

    private val renderables = arrayListOf<Renderable>()

    fun submit(layer: Int, block: (SpriteBatch) -> Unit) {
        renderables.add(Renderable(layer, block))
    }

    fun render(batch: SpriteBatch) {
        renderables.sortBy { it.layer }
        renderables.forEach { it.block(batch) }
        renderables.clear()
    }

    fun clear() {
        renderables.clear()
    }
}