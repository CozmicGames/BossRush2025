package com.cozmicgames.bosses.boss2

import com.cozmicgames.Game
import com.cozmicgames.utils.lerp
import com.cozmicgames.weapons.ProjectileType
import com.littlekt.math.geom.cosine
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.sine
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

class ParalyzedShieldMovement : ShieldMovement {
    private var isFirstUpdate = true

    override fun updateShield(delta: Duration, shield: Shield) {
        if (isFirstUpdate) {
            if (shield.intensity > 0.5f) {
                val shots = 7

                for (i in 0 until shots) {
                    val angle = (if (shield.boss.isFlipped) shield.boss.muzzleRotation - 135.0.degrees else shield.boss.muzzleRotation + 45.0.degrees) + 90.0.degrees / (shots - 1) * i
                    val cos = angle.cosine
                    val sin = angle.sine
                    val radius = shield.width * 0.5f
                    val x = shield.x + cos * radius
                    val y = shield.y + sin * radius
                    Game.projectiles.spawnProjectile(shield.boss, ProjectileType.ENERGY_BALL, x, y, angle, 500.0f, 0.0f)
                }
            }

            shield.intensity = 0.0f

            isFirstUpdate = false
        }
    }
}

class DeadShieldMovement : ShieldMovement {
    override fun updateShield(delta: Duration, shield: Shield) {
        shield.intensity = lerp(shield.intensity, 0.0f, 0.5f)
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
