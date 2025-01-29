package com.cozmicgames.graphics.ui

import com.cozmicgames.Constants
import com.cozmicgames.Game
import com.cozmicgames.bosses.BossDesc
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.elements.Divider
import com.cozmicgames.graphics.ui.elements.Image
import com.cozmicgames.graphics.ui.elements.Label
import com.cozmicgames.states.FinalFightState
import com.cozmicgames.states.GameState
import com.cozmicgames.utils.Difficulty
import com.littlekt.graphics.Color
import com.littlekt.resources.Textures
import kotlin.time.Duration

open class FinalSelectionPoster(descs: Array<BossDesc>, var isUnlocked: Boolean, onSelect: (GameState) -> Unit) : GUIElement() {
    private open class PreviewImage(val desc: BossDesc) : GUIElement() {
        private val previewImageBorder = object : Image(Textures.white, Color.fromHex("8a4836")) {
            override var layer: Int
                get() = this@PreviewImage.layer + 1
                set(value) {}
        }
        private val previewImage = object : Image(desc.preview) {
            override var layer: Int
                get() = this@PreviewImage.layer + 1
                set(value) {}
        }

        init {
            getWidth = { 85.0f }
            getHeight = { 85.0f }

            previewImageBorder.getX = { previewImage.x + (previewImage.width - previewImageBorder.width) * 0.5f }
            previewImageBorder.getY = { previewImage.y + (previewImage.height - previewImageBorder.height) * 0.5f }
            previewImageBorder.getWidth = { 85.0f }
            previewImageBorder.getHeight = { 85.0f }

            previewImage.getX = { x + (width - previewImage.width) * 0.5f }
            previewImage.getY = { y }
            previewImage.getWidth = { 80.0f }
            previewImage.getHeight = { 80.0f }
        }

        override fun renderElement(delta: Duration, renderer: Renderer) {
            previewImageBorder.render(delta, renderer)
            previewImage.render(delta, renderer)
        }
    }

    private val nameLabel = object : Label("Final Fight", 32.0f) {
        override var layer: Int
            get() = this@FinalSelectionPoster.layer + 1
            set(value) {}
    }
    private val divider = object : Divider() {
        override var layer: Int
            get() = this@FinalSelectionPoster.layer + 1
            set(value) {}
    }
    private val previews = descs.map {
        object : PreviewImage(it) {
            override var layer: Int
                get() = this@FinalSelectionPoster.layer + 1
                set(value) {}
        }
    }
    private val playEasyButton = object : PlayButton(Difficulty.EASY, { onSelect(FinalFightState(Difficulty.EASY)) }) {
        override var layer: Int
            get() = this@FinalSelectionPoster.layer + 1
            set(value) {}
    }
    private val playNormalButton = object : PlayButton(Difficulty.NORMAL, { onSelect(FinalFightState(Difficulty.NORMAL)) }) {
        override var layer: Int
            get() = this@FinalSelectionPoster.layer + 1
            set(value) {}
    }
    private val playHardButton = object : PlayButton(Difficulty.HARD, { onSelect(FinalFightState(Difficulty.HARD)) }) {
        override var layer: Int
            get() = this@FinalSelectionPoster.layer + 1
            set(value) {}
    }

    init {
        val previewSpacing = 20.0f

        width = (Constants.BOSS_SELECTION_POSTER_WIDTH - 85.0f) + 85.0f * previews.size + previewSpacing * (previews.size - 1)
        height = Constants.BOSS_SELECTION_POSTER_HEIGHT

        playEasyButton.isEnabled = isUnlocked
        playNormalButton.isEnabled = isUnlocked
        playHardButton.isEnabled = isUnlocked

        nameLabel.getX = { x + (width - nameLabel.width) * 0.5f }
        nameLabel.getY = { y + height * 0.8f }
        nameLabel.getWidth = { width * 0.9f }
        nameLabel.getHeight = { height * 0.1f }
        nameLabel.shadowOffsetX = 2.0f
        nameLabel.shadowOffsetY = -2.0f

        divider.getX = { x + (width - divider.width) * 0.5f }
        divider.getY = { y + height * 0.79f }
        divider.getWidth = { width * 0.8f }
        divider.getHeight = { 2.0f }

        previews.forEachIndexed { index, preview ->
            preview.getX = { x + (width - (preview.width * previews.size + previewSpacing * (previews.size - 1))) * 0.5f + index * (preview.width + previewSpacing) }
            preview.getY = { y + height * 0.4f }
        }

        playEasyButton.getX = { x + (width - (56.0f * 3 + 56.0f * 0.15f * 2)) * 0.5f }
        playEasyButton.getY = { y + 6.0f }
        playEasyButton.getWidth = { 56.0f }
        playEasyButton.getHeight = { 56.0f }

        playNormalButton.getX = { x + (width - (56.0f * 3 + 56.0f * 0.15f * 2)) * 0.5f + 56.0f * 1.15f }
        playNormalButton.getY = { y + 6.0f }
        playNormalButton.getWidth = { 56.0f }
        playNormalButton.getHeight = { 56.0f }

        playHardButton.getX = { x + (width - (56.0f * 3 + 56.0f * 0.15f * 2)) * 0.5f + 56.0f * 1.15f * 2 }
        playHardButton.getY = { y + 6.0f }
        playHardButton.getWidth = { 56.0f }
        playHardButton.getHeight = { 56.0f }

        playEasyButton.isEnabled = true
        playNormalButton.isEnabled = true
        playHardButton.isEnabled = true
    }

    override fun renderElement(delta: Duration, renderer: Renderer) {
        renderer.submit(layer) {
            Game.resources.fightSelectionPosterNinePatch.draw(it, x, y, width, height)
        }

        nameLabel.render(delta, renderer)
        divider.render(delta, renderer)
        previews.forEach { it.render(delta, renderer) }
        playEasyButton.render(delta, renderer)
        playNormalButton.render(delta, renderer)
        playHardButton.render(delta, renderer)
    }
}