package com.cozmicgames.bosses.boss4

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.cozmicgames.physics.CircleCollisionShape
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.Hittable
import com.littlekt.graphics.Color
import com.littlekt.graphics.slice

class Head(private val boss: Boss4, headScale: Float, layer: Int) : EnemyPart("boss4head"), Hittable, PlayerDamageSource {
    override val canHit get() = !boss.isInvulnerable

    override val renderLayer = layer

    override val collider = Collider(CircleCollisionShape(headScale * 0.4f), this)

    override val width = Game.resources.boss4head.width * headScale

    override val height = Game.resources.boss4head.height * headScale

    override val texture = Game.resources.boss4head.slice()

    override val baseColor get() = boss.camouflageColor

    override val damageSourceX get() = boss.x
    override val damageSourceY get() = boss.y

    override fun onDamageHit() {
        boss.paralyze()
    }
}