package com.cozmicgames

import com.littlekt.Context
import com.littlekt.Releasable
import com.littlekt.file.vfs.readTexture
import com.littlekt.graphics.Texture

class Resources : Releasable {
    lateinit var testTexture: Texture
    lateinit var textBackgroundTexture: Texture

    suspend fun load(context: Context) {
        testTexture = context.resourcesVfs["textures/test.png"].readTexture()
        textBackgroundTexture = context.resourcesVfs["textures/test_bg.png"].readTexture()
    }

    override fun release() {
        testTexture.release()
        textBackgroundTexture.release()
    }
}