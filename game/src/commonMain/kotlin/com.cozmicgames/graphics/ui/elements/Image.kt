package com.cozmicgames.graphics.ui.elements

import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.GUIElement
import com.littlekt.graphics.Color
import com.littlekt.graphics.Texture
import com.littlekt.graphics.g2d.TextureSlice
import com.littlekt.graphics.slice
import com.littlekt.math.geom.degrees
import kotlin.time.Duration

open class Image(var texture: TextureSlice, val color: Color = Color.WHITE) : GUIElement() {
    constructor(texture: Texture, color: Color = Color.WHITE) : this(texture.slice(), color)

    var rotation = 0.0.degrees

    override fun renderElement(delta: Duration, renderer: Renderer) {
        renderer.submit(layer) {
            it.draw(texture, x, y, width = width, height = height, rotation = rotation, color = color)
        }
    }
}