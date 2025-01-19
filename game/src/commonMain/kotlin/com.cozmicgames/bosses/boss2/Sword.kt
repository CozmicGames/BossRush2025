package com.cozmicgames.bosses.boss2

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.RectangleCollisionShape
import com.littlekt.graphics.slice
import kotlin.time.Duration

class Sword(private val boss: Boss2, scale: Float, layer: Int) : EnemyPart("enemy2sword"), PlayerDamageSource {
    override val renderLayer = layer

    override val width = Game.resources.boss2sword.width * scale

    override val height = Game.resources.boss2sword.height * scale

    override val flipX get() = boss.flip

    override val collider = Collider(getRectangleCollisionShape(scaleY = 0.4f), this)

    override val texture = Game.resources.boss2sword.slice()

    override val damageSourceX get() = boss.x
    override val damageSourceY get() = boss.y

    override fun updateWorldObject(delta: Duration, fightStarted: Boolean) {
        super.updateWorldObject(delta, fightStarted)

        println("Collider x = ${collider.x}, y = ${collider.y}  width = ${(collider.shape as RectangleCollisionShape).width}, height = ${(collider.shape as RectangleCollisionShape).height}")
    }
}