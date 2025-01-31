package com.cozmicgames.entities.worldObjects

import com.cozmicgames.bosses.BossTarget
import com.cozmicgames.graphics.Renderer
import kotlin.time.Duration

class World {
    private val objects = arrayListOf<WorldObject>()
    private val bossTargetsInternal = arrayListOf<BossTarget>()

    fun decideOnTarget(): BossTarget? {
        val targets = bossTargetsInternal.filter { it.canBeHit }
        if (targets.isEmpty())
            return null

        val highestAppeal = targets.maxOf { it.appeal }
        return targets.filter { it.appeal == highestAppeal }.random()
    }

    fun add(worldObject: WorldObject) {
        objects += worldObject
        worldObject.onAddToWorld()
        if (worldObject is BossTarget)
            addBossTarget(worldObject)
    }

    fun remove(worldObject: WorldObject) {
        objects -= worldObject
        worldObject.onRemoveFromWorld()
        if (worldObject is BossTarget)
            removeBossTarget(worldObject)
    }

    fun addBossTarget(target: BossTarget) {
        bossTargetsInternal.add(target)
    }

    fun removeBossTarget(target: BossTarget) {
        bossTargetsInternal.remove(target)
    }

    fun clearBossTargets() {
        bossTargetsInternal.clear()
    }

    fun update(delta: Duration, isFighting: Boolean) {
        for (entity in objects) {
            entity.update(delta, isFighting)
        }
    }

    fun render(renderer: Renderer) {
        for (entity in objects) {
            entity.render(renderer)
        }
    }

    fun clear() {
        objects.clear()
    }
}