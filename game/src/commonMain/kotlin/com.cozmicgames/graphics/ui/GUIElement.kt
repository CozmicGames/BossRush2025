package com.cozmicgames.graphics.ui

import com.cozmicgames.graphics.RenderLayers
import com.cozmicgames.graphics.Renderer
import kotlin.time.Duration

abstract class GUIElement {
    open var layer = RenderLayers.UI

    var x: Float
        set(value) {
            getX = { value }
        }
        get() = getX()

    var y: Float
        set(value) {
            getY = { value }
        }
        get() = getY()

    var width: Float
        set(value) {
            getWidth = { value }
        }
        get() = getWidth()

    var height: Float
        set(value) {
            getHeight = { value }
        }
        get() = getHeight()

    var getX: () -> Float = { 0.0f }
        set(value) {
            field = value
            isTransformDirty = true
        }

    var getY: () -> Float = { 0.0f }
        set(value) {
            field = value
            isTransformDirty = true
        }

    var getWidth: () -> Float = { 0.0f }
        set(value) {
            field = value
            isTransformDirty = true
        }

    var getHeight: () -> Float = { 0.0f }
        set(value) {
            field = value
            isTransformDirty = true
        }

    private var isTransformDirty = true

    protected open fun updateTransform() {}

    fun render(delta: Duration, renderer: Renderer) {
        if (isTransformDirty) {
            updateTransform()
            isTransformDirty = false
        }

        renderElement(delta, renderer)
    }

    protected abstract fun renderElement(delta: Duration, renderer: Renderer)
}