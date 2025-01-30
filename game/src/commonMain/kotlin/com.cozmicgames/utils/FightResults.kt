package com.cozmicgames.utils

import com.littlekt.math.clamp
import kotlin.math.ceil
import kotlin.time.Duration

class FightResults(val duration: Duration, val difficulty: Difficulty, val bossFullHealth: Int, val bossFinalHealth: Int, val playerAverageFinalHealth: Int, val shotsFired: Int, val shotsHit: Int) {
    val isVictory = bossFinalHealth <= 0
    val bossDamage = 1.0f - (bossFinalHealth.toFloat() / bossFullHealth.toFloat()).clamp(0.0f, 1.0f)
    val playerHealth = (playerAverageFinalHealth.toFloat() / difficulty.basePlayerHealth.toFloat()).clamp(0.0f, 1.0f)
    val accuracy = (shotsHit.toFloat() / shotsFired.toFloat()).clamp(0.0f, 1.0f)

    val bossDamagePoints get() = (bossDamage * 5).toInt()

    val playerHealthPoints get() = (playerHealth * 5).toInt()

    val accuracyPoints get() = ceil(accuracy * 5).toInt()

    val totalPoints get() = (bossDamagePoints + playerHealthPoints + accuracyPoints) / 3

    val percentage get() = (bossDamage + playerHealth + accuracy) / 3.0f

    val message
        get() = if (isVictory)
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
                1 -> "That bait didn't bite!"
                else -> "You got hooked instead!"
            }
}