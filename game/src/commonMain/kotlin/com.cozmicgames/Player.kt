package com.cozmicgames

import com.cozmicgames.entities.worldObjects.PlayerShip
import com.cozmicgames.input.InputFrame
import com.cozmicgames.utils.HighscoreEntries
import com.cozmicgames.utils.ShootStatistics
import com.cozmicgames.weapons.Weapon
import com.cozmicgames.weapons.Weapons
import com.littlekt.graphics.Color
import com.littlekt.graphics.MutableColor
import com.littlekt.graphics.OrthographicCamera

class Player {
    val color = Color.fromHex("47b16f")
    val inputFrame = InputFrame()

    var primaryWeapon: Weapon? = Weapons.REELGUN
    var secondaryWeapon: Weapon? = Weapons.REELGUN

    val ship = PlayerShip(this)
    val indicatorColor = MutableColor(1.0f, 1.0f, 1.0f, 0.0f)

    val camera by lazy { OrthographicCamera(Game.graphics.width.toFloat(), Game.graphics.height.toFloat()) }

    var tutorialStage = 0
    var isTutorialMode = false

    val shootStatistics = ShootStatistics()
    val highscores = Array(Constants.BOSS_DESCRIPTORS.size) {
        HighscoreEntries()
    }

    var wallet = 100
        private set

    val unlockedBossIndices = hashSetOf(0,1,2,3)

    val unlockedWeaponIndices = hashSetOf(0)

    var newlyUnlockedBossIndex = Constants.FINAL_FIGHT_INDEX//-1

    var currentFightIndex = 0

    fun gainCredits(amount: Int) {
        wallet += amount
    }

    fun spendCredits(amount: Int) {
        wallet -= amount
    }
}