package com.cozmicgames.bosses

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

abstract class Attack {
    open val duration: Duration? = null

    private var timer = 0.0.seconds
    private val afterAttackActions = arrayListOf<() -> Unit>()
    private var isDone = false

    abstract fun applyToMovement(movement: Movement)

    fun afterAttack(block: () -> Unit) {
        afterAttackActions += block
    }

    fun setDone() {
        isDone = true
    }

    fun isDone(delta: Duration): Boolean {
        timer += delta

        val duration = duration

        if (duration != null && timer >= duration)
            setDone()

        if (isDone)
            afterAttackActions.forEach { it() }

        return isDone
    }

    fun cancel(runAfterAttackListeners: Boolean) {
        setDone()

        if (!runAfterAttackListeners)
            afterAttackActions.clear()
    }

    open fun onStart(boss: Boss) {}
}