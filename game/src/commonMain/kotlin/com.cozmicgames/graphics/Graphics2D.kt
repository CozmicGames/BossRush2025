package com.cozmicgames.graphics

import com.littlekt.Context
import com.littlekt.Releasable
import com.littlekt.graphics.Color
import com.littlekt.graphics.webgpu.*
import com.littlekt.util.datastructure.Pool
import com.littlekt.util.viewport.ExtendViewport
import com.littlekt.util.viewport.Viewport

class Graphics2D(private val context: Context) : Releasable {
    private inner class FrameContext(
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
    private val renderPassPool = Pool(1) { RenderPass(device, context, preferredFormat) }
    private val allocatedRenderPasses = arrayListOf<RenderPass>()

    private var frameContext: FrameContext? = null

    val width get() = context.graphics.width
    val height get() = context.graphics.height

    val mainViewport: Viewport

    init {
        context.graphics.configureSurface(
            TextureUsage.RENDER_ATTACHMENT,
            preferredFormat,
            PresentMode.FIFO,
            surfaceCapabilities.alphaModes[0]
        )

        mainViewport = ExtendViewport(context.graphics.width, context.graphics.height)
    }

    fun resize(width: Int, height: Int) {
        mainViewport.update(width, height)
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

    fun beginRenderPass(view: TextureView, clearColor: Color): RenderPass {
        val frameContext = frameContext ?: throw IllegalStateException("beginFrame must be called first")

        val renderPass = renderPassPool.alloc()
        renderPass.begin(frameContext.commandEncoder, view, clearColor)
        allocatedRenderPasses += renderPass

        return renderPass
    }

    fun beginMainRenderPass(clearColor: Color = Color.DARK_GRAY): RenderPass {
        val frameContext = frameContext ?: throw IllegalStateException("beginFrame must be called first")
        return beginRenderPass(frameContext.frame, clearColor)
    }

    fun endFrame() {
        val frameContext = frameContext ?: return

        renderPassPool.free(allocatedRenderPasses)
        allocatedRenderPasses.clear()

        val commandBuffer = frameContext.commandEncoder.finish()

        device.queue.submit(commandBuffer)
        context.graphics.surface.present()

        commandBuffer.release()
        frameContext.release()
    }

    override fun release() {
        renderPassPool.allocMultiple(renderPassPool.itemsInPool) {
            it.forEach(RenderPass::release)
        }

        device.release()
    }
}