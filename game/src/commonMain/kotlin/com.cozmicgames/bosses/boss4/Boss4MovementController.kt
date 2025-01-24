package com.cozmicgames.bosses.boss4

import com.cozmicgames.bosses.*

class Boss4MovementController(boss: Boss4) : BossMovementController(boss, EndStage()) {
    override val movementSpeed = 0.5f

    override val rotationSpeed = 3.0f

    override val movement = Boss4Movement()

    override val previousMovement = Boss4Movement()
}