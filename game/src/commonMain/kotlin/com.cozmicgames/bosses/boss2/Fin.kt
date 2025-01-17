package com.cozmicgames.bosses.boss2

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.entities.worldObjects.PlayerDamageSource
import com.cozmicgames.entities.worldObjects.ProjectileSource
import com.cozmicgames.physics.Collider
import com.littlekt.graphics.slice

class Fin(val boss: Boss2, override val flipY: Boolean, scale: Float, layer: Int) : EnemyPart("boss2fin"), PlayerDamageSource, ProjectileSource {
    override val renderLayer = layer

    override val texture = Game.resources.boss2fin.slice()

    override val width = Game.resources.boss2fin.width * scale

    override val height = Game.resources.boss2fin.height * scale

    override val flipX get() = boss.isFlipped

    override val collider = Collider(getRectangleCollisionShape(scaleX = 0.5f), this)

    override val damageSourceX get() = boss.x

    override val damageSourceY get() = boss.y

    override val projectileSourceId = "boss2"

    override val muzzleX get() = boss.muzzleX

    override val muzzleY get() = boss.muzzleY

    override val muzzleRotation get() = boss.muzzleRotation
}