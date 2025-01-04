package com.cozmicgames.graphics

import com.cozmicgames.Constants
import com.littlekt.graphics.Camera
import com.littlekt.util.seconds
import kotlin.time.Duration

class PlayerCamera(val camera: Camera) {
    var getMinX: (() -> Float)? = null
    var getMinY: (() -> Float)? = null
    var getMaxX: (() -> Float)? = null
    var getMaxY: (() -> Float)? = null

    fun update(targetX: Float, targetY: Float, delta: Duration) {
        camera.position.x += (targetX - camera.position.x) * delta.seconds * Constants.CAMERA_FOLLOW_MOVE_SPEED
        camera.position.y += (targetY - camera.position.y) * delta.seconds * Constants.CAMERA_FOLLOW_MOVE_SPEED

        getMinX?.invoke()?.let {
            if (camera.position.x < it)
                camera.position.x = it
        }

        getMinY?.invoke()?.let {
            if (camera.position.y < it)
                camera.position.y = it
        }

        getMaxX?.invoke()?.let {
            if (camera.position.x > it)
                camera.position.x = it
        }

        getMaxY?.invoke()?.let {
            if (camera.position.y > it)
                camera.position.y = it
        }

        camera.update()
    }
}