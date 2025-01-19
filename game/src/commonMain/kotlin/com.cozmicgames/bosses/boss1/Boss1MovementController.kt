package com.cozmicgames.bosses.boss1

import com.cozmicgames.bosses.*

class Boss1MovementController(boss: Boss1) : BossMovementController(boss, Boss1FightStage1()) {
    override val movementSpeed = 0.5f

    override val rotationSpeed = 3.0f

    override val movement = Boss1Movement()

    override val previousMovement = Boss1Movement()
}