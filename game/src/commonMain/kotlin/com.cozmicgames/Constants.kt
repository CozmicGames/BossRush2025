package com.cozmicgames

import com.cozmicgames.bosses.boss1.Boss1Desc
import com.cozmicgames.bosses.boss2.Boss2Desc
import com.cozmicgames.bosses.boss3.Boss3Desc
import com.cozmicgames.bosses.boss4.Boss4Desc
import com.littlekt.graphics.Color

object Constants {
    val BOSS_DESCRIPTORS = arrayOf(
        Boss1Desc(),
        Boss2Desc(),
        Boss3Desc(),
        Boss4Desc()
    )

    const val INPUT_RAMP_UP_SPEED = 1.5f
    const val INPUT_RAMP_DOWN_SPEED = 1.0f

    const val PLAYER_SHIP_WIDTH = 100.0f
    const val PLAYER_SHIP_HEIGHT = 100.0f
    const val PLAYER_SHIP_BASE_MOVEMENT_SPEED = 400.0f
    const val PLAYER_SHIP_BASE_ROTATION_SPEED = 300.0f

    const val WORLD_DECELERATION_BORDER = 100.0f

    const val CAMERA_FOLLOW_MOVE_SPEED = 5.0f
    const val CAMERA_FOLLOW_ROTATE_SPEED = 0.5f

    const val BOSS_TUTORIAL_BODY_PARTS = 8
    const val BOSS1_TENTACLE_PARTS = 8
    const val BOSS2_BODY_PARTS = 12
    const val BOSS4_TAIL_PARTS = 16

    val INDICATOR_COLOR_BORDER = Color(0.1f, 0.3f, 0.9f, 1.0f)

    const val BOSS_SELECTION_POSTER_WIDTH = 200.0f
    const val BOSS_SELECTION_POSTER_HEIGHT = 256.0f

    const val FINAL_FIGHT_INDEX = 4
}