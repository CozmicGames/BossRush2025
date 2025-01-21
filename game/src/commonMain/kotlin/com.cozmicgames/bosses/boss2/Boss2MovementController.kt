package com.cozmicgames.bosses.boss2

import com.cozmicgames.bosses.BossMovementController

class Boss2MovementController(boss: Boss2) : BossMovementController(boss, Boss2FightStage1()) {
    override val movementSpeed = 0.5f

    override val rotationSpeed = 4.0f

    override val movement = Boss2Movement()

    override val previousMovement = Boss2Movement()
}