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
import com.cozmicgames.graphics.particles.effects.ExplosionEffect
import com.cozmicgames.graphics.ui.*
import com.cozmicgames.utils.Difficulty
import com.littlekt.input.Key
import com.littlekt.math.geom.degrees
import com.littlekt.math.isFuzzyZero
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.time.Duration

class FinalFightState(var difficulty: Difficulty) : GameState {
    private val fightId = Game.random.nextLong()

    private lateinit var playerCamera: PlayerCamera
    private lateinit var guiCamera: GUICamera
    private lateinit var background: Background
    private var transitionIn: Transition? = null
    private lateinit var transitionOut: Transition
    private lateinit var bosses: Array<Boss>

    private var fightStartMessage: FinalFightStartMessage? = FinalFightStartMessage()

    private var ingameUI: IngameUI? = null
    private var ingameMenu: IngameMenu? = null
    private var creditsUI: CreditsUI? = null
    private var finalFightFailedUI: FinalFightFailedUI? = null
    private val borderIndicator = BorderIndicator()

    private var isFighting = false
    private var isPaused = false
    private var fightEnded = false
    private var checkMenuResults = false
    private var checkFinalFightFailedResults = false
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
                isFighting = true
            }
            transitionIn = null
        }
    }

    private fun startFight(difficulty: Difficulty, isRetry: Boolean) {
        val player = Game.player

        this.difficulty = difficulty
        isFighting = false
        fightEnded = false

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
                isFighting = true
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

        if (!isPaused && !fightEnded) {
            bosses.forEach {
                it.update(delta, isFighting)
            }
            asteroids.update(delta, isFighting)
            Game.particles.update(delta)
            Game.world.update(delta, isFighting)
        }

        playerCamera.update(cameraTargetX, cameraTargetY, delta)
        borderIndicator.color.set(player.indicatorColor)

        if (isFighting && !fightEnded && (Game.input.isKeyJustPressed(Key.ESCAPE) || Game.input.isKeyJustPressed(Key.BACKSPACE))) {
            if (ingameMenu == null) {
                Game.audio.stopLoopingSounds()

                isPaused = true
                checkMenuResults = true
                ingameMenu = IngameMenu()
                ingameMenu?.slideIn()
                ingameUI?.slideOut()
            } else {
                ingameMenu?.slideOut {
                    isPaused = false
                    checkMenuResults = false
                    ingameMenu = null
                    ingameUI?.slideIn()
                }
            }
        }

        if (checkMenuResults)
            when (ingameMenu?.resultState) {
                IngameMenu.ResultState.CONTINUE -> {
                    checkMenuResults = false

                    ingameMenu?.slideOut {
                        ingameMenu = null
                        isPaused = false
                        ingameUI?.slideIn()
                    }
                }

                IngameMenu.ResultState.RETURN -> {
                    checkMenuResults = false

                    transitionOut.start {
                        returnState = BayState()
                    }
                }

                IngameMenu.ResultState.RETRY_EASY -> {
                    checkMenuResults = false

                    ingameMenu?.slideOut {
                        ingameMenu = null
                        isPaused = false
                        startFight(Difficulty.EASY, true)
                    }
                }

                IngameMenu.ResultState.RETRY_NORMAL -> {
                    checkMenuResults = false

                    ingameMenu?.slideOut {
                        ingameMenu = null
                        isPaused = false
                        startFight(Difficulty.NORMAL, true)
                    }
                }

                IngameMenu.ResultState.RETRY_HARD -> {
                    checkMenuResults = false

                    ingameMenu?.slideOut {
                        ingameMenu = null
                        isPaused = false
                        startFight(Difficulty.HARD, true)
                    }
                }

                else -> {}
            }

        if (!fightEnded && (bosses.all { it.isDead } || Game.player.ship.isDead)) {
            fightEnded = true
            ingameUI?.slideOut()

            bosses.forEach {
                it.movementController.onEndFight()
            }

            if (bosses.all { it.isDead }) {
                Game.player.unlockFreePlay()

                creditsUI = CreditsUI {
                    transitionOut.start {
                        returnState = BayState()
                    }
                }
            } else {
                finalFightFailedUI = FinalFightFailedUI()
                finalFightFailedUI?.slideIn()
                checkFinalFightFailedResults = true

                Game.particles.add(ExplosionEffect(Game.player.ship.x, Game.player.ship.y))
                Game.world.remove(Game.player.ship)
            }
        }

        if (checkFinalFightFailedResults)
            when (finalFightFailedUI?.resultState) {
                FinalFightFailedUI.ResultState.RETURN -> {
                    checkFinalFightFailedResults = false

                    transitionOut.start {
                        returnState = BayState()
                    }
                }

                FinalFightFailedUI.ResultState.RETRY_EASY -> {
                    checkFinalFightFailedResults = false

                    finalFightFailedUI?.slideOut {
                        finalFightFailedUI = null
                        startFight(Difficulty.EASY, true)
                    }
                }

                FinalFightFailedUI.ResultState.RETRY_NORMAL -> {
                    checkFinalFightFailedResults = false

                    finalFightFailedUI?.slideOut {
                        finalFightFailedUI = null
                        startFight(Difficulty.NORMAL, true)
                    }
                }

                FinalFightFailedUI.ResultState.RETRY_HARD -> {
                    checkFinalFightFailedResults = false

                    finalFightFailedUI?.slideOut {
                        finalFightFailedUI = null
                        startFight(Difficulty.HARD, true)
                    }
                }

                else -> {}
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
            creditsUI?.render(delta, renderer)
            finalFightFailedUI?.render(delta, renderer)
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