package com.cozmicgames.states

import com.cozmicgames.Game
import com.cozmicgames.entities.EnemyPart
import com.cozmicgames.graphics.PlayerCamera
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.input.InputFrame
import com.littlekt.util.viewport.ExtendViewport
import com.littlekt.util.viewport.Viewport
import kotlin.time.Duration

class TestState : GameState {
    lateinit var viewport: Viewport
    lateinit var playerCamera: PlayerCamera
    lateinit var enemy: EnemyPart

    override fun begin() {
        viewport = ExtendViewport(Game.context.graphics.width, Game.context.graphics.height)
        playerCamera = PlayerCamera(Game.graphics.mainViewport.camera)

        enemy = EnemyPart("testEnemy")
        Game.entities.add(enemy)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, false)
    }

    override fun render(delta: Duration): () -> GameState {
        val player = Game.players.getMyPlayer() ?: throw IllegalStateException("No current player found!")

        val inputFrame = InputFrame()
        val playerShip = player.ship

        playerCamera.update(playerShip.x, playerShip.y, delta)

        Game.input.update(delta, inputFrame)
        Game.players.getMyPlayerState().let {
            it.setState("inputX", inputFrame.deltaX)
            it.setState("inputY", inputFrame.deltaY)
            it.setState("inputRotation", inputFrame.deltaRotation)
            it.setState("inputUsePrimary", inputFrame.usePrimary)
            it.setState("inputUseSecondary", inputFrame.useSecondary)
        }

        Game.entities.update(delta)

        val pass = Game.graphics.beginMainRenderPass()

        pass.render(playerCamera.camera) { renderer: Renderer ->
            renderer.submit(-100) {
                it.draw(Game.resources.testBackgroundTexture, 0.0f, 0.0f, originX = 1024.0f, originY = 1024.0f, width = 2048.0f, height = 2048.0f)
            }

            Game.entities.render(renderer)

            renderer.submit(100) {
                Game.projectiles.render(it)
            }
        }

        pass.end()

        return { this }
    }

    override fun end() {

    }
}