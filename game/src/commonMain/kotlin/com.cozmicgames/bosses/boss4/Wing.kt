package com.cozmicgames.bosses.boss4

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.cozmicgames.physics.CircleCollisionShape
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.Hittable
import com.cozmicgames.physics.RectangleCollisionShape
import com.littlekt.graphics.slice
import com.littlekt.math.geom.cosine
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.sine
import kotlin.time.Duration

class Wing(private val boss: Boss4, private val left: Boolean, wingScale: Float, layer: Int) : EnemyPart("boss4wing${if (left) "left" else "right"}"), Hittable, PlayerDamageSource {
    override val canHit get() = !boss.isInvulnerable

    override val renderLayer = layer

    override val collider = Collider(RectangleCollisionShape(Game.resources.boss4wing.width * wingScale, Game.resources.boss4wing.height * wingScale * 0.15f, 0.0.degrees), this)

    override val width = Game.resources.boss4wing.width * wingScale

    override val height = Game.resources.boss4wing.height * wingScale

    override val flipX = !left

    override val texture = Game.resources.boss4wing.slice()

    override val baseColor get() = boss.camouflageColor

    override val damageSourceX get() = boss.x
    override val damageSourceY get() = boss.y

    val lowerCollider = Collider(RectangleCollisionShape(Game.resources.boss4wing.width * wingScale, Game.resources.boss4wing.height * wingScale * 0.15f, 0.0.degrees), this)

    override fun onDamageHit() {
        boss.paralyze()
    }

    override fun updateWorldObject(delta: Duration, fightStarted: Boolean) {
        super.updateWorldObject(delta, fightStarted)
        
        val mainColliderOffsetX = if(left) 0.1f * width else -0.1f * width
        val mainColliderOffsetY = 0.1f * height
        
        val cos = rotation.cosine
        val sin = rotation.sine
        
        val mainColliderX = x + cos * mainColliderOffsetX - sin * mainColliderOffsetY
        val mainColliderY = y + sin * mainColliderOffsetX + cos * mainColliderOffsetY

        collider.update(mainColliderX, mainColliderY)
        (collider.shape as RectangleCollisionShape).angle = rotation + if(left) 35.0.degrees else (-35.0).degrees

        val lowerColliderOffsetX = if(left) 0.2f * width else -0.2f * width
        val lowerColliderOffsetY = -0.1f * height

        val lowerColliderX = x + cos * lowerColliderOffsetX - sin * lowerColliderOffsetY
        val lowerColliderY = y + sin * lowerColliderOffsetX + cos * lowerColliderOffsetY

        lowerCollider.update(lowerColliderX, lowerColliderY)
    }
}