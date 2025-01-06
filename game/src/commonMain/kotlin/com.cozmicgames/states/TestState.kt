package com.cozmicgames.states

import com.cozmicgames.Game
import com.cozmicgames.graphics.PlayerCamera
import com.cozmicgames.graphics.RenderLayers
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.input.InputFrame
import com.cozmicgames.states.boss1.Boss1
import kotlin.time.Duration

class TestState : GameState {
    lateinit var playerCamera: PlayerCamera

    lateinit var boss: Boss1

    override fun begin() {
        playerCamera = PlayerCamera(Game.graphics.mainViewport.camera)

        boss = Boss1()
    }

    override fun resize(width: Int, height: Int) {
    }

    override fun render(delta: Duration): () -> GameState {
        val player = Game.players.getMyPlayer() ?: throw IllegalStateException("No current player found!")

        val inputFrame = InputFrame()
        val playerShip = player.ship

        boss.update(delta)

        playerCamera.update(playerShip.x, playerShip.y, playerShip.rotation, delta)

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
            renderer.submit(RenderLayers.BACKGROUND) {
                it.draw(Game.resources.testBackgroundTexture, 0.0f, 0.0f, originX = 2048.0f, originY = 2048.0f, width = 4096.0f, height = 4096.0f)
            }

            Game.entities.render(renderer)

            renderer.submit(RenderLayers.PROJECTILES_BEGIN) {
                Game.projectiles.render(it)
            }
        }

        //pass.renderShapes(playerCamera.camera) { renderer ->
        //    Game.physics.colliders.forEach {
        //        it.drawDebug(renderer)
        //    }
        //}

        pass.end()

        return { this }
    }

    override fun end() {

    }
}