package com.cozmicgames.graphics.ui

import com.cozmicgames.Game
import com.cozmicgames.graphics.Renderer
import com.littlekt.graphics.Color
import kotlin.time.Duration

open class CrewPlayerSlot(val index: Int) : GUIElement() {
    companion object {
        private val PRIMARY_COLOR = Color.fromHex("0065ff")
        private val SECONDARY_COLOR = Color.fromHex("ff5d00")
    }

    private inner class WeaponSlot(val isPrimary: Boolean) : GUIElement() {
        override var layer: Int
            get() = this@CrewPlayerSlot.layer + 1
            set(value) {}

        init {
            getX = { if (isPrimary) this@CrewPlayerSlot.x else (this@CrewPlayerSlot.x + width + 5.0f) }
            getY = { this@CrewPlayerSlot.y }
            getWidth = { (this@CrewPlayerSlot.width - 5.0f) * 0.5f }
            getHeight = { width }
        }

        override fun renderElement(delta: Duration, renderer: Renderer) {
            val player = Game.players.players.getOrNull(index)
            val weapon = if (player != null) if (isPrimary) player.primaryWeapon else player.secondaryWeapon else null

            renderer.submit(layer) {
                if (weapon == null)
                    it.draw(Game.resources.weaponSlotEmpty, x, y, width = width, height = height)
                else {
                    it.draw(Game.resources.weaponSlotBackground, x, y, width = width, height = height)
                    it.draw(Game.resources.weaponSlot, x, y, width = width, height = height, color = if (isPrimary) PRIMARY_COLOR else SECONDARY_COLOR)
                    it.draw(weapon.previewTexture, x, y, width = width, height = height)
                }
            }
        }
    }

    private val playerSlot = object : PlayerSlot(index) {
        override var layer: Int
            get() = this@CrewPlayerSlot.layer
            set(value) {}
    }
    private val primaryWeaponSlot = WeaponSlot(true)
    private val secondaryWeaponSlot = WeaponSlot(false)

    init {
        playerSlot.getX = { x }
        playerSlot.getY = { y + (width - 5.0f) * 0.5f + 5.0f }
        playerSlot.getWidth = { width }
        playerSlot.getHeight = { width }
    }

    override fun renderElement(delta: Duration, renderer: Renderer) {
        playerSlot.render(delta, renderer)
        primaryWeaponSlot.render(delta, renderer)
        secondaryWeaponSlot.render(delta, renderer)

        if (index == 0) {
            renderer.submit(layer + 4) {
                it.draw(Game.resources.captain, x + (width - width * 0.6f) * 0.5f, y + width * 1.4f, width = width * 0.6f, height = width * 0.3f)
            }
        }
    }
}