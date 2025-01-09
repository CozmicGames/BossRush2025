package com.cozmicgames.states.boss1

import com.cozmicgames.Game
import com.cozmicgames.graphics.PlayerCamera
import com.cozmicgames.graphics.RenderLayers
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.Background
import com.cozmicgames.input.InputFrame
import com.cozmicgames.states.GameState
import com.littlekt.graphics.g2d.shape.ShapeRenderer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class Boss1State : GameState {
    private lateinit var playerCamera: PlayerCamera
    private lateinit var background: Background
    private lateinit var boss: Boss1
    private lateinit var fightGraph: FightGraph

    override fun begin() {
        playerCamera = PlayerCamera(Game.graphics.mainViewport.camera)

        background = Background(Game.resources.boss1background)

        boss = Boss1()
        boss.addToEntities()
        boss.addToPhysics()

        fightGraph = FightGraph(boss)
        fightGraph.addNode(WaitNode(5.0.seconds))
        fightGraph.addNode(AttackNode(SpinAttack()))
        fightGraph.addNode(WaitNode(5.0.seconds))
        fightGraph.addNode(AttackNode(GrabAttack()))
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

        fightGraph.update(delta)
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

        pass.renderShapes(playerCamera.camera) { renderer: ShapeRenderer ->
            boss.drawDebug(renderer)
        }

        pass.end()

        return { this }
    }
}