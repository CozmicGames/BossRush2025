package com.cozmicgames.bosses

import com.cozmicgames.physics.Hittable

interface BossTarget : Hittable {
    val appeal get() = 0
}