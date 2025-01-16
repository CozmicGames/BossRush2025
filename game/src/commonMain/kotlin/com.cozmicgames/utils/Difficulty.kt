package com.cozmicgames.utils

enum class Difficulty(val basePlayerHealth: Int, val bossAttackSpeedModifier: Float) {
    EASY(10, 1.5f),
    NORMAL(5, 1.0f),
    HARD(2, 0.7f)

    //TODO: Maybe add player movement speed to this
}