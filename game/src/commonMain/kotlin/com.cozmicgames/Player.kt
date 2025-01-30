package com.cozmicgames

import com.cozmicgames.entities.worldObjects.PlayerShip
import com.cozmicgames.input.InputFrame
import com.cozmicgames.utils.HighscoreEntries
import com.cozmicgames.utils.HighscoreEntry
import com.cozmicgames.utils.ShootStatistics
import com.cozmicgames.weapons.Weapon
import com.cozmicgames.weapons.Weapons
import com.littlekt.graphics.MutableColor
import com.littlekt.graphics.OrthographicCamera
import kotlin.time.Duration.Companion.seconds

class Player {
    val color = MutableColor()
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

    var wallet = 10000
        private set

    val unlockedBossIndices = hashSetOf(0)

    val unlockedWeaponIndices = hashSetOf(0)

    var newlyUnlockedBossIndex = -1

    init {
        highscores[0].easy = HighscoreEntry(135.4.seconds, 0.54f)

        val r = Game.random.nextFloat()
        val g = Game.random.nextFloat()
        val b = Game.random.nextFloat()
        color.set(r, g, b, 1.0f)
    }

    fun gainCredits(amount: Int) {
        wallet += amount
    }

    fun spendCredits(amount: Int) {
        wallet -= amount
    }
}