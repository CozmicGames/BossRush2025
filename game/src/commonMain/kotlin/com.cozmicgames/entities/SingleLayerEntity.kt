package com.cozmicgames.entities

import com.cozmicgames.graphics.Renderer
import com.littlekt.graphics.g2d.SpriteBatch

abstract class SingleLayerEntity(id: String) : Entity(id) {
    abstract val renderLayer: Int

    abstract fun render(batch: SpriteBatch)

    override fun render(renderer: Renderer) {
        renderer.submit(renderLayer) {
            render(it)
        }
    }
}