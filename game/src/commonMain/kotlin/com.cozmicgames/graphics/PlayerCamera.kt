package com.cozmicgames.graphics

import com.cozmicgames.Constants
import com.littlekt.graphics.Camera
import com.littlekt.math.geom.Angle
import com.littlekt.math.geom.degrees
import com.littlekt.util.seconds
import kotlin.time.Duration

class PlayerCamera(val camera: Camera) {
    var getMinX: (() -> Float)? = null
    var getMinY: (() -> Float)? = null
    var getMaxX: (() -> Float)? = null
    var getMaxY: (() -> Float)? = null

    private var rotation = 0.0.degrees

    fun update(targetX: Float, targetY: Float, targetRotation: Angle, delta: Duration) {
        camera.position.x += (targetX - camera.position.x) * delta.seconds * Constants.CAMERA_FOLLOW_MOVE_SPEED
        camera.position.y += (targetY - camera.position.y) * delta.seconds * Constants.CAMERA_FOLLOW_MOVE_SPEED

        //val deltaRotation = (targetRotation - rotation) * delta.seconds * Constants.CAMERA_FOLLOW_ROTATE_SPEED
        //rotation += deltaRotation
        //camera.up.x = rotation.cosine
        //camera.up.y = -rotation.sine //TODO: Fix this

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