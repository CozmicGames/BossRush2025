package com.cozmicgames.states

import com.cozmicgames.Constants
import com.cozmicgames.Game
import com.cozmicgames.graphics.Background
import com.cozmicgames.graphics.RenderLayers
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.Transition
import com.cozmicgames.graphics.ui.*
import com.littlekt.graphics.MutableColor
import com.littlekt.util.seconds
import kotlin.math.sin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class BayState(isFreePlay: Boolean = false) : GameState {
    private lateinit var guiCamera: GUICamera
    private var returnState: GameState = this
    private var timer = 0.0.seconds

    private val messageBanner = MessageBanner()
    private val fightSelectionUI = FightSelectionUI { index, difficulty ->
        transitionOut.start {
            returnState = if (index == Constants.FINAL_FIGHT_INDEX)
                FinalFightState(difficulty)
            else
                BossFightState(Constants.BOSS_DESCRIPTORS[index], difficulty)
        }
    }

    private val shop = object : ShopUI() {
        override var layer: Int
            get() = RenderLayers.UI + 100
            set(value) {}
    }

    private val highscores = object : HighscoreUI() {
        override var layer: Int
            get() = RenderLayers.UI + 200
            set(value) {}
    }

    private val background = Background(Game.textures.background)
    private var transitionIn: Transition? = Transition(fromOpenToClose = false)
    private val transitionOut = Transition(fromOpenToClose = true)
    private val borderIndicator = BorderIndicator()
    private var canInteract = false

    private var unlockedFinalFight = false
    private val finalFightIndicatorColor = MutableColor(0.8f, 0.1f, 0.2f, 0.0f)

    override fun begin() {
        guiCamera = GUICamera()

        messageBanner.getX = { Game.graphics.width * 0.5f - messageBanner.width * 0.5f }
        messageBanner.getY = { Game.graphics.height - 45.0f }
        messageBanner.getWidth = { Game.graphics.width.toFloat() }
        messageBanner.getHeight = { 40.0f }

        shop.getX = { Game.graphics.width - shop.width }
        shop.getY = { 0.0f }

        Game.audio.baySound.play(0.4f)
        Game.audio.themeSound.play(0.5f, true)

        transitionIn?.start {
            canInteract = true
            transitionIn = null
        }
    }

    override fun resize(width: Int, height: Int) {
        guiCamera.resize(width, height)
    }

    override fun render(delta: Duration): () -> GameState {
        if (timer > 2.0.seconds) {
            if (Game.player.newlyUnlockedBossIndex >= 0) {
                if (Game.player.newlyUnlockedBossIndex == Constants.FINAL_FIGHT_INDEX) {
                    unlockedFinalFight = true
                    fightSelectionUI.transitionToFinalFight()
                } else
                    fightSelectionUI.unlock(Game.player.newlyUnlockedBossIndex) {
                        Game.player.unlockedBossIndices += Game.player.newlyUnlockedBossIndex
                    }
                Game.player.newlyUnlockedBossIndex = -1
            }
        }

        if (unlockedFinalFight) {
            finalFightIndicatorColor.a = sin(timer.seconds * 5.0f) * 0.5f + 0.5f

            borderIndicator.color.set(finalFightIndicatorColor)
        }

        val pass = Game.graphics.beginMainRenderPass()

        pass.render(guiCamera.camera) { renderer: Renderer ->
            background.render(delta, renderer)
            transitionIn?.render(delta, renderer)
            transitionOut.render(delta, renderer)

            messageBanner.render(delta, renderer)
            fightSelectionUI.render(delta, renderer)

            shop.render(delta, renderer)
            highscores.render(delta, renderer)

            borderIndicator.render(delta, renderer)
        }

        pass.end()

        timer += delta

        return { returnState }
    }
}