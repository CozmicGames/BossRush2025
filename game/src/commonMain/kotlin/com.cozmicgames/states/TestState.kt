package com.cozmicgames.states

import com.cozmicgames.Game
import com.cozmicgames.input.InputFrame
import kotlin.time.Duration

class TestState : GameState {
    override fun begin() {
    }

    override fun render(delta: Duration): () -> GameState {
        val inputFrame = InputFrame()

        Game.input.update(delta, inputFrame)
        Game.players.getMyPlayerState().let {
            it.setState("inputX", inputFrame.deltaX)
            it.setState("inputY", inputFrame.deltaY)
            it.setState("inputRotation", inputFrame.rotation)
            it.setState("inputUsePrimary", inputFrame.usePrimary)
            it.setState("inputUseSecondary", inputFrame.useSecondary)
        }


        val pass = Game.graphics.beginMainRenderPass()

        pass.render(Game.graphics.mainViewport.camera) {
            Game.players.renderPlayers(it)
        }

        pass.end()

        return { this }
    }

    override fun end() {

    }
}