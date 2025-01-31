package com.cozmicgames.bosses.boss3

import com.cozmicgames.bosses.BossMovementController

class Boss3MovementController(boss: Boss3) : BossMovementController(boss, Boss3FightStage1()) {
    override val movementSpeed = 0.4f

    override val rotationSpeed = 3.5f

    override val movement = Boss3Movement()

    override val previousMovement = Boss3Movement()

    fun onGrabbed() {
        if (currentAttack is GrabAttack)
            performAttack(ThrowAttack())
    }
}