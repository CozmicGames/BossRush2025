package com.cozmicgames.bosses.tutorialBoss

import com.cozmicgames.bosses.*

class TutorialBossMovementController(boss: TutorialBoss) : BossMovementController(boss, EndStage()) {
    override val movementSpeed = 0.3f

    override val rotationSpeed = 3.0f

    override val movement = TutorialBossMovement()

    override val previousMovement = TutorialBossMovement()
}