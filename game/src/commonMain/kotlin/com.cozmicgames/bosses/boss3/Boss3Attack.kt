package com.cozmicgames.bosses.boss3

import com.cozmicgames.Game
import com.cozmicgames.bosses.*
import com.cozmicgames.entities.worldObjects.PlayerShip
import com.cozmicgames.weapons.AreaEffectGrowthType
import com.cozmicgames.weapons.AreaEffectSourceType
import com.cozmicgames.weapons.AreaEffectType
import com.littlekt.math.geom.degrees
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

abstract class Boss1Attack : Attack() {
    abstract val bossMovement: BossMovement?
    abstract val legMovement: LegMovement?
    abstract val armMovement: ArmMovement?
    abstract val beakMovement: BeakMovement?

    override fun applyToMovement(movement: Movement) {
        movement as? Boss3Movement ?: throw IllegalArgumentException("Movement must be a Boss1Movement")
        bossMovement?.let { movement.bossMovement = it }
        legMovement?.let { movement.legMovement = it }
        armMovement?.let { movement.armMovement = it }
        beakMovement?.let { movement.beakMovement = it }
    }
}