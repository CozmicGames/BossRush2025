package com.cozmicgames

import com.littlekt.Context
import com.littlekt.Releasable
import com.littlekt.file.vfs.readTexture
import com.littlekt.graphics.Texture
import com.littlekt.graphics.g2d.TextureSlice
import com.littlekt.graphics.slice

class Resources : Releasable {
    /**
     * Test textures
     */
    lateinit var testPlayer: Texture
    lateinit var testEnemy: Texture
    lateinit var testBackgroundTexture: Texture
    lateinit var testEnergyBall: Texture


    /**
     * Boss 1 - Space octopus
     */
    lateinit var boss1tentacle: Texture
    lateinit var boss1tentacleSlices: Array<TextureSlice>

    lateinit var boss1tentacle0: Texture
    lateinit var boss1tentacle1: Texture
    lateinit var boss1tentacle2: Texture
    lateinit var boss1tentacle3: Texture
    lateinit var boss1tentacle4: Texture

    suspend fun load(context: Context) {
        testPlayer = context.resourcesVfs["textures/test.png"].readTexture()
        testEnemy = context.resourcesVfs["textures/enemy.png"].readTexture()
        testBackgroundTexture = context.resourcesVfs["textures/test_bg.png"].readTexture()
        testEnergyBall = context.resourcesVfs["textures/energy_ball.png"].readTexture()

        boss1tentacle = context.resourcesVfs["textures/boss1/tentacle.png"].readTexture()
        boss1tentacleSlices = boss1tentacle.slice(boss1tentacle.width / Constants.BOSS1_TENTACLE_PARTS, boss1tentacle.height)[0]

        boss1tentacle0 = context.resourcesVfs["textures/boss1/tentacle0.png"].readTexture()
        boss1tentacle1 = context.resourcesVfs["textures/boss1/tentacle1.png"].readTexture()
        boss1tentacle2 = context.resourcesVfs["textures/boss1/tentacle2.png"].readTexture()
        boss1tentacle3 = context.resourcesVfs["textures/boss1/tentacle3.png"].readTexture()
        boss1tentacle4 = context.resourcesVfs["textures/boss1/tentacle4.png"].readTexture()
    }

    override fun release() {
        testPlayer.release()
        testEnemy.release()
        testBackgroundTexture.release()
        testEnergyBall.release()

        boss1tentacle.release()

        boss1tentacle0.release()
        boss1tentacle1.release()
        boss1tentacle2.release()
        boss1tentacle3.release()
        boss1tentacle4.release()
    }
}