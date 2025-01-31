package com.cozmicgames.states

import com.cozmicgames.Constants
import com.cozmicgames.Game
import com.cozmicgames.bosses.Boss
import com.cozmicgames.bosses.boss1.Boss1
import com.cozmicgames.bosses.boss2.Boss2
import com.cozmicgames.bosses.boss3.Boss3
import com.cozmicgames.bosses.boss4.Boss4
import com.cozmicgames.entities.worldObjects.AsteroidManager
import com.cozmicgames.graphics.*
import com.cozmicgames.graphics.ui.*
import com.cozmicgames.utils.Difficulty
import com.cozmicgames.utils.FightResults
import com.littlekt.math.geom.degrees
import com.littlekt.math.isFuzzyZero
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class FinalFightState(var difficulty: Difficulty) : GameState {
    private val fightId = Game.random.nextLong()

    private lateinit var playerCamera: PlayerCamera
    private lateinit var guiCamera: GUICamera
    private lateinit var background: Background
    private var transitionIn: Transition? = null
    private lateinit var transitionOut: Transition
    private lateinit var bosses: Array<Boss>

    private var resultPanel: ResultPanel? = null
    private var fightStartMessage: FinalFightStartMessage? = FinalFightStartMessage()

    private var ingameUI: IngameUI? = null
    private val borderIndicator = BorderIndicator()

    private var fightDuration = 0.0.seconds
    private var fightStarted = false
    private var showResults = false
    private val asteroids = AsteroidManager(difficulty, 1500)
    private var returnState: GameState = this

    override fun begin() {
        val player = Game.player

        player.currentFightIndex = Constants.FINAL_FIGHT_INDEX

        playerCamera = PlayerCamera(player.camera)
        guiCamera = GUICamera()
        background = Background(Game.textures.background)
        transitionIn = Transition(fromOpenToClose = false)
        transitionOut = Transition(fromOpenToClose = true)

        startFight(difficulty, false)

        transitionIn?.start {
            ingameUI?.slideIn()
            fightStartMessage?.startAnimation {
                fightStartMessage = null
                fightStarted = true
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
        Game.physics.width = 4000.0f
        Game.physics.height = 4000.0f

        if (!isRetry)
            asteroids.initialize()

        bosses = arrayOf(Boss1(difficulty), Boss2(difficulty), Boss3(difficulty), Boss4(difficulty))

        bosses.forEach {
            it.addToWorld()
            it.addToPhysics()
        }

        bosses[0].x = Game.physics.minX + 200.0f
        bosses[0].y = Game.physics.maxY - 200.0f

        bosses[1].x = Game.physics.minX + 200.0f
        bosses[1].y = Game.physics.minY + 200.0f

        bosses[2].x = Game.physics.maxX - 200.0f
        bosses[2].y = Game.physics.minY + 200.0f

        bosses[3].x = Game.physics.maxX - 200.0f
        bosses[3].y = Game.physics.maxY - 200.0f

        player.ship.initialize(difficulty, 0.0f, 0.0f, 0.0.degrees, true)
        player.ship.addToWorld()
        player.ship.addToPhysics()

        ingameUI = IngameUI(player.ship, difficulty)

        if (isRetry) {
            ingameUI?.slideIn()
            fightStartMessage = FinalFightStartMessage()
            fightStartMessage?.startAnimation {
                fightStartMessage = null
                fightStarted = true
            }
        }
    }

    override fun resize(width: Int, height: Int) {
        playerCamera.resize(width, height)
        guiCamera.resize(width, height)
    }

    override fun render(delta: Duration): () -> GameState {
        val player = Game.player
        val playerShip = player.ship

        Game.input.update(delta, player.inputFrame)

        var cameraTargetX = playerShip.x
        var cameraTargetY = playerShip.y

        val nearestBoss = bosses.minBy { boss ->
            val playerShipToBossX = boss.x - playerShip.x
            val playerShipToBossY = boss.y - playerShip.y
            playerShipToBossX * playerShipToBossX + playerShipToBossY * playerShipToBossY
        }

        var playerShipToBossX = nearestBoss.x - playerShip.x
        var playerShipToBossY = nearestBoss.y - playerShip.y
        val playerShipToBossDistance = sqrt(playerShipToBossX * playerShipToBossX + playerShipToBossY * playerShipToBossY)

        if (!playerShipToBossDistance.isFuzzyZero()) {
            playerShipToBossX /= playerShipToBossDistance
            playerShipToBossY /= playerShipToBossDistance

            val cameraTargetDistance = min(playerShipToBossDistance, min(playerCamera.camera.virtualWidth, playerCamera.camera.virtualHeight) * 0.4f)
            cameraTargetX += playerShipToBossX * cameraTargetDistance
            cameraTargetY += playerShipToBossY * cameraTargetDistance
        }

        bosses.forEach {
            it.update(delta, fightStarted)
        }
        asteroids.update(delta, fightStarted)
        Game.particles.update(delta)
        Game.world.update(delta, fightStarted)
        playerCamera.update(cameraTargetX, cameraTargetY, delta)
        borderIndicator.color.set(player.indicatorColor)

        if (!showResults)
            fightDuration += delta

        if (!showResults && (bosses.all { it.isDead } || Game.player.ship.isDead)) {
            showResults = true
            ingameUI?.slideOut()

            bosses.forEach {
                it.movementController.onEndFight()
            }

            val results = FightResults(fightDuration, difficulty, bosses.sumOf { it.fullHealth }, bosses.sumOf { it.health }, Game.player.ship.health, Game.player.shootStatistics.shotsFired, Game.player.shootStatistics.shotsHit)

            resultPanel = ResultPanel(results)
        }

        val pass = Game.graphics.beginMainRenderPass()

        pass.render(playerCamera.camera) { renderer: Renderer ->
            background.render(delta, renderer)

            Game.world.render(renderer)

            bosses.forEach {
                it.renderSpecials(delta, renderer)
            }

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
                        returnState = CreditsState()
                    }

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
        bosses.forEach {
            it.removeFromPhysics()
            it.removeFromWorld()
        }

        Game.player.ship.removeFromPhysics()
        Game.player.ship.removeFromWorld()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FinalFightState) return false

        if (difficulty != other.difficulty) return false

        return fightId == other.fightId
    }

    override fun hashCode(): Int {
        return fightId.hashCode()
    }
}