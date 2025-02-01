package com.cozmicgames.graphics

import com.cozmicgames.Constants
import com.littlekt.Context
import com.littlekt.Releasable
import com.littlekt.file.vfs.readAtlas
import com.littlekt.graphics.g2d.NinePatch
import com.littlekt.graphics.g2d.TextureAtlas
import com.littlekt.graphics.g2d.TextureSlice

class Textures : Releasable {
    private lateinit var atlas: TextureAtlas

    /**
     * UI
     */
    lateinit var logo: TextureSlice
    lateinit var buttonNormal: TextureSlice
    lateinit var buttonNormalNinePatch: NinePatch
    lateinit var buttonHovered: TextureSlice
    lateinit var buttonHoveredNinePatch: NinePatch
    lateinit var buttonPressed: TextureSlice
    lateinit var buttonPressedNinePatch: NinePatch
    lateinit var starFull: TextureSlice
    lateinit var starEmpty: TextureSlice
    lateinit var resultBackground: TextureSlice
    lateinit var resultBackgroundNinePatch: NinePatch
    lateinit var ratingBackground: TextureSlice
    lateinit var ratingBackgroundNinePatch: NinePatch
    lateinit var resultBanner: TextureSlice
    lateinit var returnIcon: TextureSlice
    lateinit var playIcon: TextureSlice
    lateinit var fightSelectionPoster: TextureSlice
    lateinit var fightSelectionPosterNinePatch: NinePatch
    lateinit var fightSelectionPosterMask: TextureSlice
    lateinit var fightSelectionPosterMaskNinePatch: NinePatch
    lateinit var lockBody: TextureSlice
    lateinit var lockShackle: TextureSlice
    lateinit var todoPreview: TextureSlice
    lateinit var weaponBackground: TextureSlice
    lateinit var weaponBackgroundNinePatch: NinePatch
    lateinit var weaponBackgroundHovered: TextureSlice
    lateinit var weaponBackgroundHoveredNinePatch: NinePatch
    lateinit var weaponSelected: TextureSlice
    lateinit var weaponSelectedNinePatch: NinePatch
    lateinit var weaponMask: TextureSlice
    lateinit var weaponMaskNinePatch: NinePatch
    lateinit var shopBackground: TextureSlice
    lateinit var shopBackgroundNinePatch: NinePatch
    lateinit var walletBackground: TextureSlice
    lateinit var walletBackgroundNinePatch: NinePatch
    lateinit var highscoreBackground: TextureSlice
    lateinit var highscoreBackgroundNinePatch: NinePatch
    lateinit var reelgunPreview: TextureSlice
    lateinit var hyperHarpoonPreview: TextureSlice
    lateinit var scattergunPreview: TextureSlice
    lateinit var baitblasterPreview: TextureSlice
    lateinit var shockminePreview: TextureSlice
    lateinit var currencyIcon: TextureSlice
    lateinit var messageBannerBackground: TextureSlice
    lateinit var playerSlot: TextureSlice
    lateinit var playerSlotEmpty: TextureSlice
    lateinit var playerSlotBackground: TextureSlice
    lateinit var weaponSlot: TextureSlice
    lateinit var weaponSlotEmpty: TextureSlice
    lateinit var weaponSlotBackground: TextureSlice
    lateinit var crewBackground: TextureSlice
    lateinit var crewBackgroundNinePatch: NinePatch
    lateinit var fightBackground: TextureSlice
    lateinit var fightBackgroundNinePatch: NinePatch

    lateinit var borderIndicator: TextureSlice
    lateinit var borderIndicatorNinePatch: NinePatch
    lateinit var transition: TextureSlice

    /**
     * Projectiles and area effects
     */
    lateinit var energyBall: TextureSlice
    lateinit var energyBeam: TextureSlice
    lateinit var baitBall: TextureSlice
    lateinit var shockwave: TextureSlice
    lateinit var shield: TextureSlice

