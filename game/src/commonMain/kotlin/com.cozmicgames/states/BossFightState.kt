package com.cozmicgames.states

import com.cozmicgames.Game
import com.cozmicgames.bosses.Boss
import com.cozmicgames.bosses.BossDesc
import com.cozmicgames.bosses.SpawnPosition
import com.cozmicgames.entities.worldObjects.AsteroidManager
import com.cozmicgames.graphics.*
import com.cozmicgames.graphics.ui.*
import com.cozmicgames.utils.Difficulty
import com.cozmicgames.utils.FightResults
import com.cozmicgames.utils.HighscoreEntry
import com.littlekt.math.Vec2f
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.radians
import com.littlekt.math.isFuzzyZero
import kotlin.math.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class BossFightState(val desc: BossDesc, var difficulty: Difficulty) : GameState {
    private val fightId = Game.random.nextLong()

    private lateinit var playerCamera: PlayerCamera
    private lateinit var guiCamera: GUICamera
    private lateinit var background: Background
    private var transitionIn: Transition? = null
    private lateinit var transitionOut: Transition
    private lateinit var boss: Boss
    private var resultPanel: ResultPanel? = null
    private var fightStartMessage: FightStartMessage? = FightStartMessage()
    private var ingameUI: IngameUI? = null
    private val borderIndicator = BorderIndicator()

    private var fightDuration = 0.0.seconds
    private var fightStarted = false
    private var showResults = false
    private val asteroids = AsteroidManager(difficulty, 700)
    private var returnState: GameState = this

    override fun begin() {
        val player = Game.player
        playerCamera = PlayerCamera(player.camera)
        guiCamera = GUICamera()
        background = Background(Game.resources.background)
        transitionIn = Transition(fromOpenToClose = false)
        transitionOut = Transition(fromOpenToClose = true)

        startFight(difficulty, false)

        transitionIn?.start {
            fightStartMessage?.startAnimation {
                fightStartMessage = null
                fightStarted = true
                Game.world.shouldUpdate = true
            }
            transitionIn = null
        }
    }

    private fun startFight(difficulty: Difficulty, isRetry: Boolean) {
        val player = Game.player

        this.difficulty = difficulty
        fightDuration = 0.0.seconds
        resultPanel = null
        fightStarted = false
        showResults = false

        Game.player.shootStatistics.reset()

        Game.world.clear()
        Game.physics.clear()
        Game.physics.width = 2500.0f
        Game.physics.height = 2500.0f

        if (!isRetry)
            asteroids.initialize()

        boss = desc.createBoss(difficulty)
        boss.addToWorld()
        boss.addToPhysics()

        val spawnPosition = when (desc.centerSpawnPosition) {
            SpawnPosition.TOP_LEFT -> Vec2f(Game.physics.minX + 200.0f, Game.physics.maxY - 200.0f)
            SpawnPosition.TOP_RIGHT -> Vec2f(Game.physics.maxX - 200.0f, Game.physics.maxY - 200.0f)
            SpawnPosition.BOTTOM_LEFT -> Vec2f(Game.physics.minX + 200.0f, Game.physics.minY + 200.0f)
            SpawnPosition.BOTTOM_RIGHT -> Vec2f(Game.physics.maxX - 200.0f, Game.physics.minY + 200.0f)
        }

        val spawnRotation = atan2(spawnPosition.y - boss.y, spawnPosition.x - boss.x).radians + 180.0.degrees
        player.ship.initialize(difficulty, spawnPosition.x, spawnPosition.y, spawnRotation, false)
        player.ship.addToWorld()
        player.ship.addToPhysics()

        if (isRetry) {
            fightStartMessage = FightStartMessage()
            fightStartMessage?.startAnimation {
                fightStartMessage = null
                fightStarted = true
            }
        }

        ingameUI = IngameUI(player.ship, difficulty)

        Game.world.shouldUpdate = true
    }

    override fun resize(width: Int, height: Int) {
        playerCamera.resize(width, height)
        guiCamera.resize(width, height)
    }

    override fun render(delta: Duration): () -> GameState {
        val player = Game.player
        val playerShip = player.ship

        Game.input.update(delta, Game.player.inputFrame)

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

        boss.update(delta, fightStarted)
        asteroids.update(delta, fightStarted)
        Game.particles.update(delta)
        Game.world.update(delta, fightStarted)
        playerCamera.update(cameraTargetX, cameraTargetY, delta)
        borderIndicator.color.set(player.indicatorColor)

        if (!showResults)
            fightDuration += delta

        if (!showResults && (boss.isDead || Game.player.ship.isDead)) {
            showResults = true
            Game.world.shouldUpdate = false

            if (!boss.isDead)
                boss.movementController.onFailFight()
            else
                Game.player.newlyUnlockedBossIndex = desc.unlockedBossIndex

            val results = FightResults(fightDuration, difficulty, desc.fullHealth, boss.health, Game.player.ship.health, Game.player.shootStatistics.shotsFired, Game.player.shootStatistics.shotsHit)
            val highscoreEntries = Game.player.highscores[desc.index]

            when (difficulty) {
                Difficulty.EASY -> {
                    if (highscoreEntries.easy == null || highscoreEntries.easy!!.percentage < results.percentage)
                        highscoreEntries.easy = HighscoreEntry(results.duration, results.percentage)
                }

                Difficulty.NORMAL -> {
                    if (highscoreEntries.normal == null || highscoreEntries.normal!!.percentage < results.percentage)
                        highscoreEntries.normal = HighscoreEntry(results.duration, results.percentage)
                }

                Difficulty.HARD -> {
                    if (highscoreEntries.hard == null || highscoreEntries.hard!!.percentage < results.percentage)
                        highscoreEntries.hard = HighscoreEntry(results.duration, results.percentage)
                }

                else -> {}
            }

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
            fightStartMessage?.render(delta, renderer)
            ingameUI?.render(delta, renderer)
            transitionIn?.render(delta, renderer)
            transitionOut.render(delta, renderer)
            borderIndicator.render(delta, renderer)

            if (showResults) {
                when (resultPanel?.renderAndGetResultState(delta, renderer)) {
                    ResultPanel.ResultState.RETURN -> transitionOut.start {
                        returnState = BayState()
                    }

                    ResultPanel.ResultState.RETRY_EASY -> startFight(Difficulty.EASY, true)
                    ResultPanel.ResultState.RETRY_NORMAL -> startFight(Difficulty.NORMAL, true)
                    ResultPanel.ResultState.RETRY_HARD -> startFight(Difficulty.HARD, true)
                    else -> {}
                }
            }
        }

        //pass.renderShapes(playerCamera.camera) { renderer: ShapeRenderer ->
        //    boss.drawDebug(renderer)
        //}

        pass.end()

        return { returnState }
    }

    override fun end() {
        boss.removeFromPhysics()
        boss.removeFromWorld()

        Game.player.ship.removeFromPhysics()
        Game.player.ship.removeFromWorld()
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