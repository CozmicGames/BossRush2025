package com.cozmicgames.bosses

import com.cozmicgames.physics.Hittable

interface BossHittable: Hittable {
    val boss: Boss
}