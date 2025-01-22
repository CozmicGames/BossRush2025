package com.cozmicgames.bosses.boss3

import com.cozmicgames.bosses.BossMovementController
import com.cozmicgames.bosses.EndStage

class Boss3MovementController(boss: Boss3) : BossMovementController(boss, EndStage()) {
    override val movementSpeed = 0.3f

    override val rotationSpeed = 4.0f

    override val movement = Boss3Movement()

    override val previousMovement = Boss3Movement()
}