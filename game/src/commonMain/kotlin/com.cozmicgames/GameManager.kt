package com.cozmicgames

import com.cozmicgames.utils.AfterFightAction
import com.cozmicgames.utils.Difficulty
import com.cozmicgames.utils.FightResults
import com.cozmicgames.utils.ShootStatistics

class GameManager {
    var tutorialStage = 0
    var isTutorialMode = false
    var startGame = false

    var startFightIndex: Int? = null
    var startFightDifficulty: Difficulty? = null
    var currentFightResults: FightResults? = null
    var afterFightAction: AfterFightAction? = null

    val shootStatistics = ShootStatistics()
    var wallet = 10000
        private set

    val unlockedBossIndices = hashSetOf(0)

    val unlockedWeaponIndices = hashSetOf(0)

    var newlyUnlockedBossIndex = -1

    val newlyUnlockedWeaponIndices = hashSetOf<Int>()

    fun update() {
        if(Game.players.isHost)
            Game.players.setGlobalState("credits", wallet)
        else
            wallet = Game.players.getGlobalState("credits") ?: 0
    }

    fun gainCredits(amount: Int) {
        if (!Game.players.isHost)
            return

        wallet += amount
        Game.players.setGlobalState("wallet", wallet)
    }

    fun spendCredits(amount: Int) {
        if (!Game.players.isHost)
            return

        wallet -= amount
        Game.players.setGlobalState("wallet", wallet)
    }
}