package com.cozmicgames.states

import com.cozmicgames.Game
import com.cozmicgames.graphics.Background
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.Transition
import com.cozmicgames.graphics.ui.GUICamera
import com.cozmicgames.graphics.ui.SplashScreenLogo
import kotlin.time.Duration

class SplashScreenState : GameState {
    private var returnState: GameState = this

    private lateinit var guiCamera: GUICamera
    private lateinit var background: Background
    private lateinit var transitionOut: Transition
    private lateinit var logo: SplashScreenLogo

    private var isFirstFrame = true

    override fun begin() {
        guiCamera = GUICamera()
        background = Background(Game.resources.background)
        transitionOut = Transition(fromOpenToClose = true)
        logo = SplashScreenLogo()

        Game.resources.themeSound.play(0.5f, true)
    }

    override fun resize(width: Int, height: Int) {
        guiCamera.resize(width, height)
    }

    override fun render(delta: Duration): () -> GameState {
        if (isFirstFrame) {
            //TODO: Player overview, start and tutorial button
            logo.startAnimation {
                transitionOut.start { returnState = BayState() }

                //returnState = TutorialState() //BayState()
            }
            isFirstFrame = false
        }

        val pass = Game.graphics.beginMainRenderPass()

        pass.render(guiCamera.camera) { renderer: Renderer ->
            background.render(delta, renderer)
            transitionOut.render(delta, renderer)
            logo.render(delta, renderer)
        }

        pass.end()

        return { returnState }
    }
}