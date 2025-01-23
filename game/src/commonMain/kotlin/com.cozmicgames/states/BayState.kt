package com.cozmicgames.states

import com.cozmicgames.Constants
import com.cozmicgames.Game
import com.cozmicgames.bosses.TodoBossDesc
import com.cozmicgames.bosses.boss1.Boss1Desc
import com.cozmicgames.bosses.boss2.Boss2Desc
import com.cozmicgames.bosses.boss3.Boss3Desc
import com.cozmicgames.graphics.Background
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.*
import com.cozmicgames.graphics.ui.elements.Label
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class BayState : GameState {
    companion object {
        private val bossDescriptors = arrayOf(
            Boss1Desc(),
            Boss2Desc(),
            Boss3Desc(),
            TodoBossDesc()
        )
    }

    private lateinit var guiCamera: GUICamera
    private var returnState: GameState = this
    private var timer = 0.0.seconds

    private val messageBanner = MessageBanner()

    private val selectionLabel = Label("Select your trip!", 54.0f)

    private val selectionPosters = List(4) {
        SelectionPoster(bossDescriptors[it], it in Game.players.unlockedBossIndices) { returnState = it }
    }

    private val shop = ShopUI()

    private val background = Background(Game.resources.background)

    override fun begin() {
        guiCamera = GUICamera()

        messageBanner.getX = { Game.graphics.width * 0.5f - messageBanner.width * 0.5f }
        messageBanner.getY = { Game.graphics.height - 45.0f }
        messageBanner.getWidth = { Game.graphics.width.toFloat() }
        messageBanner.getHeight = { 40.0f }

        selectionLabel.getX = { Game.graphics.width * 0.5f - selectionLabel.width * 0.5f }
        selectionLabel.getY = { 510.0f }
        selectionLabel.getWidth = { Game.graphics.width.toFloat() }
        selectionLabel.getHeight = { 54.0f }
        selectionLabel.shadowOffsetX = 3.0f
        selectionLabel.shadowOffsetY = -3.0f

        val selectionPosterSpacing = (Game.graphics.width - selectionPosters.size * Constants.BOSS_SELECTION_POSTER_WIDTH) / (selectionPosters.size + 1)

        selectionPosters.forEachIndexed { index, poster ->
            poster.getX = { selectionPosterSpacing + index * (Constants.BOSS_SELECTION_POSTER_WIDTH + selectionPosterSpacing) }
            poster.getY = { 230.0f }
        }

        shop.getX = { Game.graphics.width - shop.width }
        shop.getY = { 0.0f }
    }

    override fun resize(width: Int, height: Int) {
        guiCamera.resize(width, height)
    }

    override fun render(delta: Duration): () -> GameState {
        if (timer > 1.0.seconds) {
            if (Game.players.newlyUnlockedBossIndex >= 0) {
                selectionPosters[Game.players.newlyUnlockedBossIndex].unlock()
                Game.players.unlockedBossIndices += Game.players.newlyUnlockedBossIndex
                Game.players.newlyUnlockedBossIndex = -1
            }
        }

        val pass = Game.graphics.beginMainRenderPass()

        pass.render(guiCamera.camera) { renderer: Renderer ->
            background.render(delta, renderer)

            messageBanner.render(delta, renderer)

            selectionLabel.render(delta, renderer)

            selectionPosters.forEach {
                it.render(delta, renderer)
            }

            shop.render(delta, renderer)
        }

        pass.end()

        timer += delta

        return { returnState }
    }
}