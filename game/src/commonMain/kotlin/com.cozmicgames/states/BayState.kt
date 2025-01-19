package com.cozmicgames.states

import com.cozmicgames.Constants
import com.cozmicgames.Game
import com.cozmicgames.bosses.TodoBossDesc
import com.cozmicgames.bosses.boss1.Boss1Desc
import com.cozmicgames.bosses.boss2.Boss2Desc
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.*
import com.littlekt.input.Key
import kotlin.time.Duration

class BayState : GameState {
    companion object {
        private val bossDescriptors = arrayOf(
            Boss1Desc(),
            Boss2Desc(),
            TodoBossDesc(),
            TodoBossDesc(),
            TodoBossDesc(),
            TodoBossDesc()
        )
    }

    private lateinit var guiCamera: GUICamera
    private var returnState: GameState = this

    private val messageBanner = MessageBanner()

    private val selectionPosters = List(6) {
        SelectionPoster(bossDescriptors[it], it == 0) { returnState = it }
    }

    private val shop = ShopUI()

    override fun begin() {
        guiCamera = GUICamera()

        messageBanner.getX = { Game.graphics.width * 0.5f - messageBanner.width * 0.5f }
        messageBanner.getY = { Game.graphics.height - 40.0f }
        messageBanner.getWidth = { Game.graphics.width - 40.0f }
        messageBanner.getHeight = { 40.0f }

        repeat(2) { yIndex ->
            val posterY = 20.0f + (1 - yIndex) * (Constants.BOSS_SELECTION_POSTER_HEIGHT + 25.0f)

            repeat(3) { xIndex ->
                val posterX = 20.0f + xIndex * (Constants.BOSS_SELECTION_POSTER_WIDTH + 25.0f)

                selectionPosters[yIndex * 3 + xIndex].apply {
                    x = posterX
                    y = posterY
                }
            }
        }

        shop.getX = { Game.graphics.width - shop.width }
        shop.getY = { 0.0f }
    }

    override fun resize(width: Int, height: Int) {
        guiCamera.resize(width, height)
    }

    override fun render(delta: Duration): () -> GameState {
        if (Game.input.isKeyJustPressed(Key.U)) //TODO: Remove
            selectionPosters[1].unlock()


        val pass = Game.graphics.beginMainRenderPass()

        pass.render(guiCamera.camera) { renderer: Renderer ->
            messageBanner.render(delta, renderer)

            selectionPosters.forEach {
                it.render(delta, renderer)
            }

            shop.render(delta, renderer)
        }

        pass.end()

        return { returnState }
    }
}