package com.cozmicgames.bosses.boss3

import com.cozmicgames.Game
import com.cozmicgames.bosses.BossHittable
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.cozmicgames.entities.worldObjects.ProjectileSource
import com.cozmicgames.entities.worldObjects.animations.ParalyzeAnimation
import com.littlekt.math.geom.Angle
import com.littlekt.math.geom.degrees
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class Arm(override val boss: Boss3, val index: Int, val flip: Boolean, val layer: Int, val baseRotation: Angle, val scale: Float) : BossHittable, PlayerDamageSource, ProjectileSource {
    override val id = "boss3arm$index"

    val otherArm get() = boss.arms[(index + 1) % 2]

    override var x = 0.0f
    override var y = 0.0f
    var rotation = 0.0.degrees

    val armAngle get() = rotation + baseRotation
    val parts: List<ArmPart>
    val isParalyzed get() = paralyzeTimer > 0.0.seconds || boss.isParalyzed

    private var paralyzeTimer = 0.0.seconds

    override val damageSourceX get() = boss.x
    override val damageSourceY get() = boss.y

    val claw get() = parts[1] as? Claw ?: throw IllegalStateException("Claw not found")

    override val muzzleX = 0.0f
    override val muzzleY = 0.0f
    override val muzzleRotation = 0.0.degrees
    override val projectileSourceId = "boss3"
    override val isStunMode = false

    init {
        val parts = arrayListOf<ArmPart>()

        parts.add(ArmPart(this, null, flip, 0, Game.textures.boss3arm, scale, layer))
        parts.add(Claw(this, parts[0], flip, 0, scale * 1.5f, layer))

        this.parts = parts
    }

    fun update(delta: Duration, movement: ArmMovement) {
        movement.updateParts(delta, this)

        paralyzeTimer -= delta
        if (paralyzeTimer < 0.0.seconds)
            paralyzeTimer = 0.0.seconds
    }

    fun paralyze(duration: Duration = 5.0.seconds, addAnimation: Boolean = true) {
        if (isParalyzed)
            return

        paralyzeTimer = duration

        if (addAnimation)
            parts.forEachIndexed { index, part ->
                val strength = (index / 2.0f).pow(2.0f)
                part.addEntityAnimation(ParalyzeAnimation(duration, strength))
            }
    }

    fun unparalyze() {
        paralyzeTimer = 0.0.seconds
        parts.forEach {
            it.cancelEntityAnimation<ParalyzeAnimation>()
        }
    }

    override fun onDamageHit() {
        if (boss.isInvulnerable)
            return

        Game.audio.hitEnemySound.play(0.5f)

        paralyze()
    }

    override fun onImpulseHit(x: Float, y: Float, strength: Float) {
        val distance = sqrt(x * x + y * y)

        boss.impulseX = x / distance * strength * 0.15f
        boss.impulseY = y / distance * strength * 0.15f
        boss.impulseSpin = strength * 0.15f
    }

    override fun onBaitHit() {
        boss.paralyze()
    }
}