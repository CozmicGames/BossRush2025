package com.cozmicgames.states

import com.cozmicgames.Game
import com.cozmicgames.TutorialStage
import com.cozmicgames.bosses.tutorialBoss.TutorialBoss
import com.cozmicgames.entities.worldObjects.AsteroidManager
import com.cozmicgames.graphics.*
import com.cozmicgames.graphics.ui.*
import com.cozmicgames.utils.Difficulty
import com.cozmicgames.weapons.Weapons
import com.littlekt.math.geom.abs
import com.littlekt.math.geom.degrees
import com.littlekt.math.isFuzzyZero
import kotlin.math.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class TutorialState() : GameState {
    private inner class MoveMessage : TutorialMessage("Can’t catch fish standing still.\nWASD or arrows to get moving!") {
        private var previousX = 0.0f
        private var previousY = 0.0f
        private var isFirst = true

        override fun shouldFadeOut(): Boolean {
            val player = Game.player
            val playerShip = player.ship

            val currentX = playerShip.x
            val currentY = playerShip.y
            val deltaX = abs(currentX - previousX)
            val deltaY = abs(currentY - previousY)
            previousX = currentX
            previousY = currentY

            if (isFirst) {
                isFirst = false
                return false
            }

            return deltaX > 0.1f || deltaY > 0.1f
        }

        override fun onFadeOut() {
            Game.player.tutorialStage = TutorialStage.LOOKING.ordinal
            nextMessage = LookMessage()
            nextMessageTimer = 1.0.seconds
        }
    }

    private inner class LookMessage : TutorialMessage("See it before it sees you!\nUse your mouse to look around!") {
        private var previousRotation = 0.0.degrees
        private var isFirst = true

        override fun shouldFadeOut(): Boolean {
            val player = Game.player
            val playerShip = player.ship

            val currentRotation = playerShip.rotation
            val deltaRotation = abs(currentRotation - previousRotation)
            previousRotation = currentRotation

            if (isFirst) {
                isFirst = false
                return false
            }

            return deltaRotation > 0.1.degrees
        }

        override fun onFadeOut() {
            Game.player.tutorialStage = TutorialStage.SHOOTING_PRIMARY.ordinal
            nextMessage = ShootPrimaryMessage()
            nextMessageTimer = 1.0.seconds
        }
    }

    private inner class ShootPrimaryMessage : TutorialMessage("Blasters up!\nLeft-click and don’t hold back!") {
        override fun shouldFadeOut(): Boolean {
            val player = Game.player
            val playerShip = player.ship

            return playerShip.tryUsePrimaryWeapon
        }

        override fun onFadeOut() {
            Game.player.tutorialStage = TutorialStage.SHOOTING_SECONDARY.ordinal
            nextMessage = ShootSecondaryMessage()
            nextMessageTimer = 1.0.seconds
        }
    }

    private inner class ShootSecondaryMessage : TutorialMessage("Swap it up!\nRight-click for your secondary!") {
        override fun shouldFadeOut(): Boolean {
            val player = Game.player
            val playerShip = player.ship

            return playerShip.tryUseSecondaryWeapon
        }

        override fun onFadeOut() {
            Game.player.tutorialStage = TutorialStage.PARALYZE.ordinal
            nextMessage = ParalyzeBossMessage()
            nextMessageTimer = 1.0.seconds
            shouldUpdate = true
        }
    }

    private inner class ParalyzeBossMessage : TutorialMessage("Now where's the haul?\nIt's too flappy for a catch, let's hit it!") {
        override fun shouldFadeOut(): Boolean {
            return boss.isParalyzed
        }

        override fun onFadeOut() {
            Game.player.tutorialStage = TutorialStage.HIT.ordinal
            nextMessage = HitBossMessage()
            nextMessageTimer = 1.0.seconds
        }
    }

    private inner class HitBossMessage : TutorialMessage("Shocked stiff!\nNow let’s finish the job!") {
        var previousHealth = boss.health

        override fun shouldFadeOut(): Boolean {
            val currentHealth = boss.health
            val healthDelta = previousHealth - currentHealth
            previousHealth = currentHealth

            return healthDelta > 0
        }

        override fun onFadeOut() {
            Game.player.tutorialStage = TutorialStage.END.ordinal
            nextMessage = EndMessage()
            nextMessageTimer = 1.0.seconds
        }
    }

    private inner class EndMessage : TutorialMessage("Enough of the easy prey!\nLet’s see what you’re really made of!") {
        private var isFirst = true
        private var startTime = 0.0.seconds

        override fun shouldFadeOut(): Boolean {
            if (isFirst) {
                startTime = Game.upTime
                isFirst = false
            }

            return Game.upTime - startTime > 3.0.seconds
        }

        override fun onFadeOut() {
            nextMessage = null
            currentMessage = null

            transitionOut.start {
                returnState = MenuState()
            }
        }
    }


    private lateinit var playerCamera: PlayerCamera
    private lateinit var guiCamera: GUICamera
    private lateinit var background: Background
    private var transitionIn: Transition? = null
    private lateinit var transitionOut: Transition
    private lateinit var boss: TutorialBoss
    private var ingameUI: IngameUI? = null

    private var shouldUpdate = false
    private var isFighting = false

    private val asteroids = AsteroidManager(300)
    private var returnState: GameState = this

    private var nextMessage: TutorialMessage? = null
    private var nextMessageTimer = 0.0.seconds
    private var currentMessage: TutorialMessage? = null

    override fun begin() {
        val player = Game.player
        Game.player.isTutorialMode = true

        playerCamera = PlayerCamera(player.camera)
        guiCamera = GUICamera()
        background = Background(Game.textures.background)
        transitionIn = Transition(false)
        transitionOut = Transition(true)

        Game.world.clear()
        Game.physics.clear()
        Game.physics.width = 2500.0f
        Game.physics.height = 800.0f

        asteroids.initialize(Difficulty.EASY)

        boss = TutorialBoss()
        boss.x = 800.0f
        boss.addToWorld()
        boss.addToPhysics()

        player.ship.initialize(Difficulty.EASY, -800.0f, 0.0f, 0.0.degrees, false)
        player.ship.primaryWeapon = Weapons.REELGUN
        player.ship.secondaryWeapon = Weapons.HYPER_HARPOON
        player.ship.addToWorld()
        player.ship.addToPhysics()

        ingameUI = IngameUI(player.ship, Difficulty.EASY)

        transitionIn?.start {
            nextMessage = MoveMessage()
            transitionIn = null
            isFighting = true
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

        boss.update(delta, isFighting)
        asteroids.update(delta, isFighting)
        Game.particles.update(delta)
        Game.world.update(delta, isFighting)
        playerCamera.update(cameraTargetX, cameraTargetY, delta)

        nextMessageTimer -= delta
        if (nextMessage != null && nextMessageTimer <= 0.0.seconds) {
            currentMessage = nextMessage
            currentMessage?.fadeIn()
            nextMessage = null
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
            currentMessage?.render(delta, renderer)
            ingameUI?.render(delta, renderer)
            transitionIn?.render(delta, renderer)
            transitionOut.render(delta, renderer)
        }

        //pass.renderShapes(playerCamera.camera) { renderer: ShapeRenderer ->
        //    boss.drawDebug(renderer)
        //}

        pass.render(Game.graphics.mainViewport.camera) { renderer: Renderer ->
            if (player.indicatorColor.a > 0.0f)
                renderer.submit(RenderLayers.BORDER_INDICATOR) {
                    it.draw(Game.textures.borderIndicator, -Game.graphics.width.toFloat() * 0.5f, -Game.graphics.height.toFloat() * 0.5f, width = Game.graphics.width.toFloat(), height = Game.graphics.height.toFloat(), color = player.indicatorColor)
                }
        }

        pass.end()

        return { returnState }
    }

    override fun end() {
        Game.player.isTutorialMode = false

        boss.removeFromPhysics()
        boss.removeFromWorld()

        Game.player.ship.removeFromPhysics()
        Game.player.ship.removeFromWorld()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is TutorialState
    }

    override fun hashCode(): Int {
        return this::class.hashCode()
    }
}