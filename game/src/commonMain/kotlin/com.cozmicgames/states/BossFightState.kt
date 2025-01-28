package com.cozmicgames.states

import com.cozmicgames.Game
import com.cozmicgames.bosses.Boss
import com.cozmicgames.bosses.BossDesc
import com.cozmicgames.bosses.SpawnPosition
import com.cozmicgames.entities.worldObjects.AsteroidManager
import com.cozmicgames.graphics.Background
import com.cozmicgames.graphics.PlayerCamera
import com.cozmicgames.graphics.RenderLayers
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.*
import com.cozmicgames.input.InputFrame
import com.cozmicgames.utils.Difficulty
import com.cozmicgames.utils.FightResults
import com.littlekt.math.Vec2f
import com.littlekt.math.geom.cosine
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.radians
import com.littlekt.math.geom.sine
import com.littlekt.math.isFuzzyZero
import kotlin.math.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class BossFightState(val desc: BossDesc, var difficulty: Difficulty) : GameState {
    private val fightId = Game.random.nextLong()

    private lateinit var playerCamera: PlayerCamera
    private lateinit var guiCamera: GUICamera
    private lateinit var background: Background
    private lateinit var boss: Boss
    private var resultPanel: ResultPanel? = null
    private var fightStartMessage: FightStartMessage? = null
    private var ingameUI: IngameUI? = null

    private var fightDuration = 0.0.seconds
    private var fightStarted = false
    private var showResults = false
    private val asteroids = AsteroidManager(difficulty)
    private var returnState: GameState = this

    override fun begin() {
        val player = Game.players.getMyPlayer() ?: throw IllegalStateException("No current player found!")
        playerCamera = PlayerCamera(player.camera)
        guiCamera = GUICamera()
        background = Background(Game.resources.background)

        startFight(difficulty, false)
    }

    private fun startFight(difficulty: Difficulty, isRetry: Boolean) {
        val player = Game.players.getMyPlayer() ?: throw IllegalStateException("No current player found!")

        this.difficulty = difficulty
        fightDuration = 0.0.seconds
        resultPanel = null
        fightStarted = false
        showResults = false

        Game.players.shootStatistics.reset()

        Game.world.clear()
        Game.physics.clear()
        Game.physics.width = 2500.0f
        Game.physics.height = 2500.0f

        if (!isRetry)
            asteroids.initialize()

        boss = desc.createBoss(difficulty)
        boss.addToWorld()
        boss.addToPhysics()

        val numPlayers = Game.players.players.size

        val spawnPositions: Array<Vec2f>

        if (numPlayers == 1) {
            spawnPositions = when (desc.centerSpawnPosition) {
                SpawnPosition.TOP_LEFT -> arrayOf(Vec2f(Game.physics.minX + 200.0f, Game.physics.maxY - 200.0f))
                SpawnPosition.TOP_RIGHT -> arrayOf(Vec2f(Game.physics.maxX - 200.0f, Game.physics.maxY - 200.0f))
                SpawnPosition.BOTTOM_LEFT -> arrayOf(Vec2f(Game.physics.minX + 200.0f, Game.physics.minY + 200.0f))
                SpawnPosition.BOTTOM_RIGHT -> arrayOf(Vec2f(Game.physics.maxX - 200.0f, Game.physics.minY + 200.0f))
            }
        } else {
            val centerSpawnPosition = when (desc.centerSpawnPosition) {
                SpawnPosition.TOP_LEFT -> Vec2f(Game.physics.minX + 200.0f, Game.physics.maxY - 200.0f)
                SpawnPosition.TOP_RIGHT -> Vec2f(Game.physics.maxX - 200.0f, Game.physics.maxY - 200.0f)
                SpawnPosition.BOTTOM_LEFT -> Vec2f(Game.physics.minX + 200.0f, Game.physics.minY + 200.0f)
                SpawnPosition.BOTTOM_RIGHT -> Vec2f(Game.physics.maxX - 200.0f, Game.physics.minY + 200.0f)
            }

            spawnPositions = Array(numPlayers) {
                val radius = 200.0f
                val angle = 360.0.degrees / numPlayers * it
                val spawnX = centerSpawnPosition.x + radius * angle.cosine
                val spawnY = centerSpawnPosition.y + radius * angle.sine
                Vec2f(spawnX, spawnY)
            }
        }

        Game.players.players.forEachIndexed { index, p ->
            val spawnPosition = spawnPositions[index]
            val spawnRotation = atan2(spawnPosition.y - boss.y, spawnPosition.x - boss.x).radians + 180.0.degrees
            p.ship.initialize(difficulty, spawnPosition.x, spawnPosition.y, spawnRotation, false)
            p.ship.addToWorld()
            p.ship.addToPhysics()
        }

        Game.world.shouldUpdate = false

        fightStartMessage = FightStartMessage()
        fightStartMessage?.startAnimation {
            fightStartMessage = null
            fightStarted = true
            Game.world.shouldUpdate = true
        }

        ingameUI = IngameUI(player.ship, difficulty)
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

        asteroids.update(delta, fightStarted)
        Game.particles.update(delta)
        Game.world.update(delta, fightStarted)
        playerCamera.update(cameraTargetX, cameraTargetY, delta)

        if (!showResults)
            fightDuration += delta

        if (!showResults && (boss.isDead || Game.players.players.all { it.ship.isDead })) {
            showResults = true
            Game.world.shouldUpdate = false

            if (!boss.isDead)
                boss.movementController.onFailFight()
            else
                Game.players.newlyUnlockedBossIndex = desc.unlockedBossIndex

            val averagePlayerHealth = round(Game.players.players.sumOf { it.ship.health }.toFloat() / Game.players.players.size).toInt()
            val results = FightResults(fightDuration, difficulty, desc.fullHealth, boss.health, averagePlayerHealth, Game.players.shootStatistics.shotsFired, Game.players.shootStatistics.shotsHit)

            resultPanel = ResultPanel(results)
        }

        val pass = Game.graphics.beginMainRenderPass()

        pass.render(playerCamera.camera) { renderer: Renderer ->
            background.render(delta, renderer)

            Game.world.render(renderer)

            boss.renderSpecials(delta, renderer)

            renderer.submit(RenderLayers.PROJECTILES) {
                Game.projectiles.render(it)
            }

            renderer.submit(RenderLayers.AREA_EFFECTS) {
                Game.areaEffects.render(it)
            }

            renderer.submit(RenderLayers.PARTICLES) {
                Game.particles.render(it)
            }
        }

        pass.render(guiCamera.camera) { renderer: Renderer ->
            ingameUI?.render(delta, renderer)

            if (!fightStarted)
                fightStartMessage?.render(delta, renderer)
        }

        //pass.renderShapes(playerCamera.camera) { renderer: ShapeRenderer ->
        //    boss.drawDebug(renderer)
        //}

        if (!showResults) //TODO: Rework this, move to UI
            pass.render(Game.graphics.mainViewport.camera) { renderer: Renderer ->
                if (player.indicatorColor.a > 0.0f)
                    renderer.submit(RenderLayers.BORDER_INDICATOR) {
                        it.draw(Game.resources.borderIndicator, -Game.graphics.width.toFloat() * 0.5f, -Game.graphics.height.toFloat() * 0.5f, width = Game.graphics.width.toFloat(), height = Game.graphics.height.toFloat(), color = player.indicatorColor)
                    }
            }
        else {
            pass.render(guiCamera.camera) { renderer: Renderer ->
                when (resultPanel?.renderAndGetResultState(delta, renderer)) {
                    ResultPanel.ResultState.RETURN -> returnState = BayState()
                    ResultPanel.ResultState.RETRY_EASY -> startFight(Difficulty.EASY, true)
                    ResultPanel.ResultState.RETRY_NORMAL -> startFight(Difficulty.NORMAL, true)
                    ResultPanel.ResultState.RETRY_HARD -> startFight(Difficulty.HARD, true)
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BossFightState) return false

        if (desc != other.desc) return false
        if (difficulty != other.difficulty) return false

        return fightId == other.fightId
    }

    override fun hashCode(): Int {
        var result = desc.hashCode()
        result = 31 * result + difficulty.hashCode()
        result = 31 * result + fightId.hashCode()
        return result
    }
}