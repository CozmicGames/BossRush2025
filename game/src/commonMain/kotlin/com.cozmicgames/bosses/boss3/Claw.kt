package com.cozmicgames.bosses.boss3

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.ProjectileSource
import com.cozmicgames.events.Events
import com.cozmicgames.physics.CircleCollisionShape
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.Grabbable
import com.cozmicgames.physics.GrabbingObject
import com.littlekt.math.geom.cosine
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.sine
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class Claw(arm: Arm, parent: ArmPart, flip: Boolean, index: Int, partScale: Float, layer: Int) : ArmPart(arm, parent, flip, index, Game.resources.boss3clawBase, partScale, layer), ProjectileSource, GrabbingObject {
    companion object {
        private val GRAB_COOLDOWN = 2.0.seconds
    }

    override val muzzleX: Float
        get() {
            val xOffset = if (flip) -width * 0.4f else width * 0.4f
            val yOffset = -0.1f * height
            val cos = rotation.cosine
            val sin = rotation.sine
            return x + cos * xOffset - sin * yOffset
        }

    override val muzzleY: Float
        get() {
            val xOffset = if (flip) -width * 0.4f else width * 0.4f
            val yOffset = -0.1f * height
            val cos = rotation.cosine
            val sin = rotation.sine
            return y + sin * xOffset + cos * yOffset
        }

    override val muzzleRotation get() = if (flip) rotation + 180.0.degrees else rotation
    override val projectileSourceId = "boss3"
    override val isStunMode = false

    override val grabbingId = "boss3"

    override var grabX = 0.0f

    override var grabY = 0.0f

    override var grabRotation = 0.0.degrees

    val upperClawPart = ClawPart(this, flip, true, partScale, layer)
    val lowerClawPart = ClawPart(this, flip, false, partScale, layer)

    var clawAngle = 0.0.degrees
    var hasGrabbedObject = false
        private set

    val grabCollider = Collider(CircleCollisionShape(height * 0.5f * 1.1f), null)
    private var grabbedObject: Grabbable? = null
    private var grabCooldown = 0.0.seconds

    override fun updateWorldObject(delta: Duration, fightStarted: Boolean) {
        super.updateWorldObject(delta, fightStarted)

        upperClawPart.rotation = rotation - clawAngle * 0.2f
        lowerClawPart.rotation = rotation + clawAngle * 0.8f

        grabCooldown -= delta

        val grabOffsetX = if (flip) -0.7f * width else 0.7f * width
        val grabOffsetY = 0.0f

        val cos = rotation.cosine
        val sin = rotation.sine

        grabX = x + cos * grabOffsetX - sin * grabOffsetY
        grabY = y + sin * grabOffsetX + cos * grabOffsetY
        grabRotation = rotation

        grabCollider.update(grabX, grabY)
    }

    fun tryGrabObject(): Boolean {
        if (hasGrabbedObject || grabCooldown > 0.0.seconds)
            return false

        var closestGrabbable: Grabbable? = null
        var closestDistance = Float.MAX_VALUE

        Game.physics.checkCollision(grabCollider) {
            if (it.userData is Grabbable) {
                val dx = it.x - collider.x
                val dy = it.y - collider.y
                val distance = dx * dx + dy * dy

                if (distance < closestDistance) {
                    closestGrabbable = it.userData
                    closestDistance = distance
                }
            }
        }

        closestGrabbable?.let {
            grabbedObject = it
            Game.events.addSendEvent(Events.grab(it.id, grabbingId))
        }

        return closestGrabbable != null
    }

    fun releaseGrabbedObject(impulseX: Float, impulseY: Float) {
        grabbedObject?.let {
            Game.events.addSendEvent(Events.release(it.id, impulseX, impulseY))
            grabbedObject = null
            grabCooldown = GRAB_COOLDOWN
        }
    }

    fun update(delta: Duration, movement: ClawMovement) {
        if (Game.players.isHost) {
            movement.updateClaw(delta, this)
            Game.players.setGlobalState("boss3clawAngle", clawAngle.degrees)
        } else
            clawAngle = (Game.players.getGlobalState("boss3clawAngle") ?: 0.0f).degrees
    }
}