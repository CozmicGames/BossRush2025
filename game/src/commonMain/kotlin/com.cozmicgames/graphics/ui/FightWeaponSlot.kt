package com.cozmicgames.graphics.ui

import com.cozmicgames.Game
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.weapons.Weapon
import com.littlekt.graphics.Color
import com.littlekt.graphics.MutableColor
import kotlin.time.Duration

open class FightWeaponSlot(val weapon: Weapon, val type: Type) : GUIElement() {
    companion object {
        private val PRIMARY_COLOR = Color.fromHex("0065ff")
        private val SECONDARY_COLOR = Color.fromHex("ff5d00")

        private val OVERLAY_NORMAL_COLOR = Color(1.0f, 1.0f, 1.0f, 0.0f)
        private val OVERLAY_CANT_USE_COLOR = Color(1.0f, 0.0f, 0.0f, 0.7f)
    }

    enum class Type {
        PRIMARY,
        SECONDARY
    }

    private val cooldown = object : CooldownElement(Color.fromHex("94fdff"), true) {
        override var layer: Int
            get() = this@FightWeaponSlot.layer + 2
            set(value) {}
    }
    private val overlayColor = MutableColor()

    init {
        cooldown.getX = { x + width + 3.0f }
        cooldown.getY = { y + height * 0.1f }
        cooldown.getWidth = { 15.0f }
        cooldown.getHeight = { height * 0.8f }
    }

    fun update(value: Float) {
        cooldown.currentValue = value
    }

    override fun renderElement(delta: Duration, renderer: Renderer) {
        overlayColor.set(OVERLAY_CANT_USE_COLOR).mix(OVERLAY_NORMAL_COLOR, cooldown.currentValue, overlayColor)

        renderer.submit(layer) {
            val background = Game.textures.weaponBackgroundNinePatch
            background.draw(it, x, y, width, height)
            it.draw(weapon.previewTexture, x + width * 0.15f, y + width * 0.15f, width = width * 0.7f, height = height * 0.7f)

            val selectionBackground = Game.textures.weaponSelectedNinePatch
            val color = when (type) {
                Type.PRIMARY -> PRIMARY_COLOR
                Type.SECONDARY -> SECONDARY_COLOR
            }

            selectionBackground.draw(it, x, y, width, height, color = color)

            Game.textures.weaponMaskNinePatch.draw(it, x, y, width, height, color = overlayColor)

        }

        cooldown.render(delta, renderer)
    }
}