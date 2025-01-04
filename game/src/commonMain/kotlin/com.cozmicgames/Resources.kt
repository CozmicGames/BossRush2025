package com.cozmicgames

import com.littlekt.Context
import com.littlekt.Releasable
import com.littlekt.file.vfs.readTexture
import com.littlekt.graphics.Texture

class Resources : Releasable {
    lateinit var testTexture: Texture
    lateinit var testBackgroundTexture: Texture
    lateinit var testEnergyBall: Texture

    suspend fun load(context: Context) {
        testTexture = context.resourcesVfs["textures/test.png"].readTexture()
        testBackgroundTexture = context.resourcesVfs["textures/test_bg.png"].readTexture()
        testEnergyBall = context.resourcesVfs["textures/energy_ball.png"].readTexture()
    }

    override fun release() {
        testTexture.release()
        testBackgroundTexture.release()
        testEnergyBall.release()
    }
}