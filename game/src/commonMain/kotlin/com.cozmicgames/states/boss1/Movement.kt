package com.cozmicgames.states.boss1

import com.littlekt.math.geom.Angle

sealed interface Movement

class WaveMovement(val maxAngle: Angle, val frequency: Float, val smoothFactor: Float) : Movement

class GrabMovement(val targetAngle: Angle, val grabFactor: Float) : Movement

class CurlMovement(val curlFactor: Float) : Movement