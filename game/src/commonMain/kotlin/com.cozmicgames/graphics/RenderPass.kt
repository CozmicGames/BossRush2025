package com.cozmicgames.graphics

import com.littlekt.Context
import com.littlekt.Releasable
import com.littlekt.graphics.Camera
import com.littlekt.graphics.Color
import com.littlekt.graphics.g2d.SpriteBatch
import com.littlekt.graphics.webgpu.*

class RenderPass(device: Device, context: Context, preferredFormat: TextureFormat) : Releasable {
    private val batch = SpriteBatch(device, context.graphics, preferredFormat)
    private var renderPassEncoder: RenderPassEncoder? = null

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