package com.cozmicgames.bosses.boss2

import com.cozmicgames.Game
import com.cozmicgames.utils.lerp
import com.cozmicgames.weapons.ProjectileType
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

sealed interface ShieldMovement {
    fun updateShield(delta: Duration, shield: Shield)

}

class IdleShieldMovement : ShieldMovement {
    override fun updateShield(delta: Duration, shield: Shield) {
        shield.intensity = lerp(shield.intensity, 1.0f, 0.1f * if (shield.boss.isParalyzed) 0.01f else 1.0f)
    }
}

class ShootShieldMovement(private val shootTime: Duration = 0.3.seconds) : ShieldMovement {
    private var timer = 0.0.seconds

    override fun updateShield(delta: Duration, shield: Shield) {
        timer += delta

        shield.intensity = lerp(shield.intensity, 1.0f, 0.1f * if (shield.boss.isParalyzed) 0.01f else 1.0f)

        if (timer >= shootTime) {
            Game.projectiles.spawnProjectile(shield.boss, ProjectileType.ENERGY_BALL, shield.boss.muzzleX, shield.boss.muzzleY, shield.boss.muzzleRotation, 500.0f, 0.0f)
            timer = 0.0.seconds
        }
    }
}

class BeamShieldMovement(private val shootTime: Duration = 2.0.seconds) : ShieldMovement {
    private var timer = 0.0.seconds
    private var isFirstUpdate = true

    override fun updateShield(delta: Duration, shield: Shield) {
        if (isFirstUpdate) {
            Game.projectiles.spawnProjectile(shield.boss, ProjectileType.ENERGY_BEAM, shield.boss.muzzleX, shield.boss.muzzleY, shield.boss.muzzleRotation, 300.0f, 0.0f)
            isFirstUpdate = false
        }

        shield.intensity = lerp(shield.intensity, 1.0f, 0.1f * if (shield.boss.isParalyzed) 0.01f else 1.0f)

        timer += delta

        if (timer >= shootTime)
            Game.projectiles.stopBeamProjectile(shield.boss)
    }
}
