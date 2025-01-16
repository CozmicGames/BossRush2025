package com.cozmicgames.graphics.ui.elements

import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.GUIElement
import com.littlekt.graphics.Texture
import com.littlekt.graphics.g2d.TextureSlice
import com.littlekt.graphics.slice
import com.littlekt.math.geom.degrees
import kotlin.time.Duration

open class Image(var texture: TextureSlice) : GUIElement() {
    constructor(texture: Texture) : this(texture.slice())

    var rotation = 0.0.degrees

    override fun renderElement(delta: Duration, renderer: Renderer) {
        renderer.submit(layer) {
            it.draw(texture, x, y, width = width, height = height, rotation = rotation)
        }
    }
}