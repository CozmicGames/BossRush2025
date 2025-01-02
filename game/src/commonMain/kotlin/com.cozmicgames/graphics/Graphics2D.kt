package com.cozmicgames.graphics

import com.littlekt.Context
import com.littlekt.Releasable
import com.littlekt.graphics.Color
import com.littlekt.graphics.HAlign
import com.littlekt.graphics.g2d.SpriteBatch
import com.littlekt.graphics.g2d.use
import com.littlekt.graphics.webgpu.*
import com.littlekt.resources.Fonts
import com.littlekt.util.viewport.ExtendViewport
import com.littlekt.util.viewport.Viewport

class Graphics2D(private val context: Context) : Releasable {
    private class FrameContext(
        val commandEncoder: CommandEncoder,
        val frame: TextureView,
        val swapChainTexture: WebGPUTexture
    ) : Releasable {
        override fun release() {
            frame.release()
            swapChainTexture.release()
            commandEncoder.release()
        }
    }

    private val device = context.graphics.device
    private val surfaceCapabilities = context.graphics.surfaceCapabilities
    private val preferredFormat = context.graphics.preferredFormat
    private val batch: SpriteBatch
    private val viewport: Viewport

    private var frameContext: FrameContext? = null

    val camera get() = viewport.camera

    init {
        context.graphics.configureSurface(
            TextureUsage.RENDER_ATTACHMENT,
            preferredFormat,
            PresentMode.FIFO,
            surfaceCapabilities.alphaModes[0]
        )

        batch = SpriteBatch(device, context.graphics, preferredFormat)
        viewport = ExtendViewport(960, 540)
    }

    fun resize(width: Int, height: Int) {
        viewport.update(width, height)
        context.graphics.configureSurface(
            TextureUsage.RENDER_ATTACHMENT,
            preferredFormat,
            PresentMode.FIFO,
            surfaceCapabilities.alphaModes[0]
        )
    }

    fun beginFrame() {
        val surfaceTexture = context.graphics.surface.getCurrentTexture()

        when (val status = surfaceTexture.status) {
            TextureStatus.SUCCESS -> {
                // all good, could check for `surfaceTexture.suboptimal` here.
            }

            TextureStatus.TIMEOUT,
            TextureStatus.OUTDATED,
            TextureStatus.LOST -> {
                surfaceTexture.texture?.release()
                context.logger.info { "getCurrentTexture status=$status" }
                return
            }

            else -> {
                context.logger.fatal { "getCurrentTexture status=$status" }
                context.close()
                return
            }
        }

        val swapChainTexture = checkNotNull(surfaceTexture.texture)
        val frame = swapChainTexture.createView()

        frameContext = FrameContext(
            commandEncoder = device.createCommandEncoder(),
            frame = frame,
            swapChainTexture = swapChainTexture
        )
    }

    fun drawFrame() {
        val frameContext = frameContext ?: return

        val renderPassEncoder =
            frameContext.commandEncoder.beginRenderPass(
                desc =
                RenderPassDescriptor(
                    listOf(
                        RenderPassColorAttachmentDescriptor(
                            view = frameContext.frame,
                            loadOp = LoadOp.CLEAR,
                            storeOp = StoreOp.STORE,
                            clearColor =
                            if (preferredFormat.srgb) Color.DARK_GRAY.toLinear()
                            else Color.DARK_GRAY
                        )
                    )
                )
            )
        camera.update()

        batch.use(renderPassEncoder, camera.viewProjection) {
            Fonts.default.draw(it, "Hello LittleKt!", 0f, 0f, align = HAlign.CENTER)
        }
        renderPassEncoder.end()
        renderPassEncoder.release()
    }

    fun endFrame() {
        val frameContext = frameContext ?: return

        val commandBuffer = frameContext.commandEncoder.finish()

        device.queue.submit(commandBuffer)
        context.graphics.surface.present()

        commandBuffer.release()
        frameContext.release()
    }

    override fun release() {
        batch.release()
        device.release()
    }
}