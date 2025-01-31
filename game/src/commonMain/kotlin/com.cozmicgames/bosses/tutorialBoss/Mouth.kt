package com.cozmicgames.bosses.tutorialBoss

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.Hittable
import com.cozmicgames.physics.RectangleCollisionShape
import com.littlekt.graphics.slice
import com.littlekt.math.geom.cosine
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.sine
import kotlin.math.sqrt
import kotlin.time.Duration

class Mouth(private val boss: TutorialBoss, scale: Float, layer: Int) : EnemyPart("bossTutorialMouth"), Hittable, PlayerDamageSource {

    override val renderLayer = layer

    override val width = Game.textures.bossTutorialMouth.width * scale

    override val height = Game.textures.bossTutorialMouth.height * scale

    override val flipX get() = boss.isFlipped

    override val collider = Collider(getRectangleCollisionShape(0.7f, scaleY = 0.3f), this)

    override var texture = Game.textures.bossTutorialMouth.slice()

    override val damageSourceX get() = boss.x
    override val damageSourceY get() = boss.y

    var mouthAngle = 0.0.degrees

    fun update(delta: Duration, movement: MouthMovement) {
        movement.updateMouth(delta, this)
    }

    override fun updateWorldObject(delta: Duration, isFighting: Boolean) {
        super.updateWorldObject(delta, isFighting)

        val mouthColliderOffsetX = 0.0f
        val mouthColliderOffsetY = -height * 0.2f

        val cos = rotation.cosine
        val sin = rotation.sine

        val mouthColliderX = x + cos * mouthColliderOffsetX - sin * mouthColliderOffsetY
        val mouthColliderY = y + sin * mouthColliderOffsetX + cos * mouthColliderOffsetY

        (collider.shape as RectangleCollisionShape).angle = rotation + mouthAngle
        collider.update(mouthColliderX, mouthColliderY)
    }

    override fun onDamageHit() {
        Game.audio.hitEnemySound.play(0.5f)

        boss.paralyze()
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