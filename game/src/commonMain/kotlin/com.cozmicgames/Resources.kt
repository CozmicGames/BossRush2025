package com.cozmicgames

import com.littlekt.Context
import com.littlekt.Releasable
import com.littlekt.file.vfs.readBitmapFont
import com.littlekt.file.vfs.readTexture
import com.littlekt.graphics.Texture
import com.littlekt.graphics.g2d.NinePatch
import com.littlekt.graphics.g2d.TextureSlice
import com.littlekt.graphics.g2d.font.BitmapFont
import com.littlekt.graphics.slice

class Resources : Releasable {
    /**
     * UI
     */
    lateinit var font: BitmapFont
    lateinit var buttonNormal: Texture
    lateinit var buttonNormalNinePatch: NinePatch
    lateinit var buttonHovered: Texture
    lateinit var buttonHoveredNinePatch: NinePatch
    lateinit var buttonPressed: Texture
    lateinit var buttonPressedNinePatch: NinePatch
    lateinit var starFull: Texture
    lateinit var starEmpty: Texture
    lateinit var resultBackground: Texture
    lateinit var resultBackgroundNinePatch: NinePatch
    lateinit var resultBanner: Texture
    lateinit var returnIcon: Texture
    lateinit var playIcon: Texture


    lateinit var debug: Texture
    lateinit var debugNinePatch: NinePatch

    /**
     * Projectiles and area effects
     */
    lateinit var energyBall: Texture
    lateinit var energyBeam: Texture
    lateinit var shockwave0: Texture
    lateinit var shockwave1: Texture
    lateinit var shockwave2: Texture


    /**
     * Asteroids
     */
    lateinit var asteroid0: Texture

    /**
     * Player
     */
    lateinit var playerShipBaseStill: Texture
    lateinit var playerShipBaseSlow: Texture
    lateinit var playerShipBaseFast: Texture
    lateinit var playerShipTemplate: Texture
    lateinit var playerHealthIndicator: Texture
    lateinit var playerHealthEmptyIndicator: Texture
    lateinit var borderIndicator: Texture
    lateinit var borderIndicatorNinePatch: NinePatch

    /**
     * Boss 1 - Space octopus
     */
    lateinit var boss1background: Texture
    lateinit var boss1head: Texture
    lateinit var boss1beak: Texture
    lateinit var boss1heart: Texture
    lateinit var boss1tentacle: Texture
    lateinit var boss1tentacleSlices: Array<TextureSlice>

    suspend fun load(context: Context) {
        font = context.resourcesVfs["fonts/font.fnt"].readBitmapFont()
        buttonNormal = context.resourcesVfs["textures/ui/button_normal.png"].readTexture()
        buttonNormalNinePatch = NinePatch(buttonNormal, 16, 16, 16, 16)
        buttonHovered = context.resourcesVfs["textures/ui/button_hovered.png"].readTexture()
        buttonHoveredNinePatch = NinePatch(buttonHovered, 16, 16, 16, 16)
        buttonPressed = context.resourcesVfs["textures/ui/button_pressed.png"].readTexture()
        buttonPressedNinePatch = NinePatch(buttonPressed, 16, 16, 16, 16)
        starFull = context.resourcesVfs["textures/ui/star_full.png"].readTexture()
        starEmpty = context.resourcesVfs["textures/ui/star_empty.png"].readTexture()
        resultBackground = context.resourcesVfs["textures/ui/result_background.png"].readTexture()
        resultBackgroundNinePatch = NinePatch(resultBackground, 32, 32, 32, 32)
        resultBanner = context.resourcesVfs["textures/ui/result_banner.png"].readTexture()
        returnIcon = context.resourcesVfs["textures/ui/return_icon.png"].readTexture()
        playIcon = context.resourcesVfs["textures/ui/play_icon.png"].readTexture()

        debug = context.resourcesVfs["textures/ui/debug.png"].readTexture()
        debugNinePatch = NinePatch(debug, 4, 4, 4, 4)

        energyBall = context.resourcesVfs["textures/projectiles/energy_ball.png"].readTexture()
        energyBeam = context.resourcesVfs["textures/projectiles/energy_beam.png"].readTexture()
        shockwave0 = context.resourcesVfs["textures/projectiles/shockwave0.png"].readTexture()
        shockwave1 = context.resourcesVfs["textures/projectiles/shockwave1.png"].readTexture()
        shockwave2 = context.resourcesVfs["textures/projectiles/shockwave2.png"].readTexture()

        asteroid0 = context.resourcesVfs["textures/asteroids/asteroid0.png"].readTexture()

        playerShipBaseStill = context.resourcesVfs["textures/player/player_ship_base_still.png"].readTexture()
        playerShipBaseSlow = context.resourcesVfs["textures/player/player_ship_base_slow.png"].readTexture()
        playerShipBaseFast = context.resourcesVfs["textures/player/player_ship_base_fast.png"].readTexture()
        playerShipTemplate = context.resourcesVfs["textures/player/player_ship_template.png"].readTexture()
        playerHealthIndicator = context.resourcesVfs["textures/player/health.png"].readTexture()
        playerHealthEmptyIndicator = context.resourcesVfs["textures/player/health_empty.png"].readTexture()
        borderIndicator = context.resourcesVfs["textures/player/border_indicator.png"].readTexture()
        borderIndicatorNinePatch = NinePatch(borderIndicator, 24, 24, 24, 24)

        boss1background = context.resourcesVfs["textures/boss1/background.png"].readTexture()
        boss1head = context.resourcesVfs["textures/boss1/head.png"].readTexture()
        boss1beak = context.resourcesVfs["textures/boss1/beak.png"].readTexture()
        boss1heart = context.resourcesVfs["textures/boss1/heart.png"].readTexture()
        boss1tentacle = context.resourcesVfs["textures/boss1/tentacle.png"].readTexture()
        boss1tentacleSlices = boss1tentacle.slice(boss1tentacle.width / Constants.BOSS1_TENTACLE_PARTS, boss1tentacle.height)[0]
    }

    override fun release() {
        font.release()
        buttonNormal.release()
        buttonHovered.release()
        buttonPressed.release()
        starFull.release()
        starEmpty.release()
        resultBackground.release()
        resultBanner.release()
        returnIcon.release()
        playIcon.release()

        debug.release()

        energyBall.release()
        energyBeam.release()
        shockwave0.release()
        shockwave1.release()
        shockwave2.release()

        asteroid0.release()

        playerShipBaseStill.release()
        playerShipBaseSlow.release()
        playerShipBaseFast.release()
        playerShipTemplate.release()
        playerHealthIndicator.release()
        playerHealthEmptyIndicator.release()
        borderIndicator.release()

        boss1background.release()
        boss1head.release()
        boss1beak.release()
        boss1heart.release()
        boss1tentacle.release()
    }
}