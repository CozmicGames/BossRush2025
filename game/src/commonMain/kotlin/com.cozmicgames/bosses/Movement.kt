package com.cozmicgames.bosses

import kotlin.time.Duration

interface Movement {
    var bossMovement: BossMovement

    fun update(delta: Duration, boss: Boss, transform: BossTransform) {
        bossMovement.update(delta, boss, transform)
    }

    fun set(movement: Movement)

    fun resetAfterAttack(boss: Boss, attack: Attack) {}

    fun setToFail(boss: Boss)

    fun setToParalyzed(boss: Boss)

    fun setToDead(boss: Boss)
}