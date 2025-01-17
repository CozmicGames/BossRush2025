package com.cozmicgames.graphics

import com.cozmicgames.Game
import com.cozmicgames.utils.Rectangle
import com.littlekt.Context
import com.littlekt.Releasable
import com.littlekt.graphics.Camera
import com.littlekt.graphics.Color
import com.littlekt.graphics.g2d.SpriteBatch
import com.littlekt.graphics.g2d.shape.ShapeRenderer
import com.littlekt.graphics.webgpu.*

class RenderPass(device: Device, context: Context, preferredFormat: TextureFormat) : Releasable {
    private val batch = SpriteBatch(device, context.graphics, preferredFormat, cameraDynamicSize = 256)
    private val shapeRenderer by lazy { ShapeRenderer(batch) }
    private var renderPassEncoder: RenderPassEncoder? = null
    private val renderer = Renderer(this)
    private val scissorStack = ScissorStack()

    fun begin(commandEncoder: CommandEncoder, view: TextureView, clearColor: Color) {
        require(renderPassEncoder == null) { "Render pass already started" }

        renderPassEncoder = commandEncoder.beginRenderPass(
            RenderPassDescriptor(
                listOf(
                    RenderPassColorAttachmentDescriptor(
                        view = view,
                        loadOp = LoadOp.CLEAR,
                        storeOp = StoreOp.STORE,
                        clearColor = clearColor
                    )
                )
            )
        )

        batch.begin()
    }

    fun render(camera: Camera, block: (SpriteBatch) -> Unit) {
        require(renderPassEncoder != null) { "Render pass not started" }

        val encoder = renderPassEncoder ?: return

        batch.viewProjection = camera.viewProjection

        block(batch)

        batch.flush(encoder)
    }

    fun render(camera: Camera, block: (Renderer) -> Unit) {
        render(camera) { batch: SpriteBatch ->
            block(renderer)
            renderer.render(batch)
        }
    }

    fun renderShapes(camera: Camera, block: (ShapeRenderer) -> Unit) {
        require(renderPassEncoder != null) { "Render pass not started" }

        val encoder = renderPassEncoder ?: return

        batch.viewProjection = camera.viewProjection

        block(shapeRenderer)

        batch.flush(encoder)
    }

    fun pushScissor(x: Int, y: Int, width: Int, height: Int) {
        require(renderPassEncoder != null) { "Render pass not started" }

        val encoder = renderPassEncoder ?: return

        scissorStack.push(Rectangle(x.toFloat(), y.toFloat(), width.toFloat(), height.toFloat()))

        val current = scissorStack.currentScissorRectangle

        val scissorX = (current?.x ?: 0.0f).toInt()
        val scissorY = (current?.y ?: 0.0f).toInt()
        val scissorWidth = (current?.width ?: Game.graphics.width).toInt()
        val scissorHeight = (current?.height ?: Game.graphics.height).toInt()

        encoder.setScissorRect(scissorX, scissorY, scissorWidth, scissorHeight)
    }

    fun popScissor() {
        require(renderPassEncoder != null) { "Render pass not started" }

        val encoder = renderPassEncoder ?: return

        scissorStack.pop()

        val current = scissorStack.currentScissorRectangle

        val scissorX = (current?.x ?: 0.0f).toInt()
        val scissorY = (current?.y ?: 0.0f).toInt()
        val scissorWidth = (current?.width ?: Game.graphics.width).toInt()
        val scissorHeight = (current?.height ?: Game.graphics.height).toInt()

        encoder.setScissorRect(scissorX, scissorY, scissorWidth, scissorHeight)
    }

    fun end() {
        require(renderPassEncoder != null) { "Render pass not started" }

        val encoder = renderPassEncoder ?: return

        batch.end()

        encoder.end()
        encoder.release()

        renderPassEncoder = null
    }

    override fun release() {
        batch.release()
    }
}