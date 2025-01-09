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
     * Player
     */
    lateinit var playerShipBaseStill: Texture
    lateinit var playerShipBaseSlow: Texture
    lateinit var playerShipBaseFast: Texture
    lateinit var playerShipTemplateDark: Texture
    lateinit var playerShipTemplateMain: Texture
    lateinit var playerShipTemplateLight: Texture


    /**
     * Boss 1 - Space octopus
     */
    lateinit var boss1background: Texture
    lateinit var boss1head: Texture
    lateinit var boss1beak: Texture
    lateinit var boss1tentacle: Texture
    lateinit var boss1tentacleSlices: Array<TextureSlice>


    suspend fun load(context: Context) {
        testPlayer = context.resourcesVfs["textures/test.png"].readTexture()
        testEnemy = context.resourcesVfs["textures/enemy.png"].readTexture()
        testBackgroundTexture = context.resourcesVfs["textures/test_bg.png"].readTexture()
        testEnergyBall = context.resourcesVfs["textures/energy_ball.png"].readTexture()

        playerShipBaseStill = context.resourcesVfs["textures/player/player_ship_base_still.png"].readTexture()
        playerShipBaseSlow = context.resourcesVfs["textures/player/player_ship_base_slow.png"].readTexture()
        playerShipBaseFast = context.resourcesVfs["textures/player/player_ship_base_fast.png"].readTexture()
        playerShipTemplateDark = context.resourcesVfs["textures/player/player_ship_template_dark.png"].readTexture()
        playerShipTemplateMain = context.resourcesVfs["textures/player/player_ship_template_main.png"].readTexture()
        playerShipTemplateLight = context.resourcesVfs["textures/player/player_ship_template_light.png"].readTexture()

        boss1background = context.resourcesVfs["textures/boss1/background.png"].readTexture()
        boss1head = context.resourcesVfs["textures/boss1/head.png"].readTexture()
        boss1beak = context.resourcesVfs["textures/boss1/beak.png"].readTexture()
        boss1tentacle = context.resourcesVfs["textures/boss1/tentacle.png"].readTexture()
        boss1tentacleSlices = boss1tentacle.slice(boss1tentacle.width / Constants.BOSS1_TENTACLE_PARTS, boss1tentacle.height)[0]
    }

    override fun release() {
        testPlayer.release()
        testEnemy.release()
        testBackgroundTexture.release()
        testEnergyBall.release()

        playerShipBaseStill.release()
        playerShipBaseSlow.release()
        playerShipBaseFast.release()
        playerShipTemplateDark.release()
        playerShipTemplateMain.release()
        playerShipTemplateLight.release()

        boss1background.release()
        boss1head.release()
        boss1beak.release()
        boss1tentacle.release()
    }
}