package com.cozmicgames.states

import com.cozmicgames.Game
import com.cozmicgames.graphics.PlayerCamera
import com.cozmicgames.graphics.RenderLayers
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.Background
import com.cozmicgames.graphics.ui.ResultsPanel
import com.cozmicgames.input.InputFrame
import com.cozmicgames.states.boss1.Boss1
import com.cozmicgames.utils.Difficulty
import com.cozmicgames.utils.FightResults
import com.littlekt.math.Vec2f
import com.littlekt.math.isFuzzyZero
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.time.Duration

class Boss1State(val difficulty: Difficulty = Difficulty.NORMAL) : GameState {
    private lateinit var playerCamera: PlayerCamera
    private lateinit var background: Background
    private lateinit var boss: Boss1
    private lateinit var resultsPanel: ResultsPanel

    private var showResults = false

    private var returnState: GameState = this

    override fun begin() {
        val player = Game.players.getMyPlayer() ?: throw IllegalStateException("No current player found!")

        Game.players.shootStatistics.reset()

        Game.physics.clear()
        Game.physics.width = 2500.0f
        Game.physics.height = 2500.0f
        playerCamera = PlayerCamera(player.camera)

        background = Background(Game.resources.boss1background)

        boss = Boss1(difficulty)
        boss.addToWorld()
        boss.addToPhysics()

        val numPlayers = Game.players.players.size
        val spawnPositions = Array(numPlayers) {
            val spawnX = -Game.physics.width * 0.25f
            val spawnY = Game.physics.height * 0.5f + (it - numPlayers * 0.5f) * 200.0f
            Vec2f(spawnX, spawnY)
        }

        Game.players.players.forEachIndexed { index, p ->
            val spawnPosition = spawnPositions[index]
            p.ship.initialize(difficulty, spawnPosition.x, spawnPosition.y)
            p.ship.addToWorld()
            p.ship.addToPhysics()
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

            val cameraTargetDistance = min(playerShipToBossDistance, min(playerCamera.camera.virtualWidth, playerCamera.camera.virtualHeight) * 0.4f)
            cameraTargetX += playerShipToBossX * cameraTargetDistance
            cameraTargetY += playerShipToBossY * cameraTargetDistance
        }

        boss.update(delta)
        playerCamera.update(cameraTargetX, cameraTargetY, delta)
        Game.world.update(delta)

        if (!showResults && (boss.isDead || Game.players.players.all { it.ship.isDead })) {
            showResults = true

            if (!boss.isDead)
                boss.movementController.onFailFight()

            val averagePlayerHealth = Game.players.players.sumOf { it.ship.health }.toFloat() / Game.players.players.size
            val results = FightResults(difficulty, Boss1.FULL_HEALTH, boss.health, averagePlayerHealth, Game.players.shootStatistics.shotsFired, Game.players.shootStatistics.shotsHit)

            println(results.message)

            resultsPanel = ResultsPanel(results)
        }

        if (showResults) {
            when (resultsPanel.renderAndGetResultState(delta)) {
                ResultsPanel.ResultState.RETURN -> returnState = MenuState()
                ResultsPanel.ResultState.RETRY_EASY -> returnState = Boss1State(Difficulty.EASY)
                ResultsPanel.ResultState.RETRY_NORMAL -> returnState = Boss1State(Difficulty.NORMAL)
                ResultsPanel.ResultState.RETRY_HARD -> returnState = Boss1State(Difficulty.HARD)
                else -> {}
            }
        }

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

        if (!showResults)
            pass.render(Game.graphics.mainViewport.camera) { renderer: Renderer ->
                renderer.submit(RenderLayers.UI_BEGIN) {
                    repeat(difficulty.basePlayerHealth) { health ->
                        val texture = if (health < playerShip.health) Game.resources.playerHealthIndicator else Game.resources.playerHealthEmptyIndicator
                        it.draw(texture, -Game.graphics.width * 0.5f + 5.0f + health * 25.0f, -Game.graphics.height * 0.5f + 5.0f, width = 25.0f, height = 25.0f)
                    }
                }

                if (player.indicatorColor.a > 0.0f)
                    renderer.submit(RenderLayers.BORDER_INDICATOR) {
                        it.draw(Game.resources.borderIndicator, -Game.graphics.width.toFloat() * 0.5f, -Game.graphics.height.toFloat() * 0.5f, width = Game.graphics.width.toFloat(), height = Game.graphics.height.toFloat(), color = player.indicatorColor)
                    }
            }

        pass.end()

        return { returnState }
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