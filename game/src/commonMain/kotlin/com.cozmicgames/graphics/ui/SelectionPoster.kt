package com.cozmicgames.graphics.ui

import com.cozmicgames.Constants
import com.cozmicgames.Game
import com.cozmicgames.bosses.BossDesc
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.elements.Divider
import com.cozmicgames.graphics.ui.elements.Image
import com.cozmicgames.graphics.ui.elements.Label
import com.cozmicgames.utils.Difficulty
import com.littlekt.graphics.Color
import com.littlekt.graphics.MutableColor
import com.littlekt.resources.Textures
import kotlin.time.Duration

open class SelectionPoster(desc: BossDesc, var isUnlocked: Boolean, onSelect: (Int, Difficulty) -> Unit) : GUIElement() {
    private val nameLabel = object : Label(desc.name, 32.0f) {
        override var layer: Int
            get() = this@SelectionPoster.layer + 1
            set(value) {}
    }
    private val divider = object : Divider() {
        override var layer: Int
            get() = this@SelectionPoster.layer + 1
            set(value) {}
    }
    private val previewImageBorder = object : Image(Textures.white, Color.fromHex("8a4836")) {
        override var layer: Int
            get() = this@SelectionPoster.layer + 1
            set(value) {}
    }
    private val previewImage = object : Image(desc.preview) {
        override var layer: Int
            get() = this@SelectionPoster.layer + 1
            set(value) {}
    }
    private val rewardLabel = object : CurrencyLabel(desc.reward, 24.0f) {
        override var layer: Int
            get() = this@SelectionPoster.layer + 1
            set(value) {}
    }
    private val playEasyButton = object : PlayButton(Difficulty.EASY, { onSelect(desc.index, Difficulty.EASY) }) {
        override var layer: Int
            get() = this@SelectionPoster.layer + 1
            set(value) {}
    }
    private val playNormalButton = object : PlayButton(Difficulty.NORMAL, { onSelect(desc.index, Difficulty.NORMAL) }) {
        override var layer: Int
            get() = this@SelectionPoster.layer + 1
            set(value) {}
    }
    private val playHardButton = object : PlayButton(Difficulty.HARD, { onSelect(desc.index, Difficulty.HARD) }) {
        override var layer: Int
            get() = this@SelectionPoster.layer + 1
            set(value) {}
    }

    private var lock = if (!isUnlocked) object : Lock() {
        override var layer: Int
            get() = this@SelectionPoster.layer + 2
            set(value) {}
    } else null
    private var isUnlocking = false

    init {
        width = Constants.BOSS_SELECTION_POSTER_WIDTH
        height = Constants.BOSS_SELECTION_POSTER_HEIGHT

        playEasyButton.isEnabled = (isUnlocked && Game.player.highscores[desc.index].easy == null) || Game.player.isFreePlay
        playNormalButton.isEnabled = (isUnlocked && Game.player.highscores[desc.index].normal == null) || Game.player.isFreePlay
        playHardButton.isEnabled = (isUnlocked && Game.player.highscores[desc.index].hard == null) || Game.player.isFreePlay

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

        previewImageBorder.getX = { previewImage.x + (previewImage.width - previewImageBorder.width) * 0.5f }
        previewImageBorder.getY = { previewImage.y + (previewImage.height - previewImageBorder.height) * 0.5f }
        previewImageBorder.getWidth = { 85.0f }
        previewImageBorder.getHeight = { 85.0f }

        previewImage.getX = { x + (width - previewImage.width) * 0.5f }
        previewImage.getY = { y + height * 0.4f }
        previewImage.getWidth = { 80.0f }
        previewImage.getHeight = { 80.0f }

        rewardLabel.getX = { x + width * 0.6f }
        rewardLabel.getY = { y + height * 0.27f }
        rewardLabel.getWidth = { width * 0.9f }
        rewardLabel.getHeight = { height * 0.1f }

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

        lock?.let {
            it.getX = { x + (width - it.width) * 0.5f }
            it.getY = { y + (height - it.height) * 0.5f }
            it.getWidth = { 128.0f }
            it.getHeight = { 128.0f }
        }
    }

    fun unlock(callback: () -> Unit) {
        if (isUnlocked || isUnlocking)
            return

        lock?.let {
            it.startUnlockAnimation {
                isUnlocked = true
                playEasyButton.isEnabled = true
                playNormalButton.isEnabled = true
                playHardButton.isEnabled = true
                lock = null
                callback()
            }
        }
    }

    override fun renderElement(delta: Duration, renderer: Renderer) {
        renderer.submit(layer) {
            Game.textures.fightSelectionPosterNinePatch.draw(it, x, y, width, height)
        }

        nameLabel.render(delta, renderer)
        divider.render(delta, renderer)
        previewImageBorder.render(delta, renderer)
        previewImage.render(delta, renderer)
        rewardLabel.render(delta, renderer)

        playEasyButton.render(delta, renderer)
        playNormalButton.render(delta, renderer)
        playHardButton.render(delta, renderer)

        if (!isUnlocked) {
            lock?.render(delta, renderer)

            renderer.submit(layer) {
                Game.textures.fightSelectionPosterMaskNinePatch.draw(it, x, y, width, height, color = (lock?.color ?: MutableColor(Color.WHITE)).mix(Color(1.0f, 1.0f, 1.0f, 0.5f), 0.5f))
            }
        }
    }
}