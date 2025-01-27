package com.cozmicgames.graphics.ui

import com.cozmicgames.Game
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.weapons.Weapon
import com.littlekt.graphics.Color
import com.littlekt.graphics.MutableColor
import com.littlekt.util.seconds
import kotlin.time.Duration

class FightWeaponSlot(val weapon: Weapon, val type: Type) : GUIElement() {
    companion object {
        private val PRIMARY_COLOR = Color.fromHex("0065ff")
        private val SECONDARY_COLOR = Color.fromHex("ff5d00")

        private val OVERLAY_NORMAL_COLOR = Color(1.0f, 1.0f, 1.0f, 0.0f)
        private val OVERLAY_USE_COLOR = Color(1.0f, 1.0f, 1.0f, 0.3f)
        private val OVERLAY_CANT_USE_COLOR = Color(1.0f, 0.0f, 0.0f, 0.7f)
    }

    enum class Type {
        PRIMARY,
        SECONDARY
    }

    private val cooldown = object : CooldownElement() {
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

    fun update(value: Float, tryUse: Boolean) {
        cooldown.currentValue = value

        if (tryUse) {
            if (cooldown.currentValue < 1.0f)
                overlayColor.set(OVERLAY_CANT_USE_COLOR)
            else
                overlayColor.set(OVERLAY_USE_COLOR)
        }
    }

    override fun renderElement(delta: Duration, renderer: Renderer) {
        overlayColor.mix(OVERLAY_NORMAL_COLOR, delta.seconds * 2.0f, overlayColor)

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

            Game.resources.weaponMaskNinePatch.draw(it, x, y, width, height, color = overlayColor)

        }

        cooldown.render(delta, renderer)
    }
}