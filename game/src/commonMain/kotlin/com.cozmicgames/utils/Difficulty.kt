package com.cozmicgames.utils

enum class Difficulty(val basePlayerHealth: Int, val bossAttackSpeedModifier: Float) {
    EASY(20, 1.5f),
    NORMAL(1, 1.0f),
    HARD(5, 0.7f)

    //TODO: Maybe add player movement speed to this
}