package com.cozmicgames.states

import com.cozmicgames.Game
import com.cozmicgames.graphics.Background
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.Transition
import com.cozmicgames.graphics.ui.GUICamera
import com.cozmicgames.graphics.ui.GameLogo
import com.cozmicgames.graphics.ui.elements.TextButton
import com.littlekt.graphics.Color
import kotlin.time.Duration

class MenuState() : GameState {
    private var returnState: GameState = this

    private lateinit var guiCamera: GUICamera
    private lateinit var background: Background
    private lateinit var transitionOut: Transition
    private lateinit var logo: GameLogo

    private lateinit var startButton: TextButton
    private lateinit var tutorialButton: TextButton

    private var isFirstFrame = true
    private var showMenu = false

    override fun begin() {
        guiCamera = GUICamera()
        background = Background(Game.textures.background)
        transitionOut = Transition(fromOpenToClose = true)
        logo = GameLogo()

        val buttonSpacing = 50.0f
        val buttonWidth = 300.0f
        val buttonHeight = 50.0f

        startButton = TextButton("Start", Color.fromHex("33984b"), fontSize = 36.0f) {
            transitionOut.start { returnState = BayState() }
        }

        startButton.getX = { (Game.graphics.width - (buttonWidth * 2 + buttonSpacing)) * 0.5f }
        startButton.getY = { 60.0f }
        startButton.getWidth = { buttonWidth }
        startButton.getHeight = { buttonHeight }

        tutorialButton = TextButton("Tutorial", Color.fromHex("e07438"), fontSize = 36.0f) {
            transitionOut.start { returnState = TutorialState() }
        }

        tutorialButton.getX = { (Game.graphics.width - (buttonWidth * 2 + buttonSpacing)) * 0.5f + buttonWidth + buttonSpacing }
        tutorialButton.getY = { 60.0f }
        tutorialButton.getWidth = { buttonWidth }
        tutorialButton.getHeight = { buttonHeight }
    }

    override fun resize(width: Int, height: Int) {
        guiCamera.resize(width, height)
    }

    override fun render(delta: Duration): () -> GameState {
        if (isFirstFrame) {
            logo.startAnimation {
                showMenu = true
            }
            isFirstFrame = false
        }

        val pass = Game.graphics.beginMainRenderPass()

        pass.render(guiCamera.camera) { renderer: Renderer ->
            background.render(delta, renderer)
            transitionOut.render(delta, renderer)
            logo.render(delta, renderer)

            if (showMenu) {
                startButton.render(delta, renderer)

                tutorialButton.render(delta, renderer)
            }
        }

        pass.end()

        return { returnState }
    }
}