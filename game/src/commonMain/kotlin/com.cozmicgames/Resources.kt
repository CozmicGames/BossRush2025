package com.cozmicgames

import com.littlekt.Context
import com.littlekt.Releasable
import com.littlekt.file.vfs.readTexture
import com.littlekt.graphics.Texture

class Resources : Releasable {
    lateinit var testPlayer: Texture
    lateinit var testEnemy: Texture
    lateinit var testBackgroundTexture: Texture
    lateinit var testEnergyBall: Texture

    suspend fun load(context: Context) {
        testPlayer = context.resourcesVfs["textures/test.png"].readTexture()
        testEnemy = context.resourcesVfs["textures/enemy.png"].readTexture()
        testBackgroundTexture = context.resourcesVfs["textures/test_bg.png"].readTexture()
        testEnergyBall = context.resourcesVfs["textures/energy_ball.png"].readTexture()
    }

    override fun release() {
        testPlayer.release()
        testEnemy.release()
        testBackgroundTexture.release()
        testEnergyBall.release()
    }
}