    /**
     * World
     */
    lateinit var background: TextureSlice
    lateinit var asteroid0: TextureSlice
    lateinit var vortexBase: TextureSlice
    lateinit var vortex0: TextureSlice
    lateinit var vortex1: TextureSlice
    lateinit var vortex2: TextureSlice
    lateinit var vortex3: TextureSlice

    /**
     * Player
     */
    lateinit var playerShipBaseStill: TextureSlice
    lateinit var playerShipBaseSlow: TextureSlice
    lateinit var playerShipBaseFast: TextureSlice
    lateinit var playerShipTemplate: TextureSlice
    lateinit var playerHealthIndicator: TextureSlice
    lateinit var playerHealthEmptyIndicator: TextureSlice
    lateinit var playerHealthIndicatorNinepatch: NinePatch
    lateinit var playerHealthEmptyIndicatorNinepatch: NinePatch

    /**
     * Tutorial boss
     */
    lateinit var bossTutorialHead: TextureSlice
    lateinit var bossTutorialEyes: TextureSlice
    lateinit var bossTutorialEyesDead: TextureSlice
    lateinit var bossTutorialMouth: TextureSlice
    lateinit var bossTutorialBody: TextureSlice
    lateinit var bossTutorialBodySlices: Array<TextureSlice>
    lateinit var bossTutorialTail: TextureSlice
    lateinit var bossTutorialBackFin: TextureSlice
    lateinit var bossTutorialBodyFin: TextureSlice


    /**
     * Bosses general
     */
    lateinit var bossBeak: TextureSlice
    lateinit var bossHeart: TextureSlice

    /**
     * Boss 1 - Space octopus
     */
    lateinit var boss1preview: TextureSlice
    lateinit var boss1head: TextureSlice
    lateinit var boss1headDead: TextureSlice
    lateinit var boss1tentacle: TextureSlice
    lateinit var boss1tentacleSlices: Array<TextureSlice>

    /**
     * Boss 2 - Shark
     */
    lateinit var boss2preview: TextureSlice
    lateinit var boss2head: TextureSlice
    lateinit var boss2headDead: TextureSlice
    lateinit var boss2sword: TextureSlice
    lateinit var boss2swordDead: TextureSlice
    lateinit var boss2fin: TextureSlice
    lateinit var boss2body: TextureSlice
    lateinit var boss2bodySlices: Array<TextureSlice>
    lateinit var boss2tail: TextureSlice

    /*
    * Boss 3 - Crab
     */
    lateinit var boss3preview: TextureSlice
    lateinit var boss3head: TextureSlice
    lateinit var boss3headDead: TextureSlice
    lateinit var boss3arm: TextureSlice
    lateinit var boss3clawBase: TextureSlice
    lateinit var boss3clawUpper: TextureSlice
    lateinit var boss3clawLower: TextureSlice
    lateinit var boss3clawLowerDead: TextureSlice
    lateinit var boss3legUpper: TextureSlice
    lateinit var boss3legLower: TextureSlice
    lateinit var boss3foot: TextureSlice

    /*
    * Boss 4 - Ray
     */
    lateinit var boss4preview: TextureSlice
    lateinit var boss4head: TextureSlice
    lateinit var boss4eyes: TextureSlice
    lateinit var boss4eyesDead: TextureSlice
    lateinit var boss4body: TextureSlice
    lateinit var boss4wing: TextureSlice
    lateinit var boss4tail: TextureSlice
    lateinit var boss4tailSlices: Array<TextureSlice>

