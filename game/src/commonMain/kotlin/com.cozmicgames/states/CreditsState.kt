package com.cozmicgames.states

import com.cozmicgames.Game
import com.cozmicgames.graphics.Background
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.Transition
import com.cozmicgames.graphics.ui.*
import com.cozmicgames.graphics.ui.elements.Label
import com.cozmicgames.graphics.ui.elements.TextButton
import com.littlekt.graphics.Color
import kotlin.time.Duration

class CreditsState : GameState {
    private lateinit var guiCamera: GUICamera
    private var returnState: GameState = this
    private val background = Background(Game.textures.background)
    private var transitionIn: Transition? = Transition(fromOpenToClose = false)
    private val transitionOut = Transition(fromOpenToClose = true)

    private val upperMessageLabel = Label("The monsters are gone!", 60.0f)
    private val lowerMessageLabel = Label("But who knows what else lurks out there?", 40.0f)
    private val thanksLabel = Label("Thank you for playing!", 32.0f)
    private val creditsLabel = Label("Made by cozmicgames for Boss Rush Jam 2025", 26.0f)
    private val freePlayButton = TextButton("Free Play", Color.fromHex("33984b"), fontSize = 28.0f) {
        transitionOut.start {
            returnState = BayState(true)
        }
    }

    override fun begin() {
        guiCamera = GUICamera()

        upperMessageLabel.getX = { Game.graphics.width * 0.5f }
        upperMessageLabel.getY = { 460.0f }
        upperMessageLabel.shadowOffsetX = 3.0f
        upperMessageLabel.shadowOffsetY = -3.0f

        lowerMessageLabel.getX = { Game.graphics.width * 0.5f }
        lowerMessageLabel.getY = { 400.0f }
        lowerMessageLabel.shadowOffsetX = 3.0f
        lowerMessageLabel.shadowOffsetY = -3.0f

        thanksLabel.getX = { Game.graphics.width * 0.5f }
        thanksLabel.getY = { 190.0f }
        thanksLabel.shadowOffsetX = 2.0f
        thanksLabel.shadowOffsetY = -2.0f

        creditsLabel.getX = { Game.graphics.width * 0.5f }
        creditsLabel.getY = { 150.0f }
        creditsLabel.shadowOffsetX = 2.0f
        creditsLabel.shadowOffsetY = -2.0f

        freePlayButton.getX = { Game.graphics.width * 0.5f - freePlayButton.width * 0.5f }
        freePlayButton.getY = { 50.0f }
        freePlayButton.getWidth = { 300.0f }
        freePlayButton.getHeight = { 50.0f }

        transitionIn?.start {
            transitionIn = null
        }
    }

    override fun resize(width: Int, height: Int) {
        guiCamera.resize(width, height)
    }

    override fun render(delta: Duration): () -> GameState {
        val pass = Game.graphics.beginMainRenderPass()

        pass.render(guiCamera.camera) { renderer: Renderer ->
            background.render(delta, renderer)
            transitionIn?.render(delta, renderer)
            transitionOut.render(delta, renderer)

            upperMessageLabel.render(delta, renderer)
            lowerMessageLabel.render(delta, renderer)
            thanksLabel.render(delta, renderer)
            creditsLabel.render(delta, renderer)
            freePlayButton.render(delta, renderer)
        }

        pass.end()

        return { returnState }
    }
}