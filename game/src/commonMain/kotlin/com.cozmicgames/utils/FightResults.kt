package com.cozmicgames.utils

import com.littlekt.math.clamp

class FightResults(val difficulty: Difficulty, val bossFullHealth: Int, val bossFinalHealth: Int, val playerAverageFinalHealth: Float, val shotsFired: Int, val shotsHit: Int) {
    val bossDamagePoints get() = ((bossFinalHealth.toFloat() / bossFullHealth.toFloat()).clamp(0.0f, 1.0f) * 5).toInt()

    val playerDamagePoints get() = ((playerAverageFinalHealth / difficulty.basePlayerHealth).clamp(0.0f, 1.0f) * 5).toInt()

    val accuracyPoints get() = ((shotsHit.toFloat() / shotsFired.toFloat()).clamp(0.0f, 1.0f) * 5).toInt()

    val totalPoints get() = (bossDamagePoints + playerDamagePoints + accuracyPoints) / 3

    val message
        get() = if (bossFinalHealth <= 0)
            when (totalPoints) {
                5 -> "Stellar catch!"
                4 -> "Reeling in the big one!"
                3 -> "Decent haul!"
                2 -> "Practice makes perfect!"
                1 -> "Tighten those lines!"
                else -> "Lucky catch!"
            }
        else
            when (totalPoints) {
                5 -> "Reeling in the big one!"
                4 -> "Almost there!"
                3 -> "Tighten those lines!"
                2 -> "Fishing in the dark!"
                1 -> "The bait didn't bite!"
                else -> "You got hooked instead!"
            }
}