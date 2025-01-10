package com.cozmicgames.states.boss1

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.EnemyPart
import com.cozmicgames.physics.CircleCollisionShape
import com.cozmicgames.physics.Collider
import com.cozmicgames.physics.Hittable
import com.littlekt.graphics.slice
import com.littlekt.util.seconds
import kotlin.math.sin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class Heart(val boss: Boss1, layer: Int) : EnemyPart("boss1heart"), Hittable {
    override val canHit get() = boss.isParalyzed

    override val renderLayer = layer

    override val width get() = Game.resources.boss1heart.width * 2.0f * size

    override val height get() = Game.resources.boss1heart.height * 2.0f * size

    override val collider = Collider(CircleCollisionShape(Game.resources.boss1heart.width * 1.5f), this)

    override val texture = Game.resources.boss1heart.slice()

    private var size = 1.0f
    private var timer = 0.0.seconds

    override fun updateWorldObject(delta: Duration) {
        timer += delta

        size = 1.0f + sin(timer.seconds * 3.0f) * 0.2f

        collider.x = x
        collider.y = y
        collider.update()
        x = collider.x
        y = collider.y
    }

    override fun onDamageHit() {
        if (boss.isParalyzed && !boss.isInvulnerable)
            boss.hit()
    }
}