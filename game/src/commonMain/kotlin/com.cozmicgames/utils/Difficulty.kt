package com.cozmicgames.utils

enum class Difficulty(val basePlayerHealth: Int, val bossAttackSpeedModifier: Float) {
    TUTORIAL(7, 1.0f),
    EASY(10, 1.5f),
    NORMAL(7, 1.0f),
    HARD(4, 0.5f)
}