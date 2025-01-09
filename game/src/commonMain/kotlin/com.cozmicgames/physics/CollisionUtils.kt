package com.cozmicgames.physics

import com.littlekt.math.geom.Angle
import com.littlekt.math.geom.cosine
import com.littlekt.math.geom.sine
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

object CollisionUtils {
    fun collideCircleCircle(x0: Float, y0: Float, r0: Float, x1: Float, y1: Float, r1: Float): Boolean {
        val d = (x0 - x1) * (x0 - x1) + (y0 - y1) * (y0 - y1)
        return d <= (r0 + r1) * (r0 + r1)
    }


    private fun getRectangleCorners(x: Float, y: Float, width: Float, height: Float, angle: Angle, corners: FloatArray) {
        val cosTheta = angle.cosine
        val sinTheta = angle.sine
        val halfWidth = width * 0.5f
        val halfHeight = height * 0.5f

        val dx = arrayOf(-halfWidth, halfWidth, halfWidth, -halfWidth)
        val dy = arrayOf(-halfHeight, -halfHeight, halfHeight, halfHeight)

        for (i in 0..3) {
            corners[i * 2] = x + dx[i] * cosTheta - dy[i] * sinTheta
            corners[i * 2 + 1] = y + dx[i] * sinTheta + dy[i] * cosTheta
        }
    }

    private fun closestPointOnRectangle(circleX: Float, circleY: Float, rectangleX: Float, rectangleY: Float, rectangleWidth: Float, rectangleHeight: Float, rectangleAngle: Angle): Pair<Float, Float> {
        val corners = FloatArray(8)
        getRectangleCorners(rectangleX, rectangleY, rectangleWidth, rectangleHeight, rectangleAngle, corners)

        var closestX = circleX
        var closestY = circleY
        var minDistSquared = Float.POSITIVE_INFINITY

        for (i in 0..3) {
            val x1 = corners[i * 2]
            val y1 = corners[i * 2 + 1]
            val x2 = corners[(i * 2 + 2) % 8]
            val y2 = corners[(i * 2 + 3) % 8]

            val dx = circleX - x1
            val dy = circleY - y1

            val ex = x2 - x1
            val ey = y2 - y1

            val lengthSquared = ex * ex + ey * ey
            val t = max(0.0f, min(1.0f, (dx * ex + dy * ey) / lengthSquared))

            val px = x1 + t * ex
            val py = y1 + t * ey

            val distSquared = (circleX - px) * (circleX - px) + (circleY - py) * (circleY - py)
            if (distSquared < minDistSquared) {
                minDistSquared = distSquared
                closestX = px
                closestY = py
            }
        }

        return closestX to closestY
    }

    fun project(corners: FloatArray, axisX: Float, axisY: Float): Pair<Float, Float> {
        var min = Float.POSITIVE_INFINITY
        var max = Float.NEGATIVE_INFINITY

        for (i in 0..3) {
            val projection = corners[i * 2] * axisX + corners[i * 2 + 1] * axisY
            min = min(min, projection)
            max = max(max, projection)
        }

        return min to max
    }

    fun axesOverlap(cornersA: FloatArray, cornersB: FloatArray): Boolean {
        for (i in 0..3) {
            val x1 = cornersA[i * 2]
            val y1 = cornersA[i * 2 + 1]
            val x2 = cornersA[(i * 2 + 2) % 8]
            val y2 = cornersA[(i * 2 + 3) % 8]

            val axisX = -(y2 - y1)
            val axisY = x2 - x1
            val length = sqrt(axisX * axisX + axisY * axisY)

            val normalizedAxisX = axisX / length
            val normalizedAxisY = axisY / length

            val projectionA = project(cornersA, normalizedAxisX, normalizedAxisY)
            val projectionB = project(cornersB, normalizedAxisX, normalizedAxisY)

            if (projectionA.second < projectionB.first || projectionB.second < projectionA.first) {
                return false
            }
        }

        return true
    }

    fun collideCircleRectangle(circleX: Float, circleY: Float, circleRadius: Float, rectangleX: Float, rectangleY: Float, rectangleWidth: Float, rectangleHeight: Float, rectangleAngle: Angle): Boolean {
        val (closestX, closestY) = closestPointOnRectangle(circleX, circleY, rectangleX, rectangleY, rectangleWidth, rectangleHeight, rectangleAngle)
        val dx = circleX - closestX
        val dy = circleY - closestY
        return dx * dx + dy * dy <= circleRadius * circleRadius
    }

    fun collideRectangleRectangle(x0: Float, y0: Float, w0: Float, h0: Float, r0: Angle, x1: Float, y1: Float, w1: Float, h1: Float, r1: Angle): Boolean {
        val cornersA = FloatArray(8)
        val cornersB = FloatArray(8)

        getRectangleCorners(x0, y0, w0, h0, r0, cornersA)
        getRectangleCorners(x1, y1, w1, h1, r1, cornersB)

        return axesOverlap(cornersA, cornersB) && axesOverlap(cornersB, cornersA)
    }

    fun collideLineCircle(x0: Float, y0: Float, x1: Float, y1: Float, circleX: Float, circleY: Float, circleRadius: Float, callback: (Float) -> Unit = {}): Boolean {
        val dx = x1 - x0
        val dy = y1 - y0

        val fx = x0 - circleX
        val fy = y0 - circleY

        val a = dx * dx + dy * dy
        val b = 2 * (fx * dx + fy * dy)
        val c = (fx * fx + fy * fy) - circleRadius * circleRadius

        val discriminant = b * b - 4 * a * c

        if (discriminant < 0)
            return false
        else {
            val sqrtDiscriminant = sqrt(discriminant)
            val t1 = (-b - sqrtDiscriminant) / (2 * a)
            val t2 = (-b + sqrtDiscriminant) / (2 * a)

            if (t1 in 0.0f..1.0f) {
                callback(t1)
                return true
            }

            if (t2 in 0.0f..1.0f) {
                callback(t2)
                return true
            }

            return false
        }
    }

    fun collideLineRectangle(x0: Float, y0: Float, x1: Float, y1: Float, rectangleX: Float, rectangleY: Float, rectangleWidth: Float, rectangleHeight: Float, rectangleAngle: Angle, callback: (Float) -> Unit = {}): Boolean {
        val corners = FloatArray(8)
        getRectangleCorners(rectangleX, rectangleY, rectangleWidth, rectangleHeight, rectangleAngle, corners)

        val dx = x1 - x0
        val dy = y1 - y0

        var result = false
        var smallestT1 = Float.POSITIVE_INFINITY

        for (i in 0..3) {
            val xA = corners[i * 2]
            val yA = corners[i * 2 + 1]
            val xB = corners[(i * 2 + 2) % 8]
            val yB = corners[(i * 2 + 3) % 8]

            val ex = xB - xA
            val ey = yB - yA

            val t1 = (ex * (y0 - yA) - ey * (x0 - xA)) / (dx * ey - dy * ex)
            val t2 = (dx * (y0 - yA) - dy * (x0 - xA)) / (dx * ey - dy * ex)

            if (t1 in 0.0f..1.0f && t2 >= 0.0f && t2 <= 1.0f) {
                if (t1 < smallestT1)
                    smallestT1 = t1

                result = true
            }
        }

        if (result)
            callback(smallestT1)

        return result
    }
}