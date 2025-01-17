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
    lateinit var ratingBackground: Texture
    lateinit var ratingBackgroundNinePatch: NinePatch
    lateinit var resultBanner: Texture
    lateinit var returnIcon: Texture
    lateinit var playIcon: Texture
    lateinit var fightSelectionPoster: Texture
    lateinit var fightSelectionPosterNinePatch: NinePatch
    lateinit var fightSelectionPosterMask: Texture
    lateinit var fightSelectionPosterMaskNinePatch: NinePatch
    lateinit var lockBody: Texture
    lateinit var lockShackle: Texture
    lateinit var todoPreview: Texture
    lateinit var weaponBackground: Texture
    lateinit var weaponBackgroundNinePatch: NinePatch
    lateinit var weaponBackgroundHovered: Texture
    lateinit var weaponBackgroundHoveredNinePatch: NinePatch
    lateinit var weaponSelected: Texture
    lateinit var weaponSelectedNinePatch: NinePatch
    lateinit var weaponMask: Texture
    lateinit var weaponMaskNinePatch: NinePatch
    lateinit var shopBackground: Texture
    lateinit var shopBackgroundNinePatch: NinePatch
    lateinit var walletBackground: Texture
    lateinit var walletBackgroundNinePatch: NinePatch
    lateinit var reelgunPreview: Texture
    lateinit var hyperHarpoonPreview: Texture
    lateinit var scattergunPreview: Texture
    lateinit var baitblasterPreview: Texture
    lateinit var currencyIcon: Texture

    lateinit var debug: Texture
    lateinit var debugNinePatch: NinePatch

    /**
     * Projectiles and area effects
     */
    lateinit var energyBall: Texture
    lateinit var energyBeam: Texture
    lateinit var baitBall: Texture
    lateinit var shockwave0: Texture
    lateinit var shockwave1: Texture
    lateinit var shockwave2: Texture
    lateinit var shield: Texture

    /**
     * World
     */
    lateinit var background: Texture
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
    lateinit var boss1preview: Texture
    lateinit var boss1head: Texture
    lateinit var boss1beak: Texture
    lateinit var boss1heart: Texture
    lateinit var boss1tentacle: Texture
    lateinit var boss1tentacleSlices: Array<TextureSlice>

    /**
     * Boss 2 - Shark
     */
    lateinit var boss2preview: Texture
    lateinit var boss2head: Texture
    lateinit var boss2sword: Texture
    lateinit var boss2fin: Texture
    lateinit var boss2body: Texture
    lateinit var boss2bodySlices: Array<TextureSlice>
    lateinit var boss2tail: Texture

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
        ratingBackground = context.resourcesVfs["textures/ui/rating_background.png"].readTexture()
        ratingBackgroundNinePatch = NinePatch(ratingBackground, 16, 16, 16, 16)
        resultBanner = context.resourcesVfs["textures/ui/result_banner.png"].readTexture()
        returnIcon = context.resourcesVfs["textures/ui/return_icon.png"].readTexture()
        playIcon = context.resourcesVfs["textures/ui/play_icon.png"].readTexture()
        fightSelectionPoster = context.resourcesVfs["textures/ui/fight_selection_poster.png"].readTexture()
        fightSelectionPosterNinePatch = NinePatch(fightSelectionPoster, 28, 28, 28, 28)
        fightSelectionPosterMask = context.resourcesVfs["textures/ui/fight_selection_poster_mask.png"].readTexture()
        fightSelectionPosterMaskNinePatch = NinePatch(fightSelectionPoster, 28, 28, 28, 28)
        lockBody = context.resourcesVfs["textures/ui/lock_body.png"].readTexture()
        lockShackle = context.resourcesVfs["textures/ui/lock_shackle.png"].readTexture()
        todoPreview = context.resourcesVfs["textures/todo_preview.png"].readTexture()
        weaponBackground = context.resourcesVfs["textures/ui/weapon_background.png"].readTexture()
        weaponBackgroundNinePatch = NinePatch(weaponBackground, 32, 32, 32, 32)
        weaponBackgroundHovered = context.resourcesVfs["textures/ui/weapon_background_hovered.png"].readTexture()
        weaponBackgroundHoveredNinePatch = NinePatch(weaponBackgroundHovered, 32, 32, 32, 32)
        weaponSelected = context.resourcesVfs["textures/ui/weapon_selected.png"].readTexture()
        weaponSelectedNinePatch = NinePatch(weaponSelected, 32, 32, 32, 32)
        weaponMask = context.resourcesVfs["textures/ui/weapon_mask.png"].readTexture()
        weaponMaskNinePatch = NinePatch(weaponMask, 32, 32, 32, 32)
        shopBackground = context.resourcesVfs["textures/ui/shop_background.png"].readTexture()
        shopBackgroundNinePatch = NinePatch(shopBackground, 32, 32, 32, 32)
        walletBackground = context.resourcesVfs["textures/ui/wallet_background.png"].readTexture()
        walletBackgroundNinePatch = NinePatch(walletBackground, 12, 12, 12, 12)
        reelgunPreview = context.resourcesVfs["textures/weapons/reelgun_preview.png"].readTexture()
        hyperHarpoonPreview = context.resourcesVfs["textures/weapons/hyper_harpoon_preview.png"].readTexture()
        scattergunPreview = context.resourcesVfs["textures/weapons/scattergun_preview.png"].readTexture()
        baitblasterPreview = context.resourcesVfs["textures/weapons/baitblaster_preview.png"].readTexture()
        currencyIcon = context.resourcesVfs["textures/ui/currency_icon.png"].readTexture()

        debug = context.resourcesVfs["textures/ui/debug.png"].readTexture()
        debugNinePatch = NinePatch(debug, 4, 4, 4, 4)

        energyBall = context.resourcesVfs["textures/projectiles/energy_ball.png"].readTexture()
        energyBeam = context.resourcesVfs["textures/projectiles/energy_beam.png"].readTexture()
        baitBall = context.resourcesVfs["textures/projectiles/bait_ball.png"].readTexture()
        shockwave0 = context.resourcesVfs["textures/projectiles/shockwave0.png"].readTexture()
        shockwave1 = context.resourcesVfs["textures/projectiles/shockwave1.png"].readTexture()
        shockwave2 = context.resourcesVfs["textures/projectiles/shockwave2.png"].readTexture()
        shield = context.resourcesVfs["textures/projectiles/shield.png"].readTexture()

        background = context.resourcesVfs["textures/background.png"].readTexture()
        asteroid0 = context.resourcesVfs["textures/asteroids/asteroid0.png"].readTexture()

        playerShipBaseStill = context.resourcesVfs["textures/player/player_ship_base_still.png"].readTexture()
        playerShipBaseSlow = context.resourcesVfs["textures/player/player_ship_base_slow.png"].readTexture()
        playerShipBaseFast = context.resourcesVfs["textures/player/player_ship_base_fast.png"].readTexture()
        playerShipTemplate = context.resourcesVfs["textures/player/player_ship_template.png"].readTexture()
        playerHealthIndicator = context.resourcesVfs["textures/player/health.png"].readTexture()
        playerHealthEmptyIndicator = context.resourcesVfs["textures/player/health_empty.png"].readTexture()
        borderIndicator = context.resourcesVfs["textures/player/border_indicator.png"].readTexture()
        borderIndicatorNinePatch = NinePatch(borderIndicator, 24, 24, 24, 24)

        boss1preview = context.resourcesVfs["textures/boss1/preview.png"].readTexture()
        boss1head = context.resourcesVfs["textures/boss1/head.png"].readTexture()
        boss1beak = context.resourcesVfs["textures/boss1/beak.png"].readTexture()
        boss1heart = context.resourcesVfs["textures/boss1/heart.png"].readTexture()
        boss1tentacle = context.resourcesVfs["textures/boss1/tentacle.png"].readTexture()
        boss1tentacleSlices = boss1tentacle.slice(boss1tentacle.width / Constants.BOSS1_TENTACLE_PARTS, boss1tentacle.height)[0]

        //boss2preview = context.resourcesVfs["textures/boss2/preview.png"].readTexture()
        boss2head = context.resourcesVfs["textures/boss2/head.png"].readTexture()
        boss2sword = context.resourcesVfs["textures/boss2/sword.png"].readTexture()
        boss2fin = context.resourcesVfs["textures/boss2/fin.png"].readTexture()
        boss2body = context.resourcesVfs["textures/boss2/body.png"].readTexture()
        boss2bodySlices = boss2body.slice(boss2body.width / Constants.BOSS2_BODY_PARTS, boss2body.height)[0]
        boss2tail = context.resourcesVfs["textures/boss2/tail.png"].readTexture()
    }

    override fun release() {
        font.release()
        buttonNormal.release()
        buttonHovered.release()
        buttonPressed.release()
        starFull.release()
        starEmpty.release()
        resultBackground.release()
        ratingBackground.release()
        resultBanner.release()
        returnIcon.release()
        playIcon.release()
        fightSelectionPoster.release()
        fightSelectionPosterMask.release()
        lockBody.release()
        lockShackle.release()
        todoPreview.release()
        weaponBackground.release()
        weaponBackgroundHovered.release()
        weaponSelected.release()
        weaponMask.release()
        shopBackground.release()
        walletBackground.release()
        reelgunPreview.release()
        hyperHarpoonPreview.release()
        scattergunPreview.release()
        baitblasterPreview.release()
        currencyIcon.release()

        debug.release()

        energyBall.release()
        energyBeam.release()
        baitBall.release()
        shockwave0.release()
        shockwave1.release()
        shockwave2.release()
        shield.release()

        background.release()
        asteroid0.release()

        playerShipBaseStill.release()
        playerShipBaseSlow.release()
        playerShipBaseFast.release()
        playerShipTemplate.release()
        playerHealthIndicator.release()
        playerHealthEmptyIndicator.release()
        borderIndicator.release()

        boss1preview.release()
        boss1head.release()
        boss1beak.release()
        boss1heart.release()
        boss1tentacle.release()

        //boss2preview.release()
        //boss2background.release()
        boss2head.release()
        boss2sword.release()
        boss2fin.release()
        boss2body.release()
        boss2tail.release()
    }
}