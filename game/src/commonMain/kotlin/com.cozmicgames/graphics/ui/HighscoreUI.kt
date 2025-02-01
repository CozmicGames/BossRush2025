package com.cozmicgames.graphics.ui

import com.cozmicgames.Constants
import com.cozmicgames.Game
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.elements.Image
import com.cozmicgames.graphics.ui.elements.Label
import com.cozmicgames.utils.Difficulty
import com.littlekt.graphics.Color
import com.littlekt.graphics.HAlign
import com.littlekt.graphics.MutableColor
import com.littlekt.graphics.slice
import com.littlekt.resources.Textures
import kotlin.math.round
import kotlin.time.Duration

open class HighscoreUI : GUIElement() {
    private class Place(val bossIndex: Int, val difficulty: Difficulty, val duration: Duration, val percentage: Float)

    private open inner class HighscoreEntry(val place: Int) : GUIElement() {
        val difficultyIconColor = MutableColor()

        private val previewImageBorder = object : Image(Textures.white, Color.fromHex("8a4836")) {
            override var layer: Int
                get() = this@HighscoreEntry.layer + 1
                set(value) {}
        }
        private val previewImage = object : Image(Textures.white) {
            override var layer: Int
                get() = this@HighscoreEntry.layer + 1
                set(value) {}
        }
        val difficultyIcon = object : Image(Game.textures.playIcon, difficultyIconColor) {
            override var layer: Int
                get() = this@HighscoreEntry.layer + 1
                set(value) {}
        }
        val durationLabel = object : HighscoreDurationLabel(24.0f) {
            override var layer: Int
                get() = this@HighscoreEntry.layer + 1
                set(value) {}
        }
        val percentageLabel = object : Label("", 24.0f) {
            override var layer: Int
                get() = this@HighscoreEntry.layer + 1
                set(value) {}
        }

        init {
            getWidth = { 40.0f + 5.0f + 35.0f + 5.0f + 150.0f }
            getHeight = { 45.0f }

            previewImageBorder.getX = { x + 2.5f }
            previewImageBorder.getY = { y + 2.5f }
            previewImageBorder.getWidth = { 40.0f }
            previewImageBorder.getHeight = { 40.0f }

            previewImage.getX = { previewImageBorder.x + (previewImageBorder.width - previewImage.width) * 0.5f }
            previewImage.getY = { previewImageBorder.y + (previewImageBorder.height - previewImage.height) * 0.5f }
            previewImage.getWidth = { 36.0f }
            previewImage.getHeight = { 36.0f }

            difficultyIcon.getX = { x + 40.0f + 5.0f }
            difficultyIcon.getY = { y + 5.0f }
            difficultyIcon.getWidth = { 35.0f }
            difficultyIcon.getHeight = { 35.0f }

            durationLabel.getX = { x + 40.0f + 5.0f + 35.0f + 5.0f + 60.0f }
            durationLabel.getY = { y + 10.0f }
            durationLabel.getWidth = { 0.0f }
            durationLabel.getHeight = { 25.0f }

            percentageLabel.getX = { x + width - 80.0f }
            percentageLabel.getY = { y + 10.0f }
            percentageLabel.getWidth = { 60.0f }
            percentageLabel.getHeight = { 25.0f }
        }

        override fun renderElement(delta: Duration, renderer: Renderer) {
            renderer.submit(layer) {
                Game.textures.highscoreBackgroundNinePatch.draw(it, x, y, width, height)
            }

            val place = places[place]

            if (place != null) {
                previewImage.texture = Constants.BOSS_DESCRIPTORS[place.bossIndex].preview.slice()

                difficultyIconColor.set(
                    when (place.difficulty) {
                        Difficulty.EASY -> Color.fromHex("33984b")
                        Difficulty.NORMAL -> Color.fromHex("ffdd25")
                        Difficulty.HARD -> Color.fromHex("d31321")
                        else -> throw IllegalArgumentException("Tutorial difficulty is not supported for PlayButton")
                    }
                )

                durationLabel.duration = place.duration
                percentageLabel.text = "${round(place.percentage * 100).toInt()}%"

                previewImageBorder.render(delta, renderer)
                previewImage.render(delta, renderer)
                difficultyIcon.render(delta, renderer)
                durationLabel.render(delta, renderer)
                percentageLabel.render(delta, renderer)
            }
        }
    }

    private val highscoreLabel = object : Label("Highscores", 48.0f) {
        override var layer: Int
            get() = this@HighscoreUI.layer + 1
            set(value) {}
    }

    private val highscoreEntries = Array(3) {
        object : HighscoreEntry(it) {
            override var layer: Int
                get() = this@HighscoreUI.layer + 1
                set(value) {}
        }
    }

    private val places = arrayOfNulls<Place>(3)

    init {
        highscoreLabel.getX = { Game.graphics.width - 380.0f + 360.0f * 0.5f }
        highscoreLabel.getY = { 185.0f }
        highscoreLabel.shadowOffsetX = 3.0f
        highscoreLabel.shadowOffsetY = -3.0f
        highscoreLabel.hAlign = HAlign.CENTER

        repeat(3) {
            highscoreEntries[it].getX = { Game.graphics.width - 380.0f + (360.0f - 300.0f) * 0.5f }
            highscoreEntries[it].getY = { (45.0f * 2 + 8.0f * 2) - (45.0f + 8.0f) * it + 5.0f }
            highscoreEntries[it].getWidth = { 300.0f }
            highscoreEntries[it].getHeight = { 45.0f }
        }
    }

    override fun renderElement(delta: Duration, renderer: Renderer) {
        val places = arrayListOf<Place>()

        Game.player.highscores.forEachIndexed { index, entries ->
            if (entries.easy != null)
                places.add(Place(index, Difficulty.EASY, entries.easy!!.duration, entries.easy!!.percentage))

            if (entries.normal != null)
                places.add(Place(index, Difficulty.NORMAL, entries.normal!!.duration, entries.normal!!.percentage))

            if (entries.hard != null)
                places.add(Place(index, Difficulty.HARD, entries.hard!!.duration, entries.hard!!.percentage))
        }

        places.sortBy { it.percentage }

        this.places[0] = places.getOrNull(0)
        this.places[1] = places.getOrNull(1)
        this.places[2] = places.getOrNull(2)

        renderer.submit(layer) {
            Game.textures.crewBackgroundNinePatch.draw(it, Game.graphics.width - 380.0f, 0.0f, 360.0f, 220.0f)
        }

        highscoreEntries.forEach {
            it.render(delta, renderer)
        }

        highscoreLabel.render(delta, renderer)
    }
}