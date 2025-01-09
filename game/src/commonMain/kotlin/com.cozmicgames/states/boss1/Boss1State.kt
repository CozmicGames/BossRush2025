package com.cozmicgames.states.boss1

import com.cozmicgames.Game
import com.cozmicgames.graphics.PlayerCamera
import com.cozmicgames.graphics.RenderLayers
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.Background
import com.cozmicgames.input.InputFrame
import com.cozmicgames.states.GameState
import com.littlekt.math.MutableVec2f
import kotlin.time.Duration

class Boss1State : GameState {
    private lateinit var playerCamera: PlayerCamera
    private lateinit var background: Background
    private lateinit var boss: Boss1

    override fun begin() {
        playerCamera = PlayerCamera(Game.graphics.mainViewport.camera)

        background = Background(Game.resources.boss1background)

        boss = Boss1()
        boss.addToEntities()
        boss.addToPhysics()
    }

    override fun render(delta: Duration): () -> GameState {
        val player = Game.players.getMyPlayer() ?: throw IllegalStateException("No current player found!")
        val playerShip = player.ship

        val inputFrame = InputFrame()
        Game.input.update(delta, inputFrame)
        Game.players.getMyPlayerState().let {
            it.setState("inputX", inputFrame.deltaX)
            it.setState("inputY", inputFrame.deltaY)
            it.setState("inputRotation", inputFrame.deltaRotation)
            it.setState("inputUsePrimary", inputFrame.usePrimary)
            it.setState("inputUseSecondary", inputFrame.useSecondary)
        }

        boss.update(delta)
        playerCamera.update(playerShip.x, playerShip.y, playerShip.rotation, delta)
        Game.entities.update(delta)

        val pass = Game.graphics.beginMainRenderPass()

        pass.render(playerCamera.camera) { renderer: Renderer ->
            background.render(renderer)

            Game.entities.render(renderer)

            renderer.submit(RenderLayers.PROJECTILES_BEGIN) {
                Game.projectiles.render(it)
            }
        }

        pass.end()

        return { this }
    }
}