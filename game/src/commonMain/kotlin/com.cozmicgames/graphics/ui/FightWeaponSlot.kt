package com.cozmicgames.graphics.ui

import com.cozmicgames.Game
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.elements.Label
import com.cozmicgames.weapons.Weapon
import com.littlekt.graphics.Color
import kotlin.time.Duration

class FightWeaponSlot(val weapon: Weapon, val type: Type) : GUIElement() {
    companion object {
        private val PRIMARY_COLOR = Color.fromHex("0065ff")
        private val SECONDARY_COLOR = Color.fromHex("ff5d00")
    }

    enum class Type {
        PRIMARY,
        SECONDARY
    }

    private val nameLabel = Label(weapon.displayName, 18.0f)

    init {
        nameLabel.getX = { x + (width - nameLabel.width) * 0.5f }
        nameLabel.getY = { y + height * 0.25f }
        nameLabel.getWidth = { width }
        nameLabel.getHeight = { height }
        nameLabel.shadowOffsetX = 1.0f
        nameLabel.shadowOffsetY = -1.0f
    }

    override fun renderElement(delta: Duration, renderer: Renderer) {
        renderer.submit(layer) {
            val background = Game.resources.weaponBackgroundNinePatch
            background.draw(it, x, y, width, height)
            it.draw(weapon.previewTexture, x + width * 0.15f, y + width * 0.15f, width = width * 0.7f, height = height * 0.7f)

            val selectionBackground = Game.resources.weaponSelectedNinePatch
            val color = when (type) {
                Type.PRIMARY -> PRIMARY_COLOR
                Type.SECONDARY -> SECONDARY_COLOR
            }

            selectionBackground.draw(it, x, y, width, height, color = color)
        }

        nameLabel.render(delta, renderer)
    }
}