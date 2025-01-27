package com.cozmicgames.bosses.boss4

import com.cozmicgames.bosses.*

class Boss4MovementController(boss: Boss4) : BossMovementController(boss, Boss4FightStage1()) {
    override val movementSpeed = 0.8f

    override val rotationSpeed = 7.0f

    override val movement = Boss4Movement()

    override val previousMovement = Boss4Movement()
}