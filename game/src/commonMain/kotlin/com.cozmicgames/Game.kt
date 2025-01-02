package com.cozmicgames

import com.cozmicgames.graphics.Graphics2D
import com.cozmicgames.multiplayer.Multiplayer
import com.littlekt.Context
import com.littlekt.ContextListener
import com.littlekt.graphics.Color
import com.littlekt.graphics.HAlign
import com.littlekt.graphics.g2d.SpriteBatch
import com.littlekt.graphics.g2d.shape.ShapeRenderer
import com.littlekt.graphics.g2d.use
import com.littlekt.graphics.webgpu.*
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.radians
import com.littlekt.resources.Fonts
import com.littlekt.util.viewport.ExtendViewport
import kotlin.time.Duration.Companion.milliseconds

class Game(multiplayer: Multiplayer, context: Context) : ContextListener(context) {
    override suspend fun Context.start() {
        val g = Graphics2D(this)

        onResize { width, height ->
            g.resize(width, height)
        }
        onUpdate { dt ->
            g.beginFrame()
            g.drawFrame()
            g.endFrame()
        }

        onRelease {
            g.release()
        }
    }
}