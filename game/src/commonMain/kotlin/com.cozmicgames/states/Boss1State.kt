package com.cozmicgames.states

import com.cozmicgames.Game
import com.cozmicgames.graphics.PlayerCamera
import com.cozmicgames.graphics.RenderLayers
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.Background
import com.cozmicgames.input.InputFrame
import com.cozmicgames.states.boss1.Boss1
import com.littlekt.math.isFuzzyZero
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.time.Duration

class Boss1State : GameState {
    private lateinit var playerCamera: PlayerCamera
    private lateinit var background: Background
    private lateinit var boss: Boss1

    override fun begin() {
        val player = Game.players.getMyPlayer() ?: throw IllegalStateException("No current player found!")

        Game.physics.clear()
        Game.physics.width = 2500.0f
        Game.physics.height = 2500.0f
        playerCamera = PlayerCamera(player.camera)

        background = Background(Game.resources.boss1background)

        boss = Boss1()
        boss.addToWorld()
        boss.addToPhysics()

        Game.players.players.forEach {
            it.ship.addToWorld()
            it.ship.addToPhysics()
        }
    }

    override fun resize(width: Int, height: Int) {
        playerCamera.camera.virtualWidth = width.toFloat()
        playerCamera.camera.virtualHeight = height.toFloat()
        playerCamera.camera.update()
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

        var cameraTargetX = playerShip.x
        var cameraTargetY = playerShip.y

        var playerShipToBossX = boss.x - playerShip.x
        var playerShipToBossY = boss.y - playerShip.y
        val playerShipToBossDistance = sqrt(playerShipToBossX * playerShipToBossX + playerShipToBossY * playerShipToBossY)

        if (!playerShipToBossDistance.isFuzzyZero()) {
            playerShipToBossX /= playerShipToBossDistance
            playerShipToBossY /= playerShipToBossDistance

            val cameraTargetDistance = min(playerShipToBossDistance, min(playerCamera.camera.virtualWidth, playerCamera.camera.virtualHeight) * 0.25f)
            cameraTargetX += playerShipToBossX * cameraTargetDistance
            cameraTargetY += playerShipToBossY * cameraTargetDistance
        }

        boss.update(delta)
        playerCamera.update(cameraTargetX, cameraTargetY, playerShip.rotation, delta)
        Game.world.update(delta)

        val pass = Game.graphics.beginMainRenderPass()

        pass.render(playerCamera.camera) { renderer: Renderer ->
            background.render(renderer)

            Game.world.render(renderer)

            renderer.submit(RenderLayers.PROJECTILES_BEGIN) {
                Game.projectiles.render(it)
            }

            renderer.submit(RenderLayers.AREA_EFFECTS_BEGIN) {
                Game.areaEffects.render(it)
            }
        }

        //pass.renderShapes(playerCamera.camera) { renderer: ShapeRenderer ->
        //    boss.drawDebug(renderer)
        //}

        if (player.indicatorColor.a > 0.0f)
            pass.render(Game.graphics.mainViewport.camera) { renderer: Renderer ->
                renderer.submit(RenderLayers.BORDER_INDICATOR) {
                    it.draw(Game.resources.borderIndicator, -Game.graphics.width.toFloat() * 0.5f, -Game.graphics.height.toFloat() * 0.5f, width = Game.graphics.width.toFloat(), height = Game.graphics.height.toFloat(), color = player.indicatorColor)
                }
            }

        pass.end()

        return { this }
    }

    override fun end() {
        boss.removeFromPhysics()
        boss.removeFromWorld()

        Game.players.players.forEach {
            it.ship.removeFromPhysics()
            it.ship.removeFromWorld()
        }
    }
}