    suspend fun load(context: Context) {
        atlas = context.resourcesVfs["textures.json"].readAtlas()

        logo = atlas["logo"].slice
        buttonNormal = atlas["button_normal"].slice
        buttonNormalNinePatch = NinePatch(buttonNormal, 16, 16, 16, 16)
        buttonHovered = atlas["button_hovered"].slice
        buttonHoveredNinePatch = NinePatch(buttonHovered, 16, 16, 16, 16)
        buttonPressed = atlas["button_pressed"].slice
        buttonPressedNinePatch = NinePatch(buttonPressed, 16, 16, 16, 16)
        starFull = atlas["star_full"].slice
        starEmpty = atlas["star_empty"].slice
        resultBackground = atlas["result_background"].slice
        resultBackgroundNinePatch = NinePatch(resultBackground, 32, 32, 32, 32)
        ratingBackground = atlas["rating_background"].slice
        ratingBackgroundNinePatch = NinePatch(ratingBackground, 32, 32, 32, 32)
        resultBanner = atlas["result_banner"].slice
        returnIcon = atlas["return_icon"].slice
        playIcon = atlas["play_icon"].slice
        fightSelectionPoster = atlas["fight_selection_poster"].slice
        fightSelectionPosterNinePatch = NinePatch(fightSelectionPoster, 28, 28, 28, 28)
        fightSelectionPosterMask = atlas["fight_selection_poster_mask"].slice
        fightSelectionPosterMaskNinePatch = NinePatch(fightSelectionPoster, 28, 28, 28, 28)
        lockBody = atlas["lock_body"].slice
        lockShackle = atlas["lock_shackle"].slice
        todoPreview = atlas["todo_preview"].slice
        weaponBackground = atlas["weapon_background"].slice
        weaponBackgroundNinePatch = NinePatch(weaponBackground, 32, 32, 32, 32)
        weaponBackgroundHovered = atlas["weapon_background_hovered"].slice
        weaponBackgroundHoveredNinePatch = NinePatch(weaponBackgroundHovered, 32, 32, 32, 32)
        weaponSelected = atlas["weapon_selected"].slice
        weaponSelectedNinePatch = NinePatch(weaponSelected, 32, 32, 32, 32)
        weaponMask = atlas["weapon_mask"].slice
        weaponMaskNinePatch = NinePatch(weaponMask, 32, 32, 32, 32)
        shopBackground = atlas["shop_background"].slice
        shopBackgroundNinePatch = NinePatch(shopBackground, 32, 32, 32, 32)
        walletBackground = atlas["wallet_background"].slice
        walletBackgroundNinePatch = NinePatch(walletBackground, 12, 12, 12, 12)
        highscoreBackground = atlas["highscore_background"].slice
        highscoreBackgroundNinePatch = NinePatch(highscoreBackground, 7, 7, 7, 7)
        reelgunPreview = atlas["reelgun_preview"].slice
        hyperHarpoonPreview = atlas["hyper_harpoon_preview"].slice
        scattergunPreview = atlas["scattergun_preview"].slice
        baitblasterPreview = atlas["baitblaster_preview"].slice
        shockminePreview = atlas["shockmine_preview"].slice
        currencyIcon = atlas["currency_icon"].slice
        messageBannerBackground = atlas["message_banner_background"].slice
        playerSlot = atlas["player_slot"].slice
        playerSlotEmpty = atlas["player_slot_empty"].slice
        playerSlotBackground = atlas["player_slot_background"].slice
        weaponSlot = atlas["weapon_slot"].slice
        weaponSlotEmpty = atlas["weapon_slot_empty"].slice
        weaponSlotBackground = atlas["weapon_slot_background"].slice
        crewBackground = atlas["crew_background"].slice
        crewBackgroundNinePatch = NinePatch(crewBackground, 36, 36, 36, 36)
        fightBackground = atlas["fight_background"].slice
        fightBackgroundNinePatch = NinePatch(fightBackground, 22, 22, 22, 22)

        transition = atlas["transition"].slice
        borderIndicator = atlas["border_indicator"].slice
        borderIndicatorNinePatch = NinePatch(borderIndicator, 60, 60, 60, 60)

        energyBall = atlas["energy_ball"].slice
        energyBeam = atlas["energy_beam"].slice
        baitBall = atlas["bait_ball"].slice
        shockwave = atlas["shockwave"].slice
        shield = atlas["shield"].slice

        background = atlas["background"].slice
        asteroid0 = atlas["asteroid0"].slice
        vortexBase = atlas["vortex_base"].slice
        vortex0 = atlas["vortex0"].slice
        vortex1 = atlas["vortex1"].slice
        vortex2 = atlas["vortex2"].slice
        vortex3 = atlas["vortex3"].slice

        playerShipBaseStill = atlas["player_ship_base_still"].slice
        playerShipBaseSlow = atlas["player_ship_base_slow"].slice
        playerShipBaseFast = atlas["player_ship_base_fast"].slice
        playerShipTemplate = atlas["player_ship_template"].slice
        playerHealthIndicator = atlas["player_health"].slice
        playerHealthIndicatorNinepatch = NinePatch(playerHealthIndicator, 3, 3, 3, 3)
        playerHealthEmptyIndicator = atlas["player_health_empty"].slice
        playerHealthEmptyIndicatorNinepatch = NinePatch(playerHealthEmptyIndicator, 3, 3, 3, 3)

        bossTutorialHead = atlas["tutorial_boss_head"].slice
        bossTutorialEyes = atlas["tutorial_boss_eyes"].slice
        bossTutorialEyesDead = atlas["tutorial_boss_eyes_dead"].slice
        bossTutorialMouth = atlas["tutorial_boss_mouth"].slice
        bossTutorialBody = atlas["tutorial_boss_body"].slice
        bossTutorialBodySlices = bossTutorialBody.slice(bossTutorialBody.width / Constants.BOSS_TUTORIAL_BODY_PARTS, bossTutorialBody.height)[0]
        bossTutorialTail = atlas["tutorial_boss_tail"].slice
        bossTutorialBackFin = atlas["tutorial_boss_back_fin"].slice
        bossTutorialBodyFin = atlas["tutorial_boss_body_fin"].slice

        bossBeak = atlas["boss_beak"].slice
        bossHeart = atlas["boss_heart"].slice

        boss1preview = atlas["boss1_preview"].slice
        boss1head = atlas["boss1_head"].slice
        boss1headDead = atlas["boss1_head_dead"].slice
        boss1tentacle = atlas["boss1_tentacle"].slice
        boss1tentacleSlices = boss1tentacle.slice(boss1tentacle.width / Constants.BOSS1_TENTACLE_PARTS, boss1tentacle.height)[0]

        boss2preview = atlas["boss2_preview"].slice
        boss2head = atlas["boss2_head"].slice
        boss2headDead = atlas["boss2_head_dead"].slice
        boss2sword = atlas["boss2_sword"].slice
        boss2swordDead = atlas["boss2_sword_dead"].slice
        boss2fin = atlas["boss2_fin"].slice
        boss2body = atlas["boss2_body"].slice
        boss2bodySlices = boss2body.slice(boss2body.width / Constants.BOSS2_BODY_PARTS, boss2body.height)[0]
        boss2tail = atlas["boss2_tail"].slice

        boss3preview = atlas["boss3_preview"].slice
        boss3head = atlas["boss3_head"].slice
        boss3headDead = atlas["boss3_head_dead"].slice
        boss3arm = atlas["boss3_arm"].slice
        boss3clawBase = atlas["boss3_claw_base"].slice
        boss3clawUpper = atlas["boss3_claw_upper"].slice
        boss3clawLower = atlas["boss3_claw_lower"].slice
        boss3clawLowerDead = atlas["boss3_claw_lower_dead"].slice
        boss3legUpper = atlas["boss3_leg_upper"].slice
        boss3legLower = atlas["boss3_leg_lower"].slice
        boss3foot = atlas["boss3_foot"].slice

        boss4preview = atlas["boss4_preview"].slice
        boss4head = atlas["boss4_head"].slice
        boss4eyes = atlas["boss4_eyes"].slice
        boss4eyesDead = atlas["boss4_eyes_dead"].slice
        boss4body = atlas["boss4_body"].slice
        boss4wing = atlas["boss4_wing"].slice
        boss4tail = atlas["boss4_tail"].slice
        boss4tailSlices = boss4tail.slice(boss4tail.width, boss4tail.height / Constants.BOSS4_TAIL_PARTS).map { it[0] }.toTypedArray()
    }

    override fun release() {
        atlas.release()
    }
}