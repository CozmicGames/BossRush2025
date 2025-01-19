package com.cozmicgames.states

import com.cozmicgames.Game
import com.cozmicgames.bosses.Boss
import com.cozmicgames.bosses.BossDesc
import com.cozmicgames.graphics.Background
import com.cozmicgames.graphics.PlayerCamera
import com.cozmicgames.graphics.RenderLayers
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.FightStartMessage
import com.cozmicgames.graphics.ui.GUICamera
import com.cozmicgames.graphics.ui.ResultPanel
import com.cozmicgames.input.InputFrame
import com.cozmicgames.utils.Difficulty
import com.cozmicgames.utils.FightResults
import com.littlekt.graphics.g2d.shape.ShapeRenderer
import com.littlekt.math.Vec2f
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.radians
import com.littlekt.math.isFuzzyZero
import kotlin.math.atan2
import kotlin.math.min
import kotlin.math.round
import kotlin.math.sqrt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class BossFightState(val desc: BossDesc, val difficulty: Difficulty) : GameState {
    private lateinit var playerCamera: PlayerCamera
    private lateinit var guiCamera: GUICamera
    private lateinit var background: Background
    private lateinit var boss: Boss
    private lateinit var resultPanel: ResultPanel
    private var fightStartMessage: FightStartMessage? = FightStartMessage()

    private var fightDuration = 0.0.seconds
    private var fightStarted = false
    private var showResults = false
    private var returnState: GameState = this

    override fun begin() {
        val player = Game.players.getMyPlayer() ?: throw IllegalStateException("No current player found!")

        Game.players.shootStatistics.reset()

        Game.physics.clear()
        Game.physics.width = 2500.0f
        Game.physics.height = 2500.0f
        playerCamera = PlayerCamera(player.camera)
        guiCamera = GUICamera()

        background = Background(Game.resources.background)

        boss = desc.createBoss(difficulty)
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
            val spawnRotation = atan2(spawnPosition.y - boss.y, spawnPosition.x - boss.x).radians + 180.0.degrees
            p.ship.initialize(difficulty, spawnPosition.x, spawnPosition.y, spawnRotation)
            p.ship.addToWorld()
            p.ship.addToPhysics()
        }

        Game.world.shouldUpdate = false

        fightStartMessage?.startAnimation {
            fightStartMessage = null
            fightStarted = true
            Game.world.shouldUpdate = true
        }
    }

    override fun resize(width: Int, height: Int) {
        playerCamera.resize(width, height)
        guiCamera.resize(width, height)
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

        if (fightStarted)
            boss.update(delta)

        Game.world.update(delta, fightStarted)
        playerCamera.update(cameraTargetX, cameraTargetY, delta)

        if (!showResults)
            fightDuration += delta

        if (!showResults && (boss.isDead || Game.players.players.all { it.ship.isDead })) {
            showResults = true
            Game.world.shouldUpdate = false

            if (!boss.isDead)
                boss.movementController.onFailFight()

            val averagePlayerHealth = round(Game.players.players.sumOf { it.ship.health }.toFloat() / Game.players.players.size).toInt()
            val results = FightResults(fightDuration, difficulty, desc.fullHealth, boss.health, averagePlayerHealth, Game.players.shootStatistics.shotsFired, Game.players.shootStatistics.shotsHit)

            resultPanel = ResultPanel(results)
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

        if (!fightStarted)
            pass.render(guiCamera.camera) { renderer: Renderer ->
                fightStartMessage?.render(delta, renderer)
            }

        pass.renderShapes(playerCamera.camera) { renderer: ShapeRenderer ->
            boss.drawDebug(renderer)
        }

        if (!showResults) //TODO: Rework this, move to UI
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
        else {
            pass.render(guiCamera.camera) { renderer: Renderer ->
                when (resultPanel.renderAndGetResultState(delta, renderer)) {
                    ResultPanel.ResultState.RETURN -> returnState = BayState()
                    ResultPanel.ResultState.RETRY_EASY -> returnState = BossFightState(desc, Difficulty.EASY)
                    ResultPanel.ResultState.RETRY_NORMAL -> returnState = BossFightState(desc, Difficulty.NORMAL)
                    ResultPanel.ResultState.RETRY_HARD -> returnState = BossFightState(desc, Difficulty.HARD)
                    else -> {}
                }
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