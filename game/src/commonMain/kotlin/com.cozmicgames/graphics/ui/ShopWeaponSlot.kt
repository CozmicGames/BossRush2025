package com.cozmicgames.graphics.ui

import com.cozmicgames.Game
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.elements.Label
import com.cozmicgames.graphics.ui.elements.Tooltip
import com.cozmicgames.weapons.Weapon
import com.cozmicgames.weapons.Weapons
import com.littlekt.graphics.Color
import com.littlekt.graphics.MutableColor
import com.littlekt.input.Pointer
import kotlin.time.Duration

class ShopWeaponSlot(val weapon: Weapon, var isUnlocked: Boolean, private val onResetSelection: () -> Unit) : GUIElement() {
    companion object {
        private val PRIMARY_COLOR = Color.fromHex("0065ff")
        private val SECONDARY_COLOR = Color.fromHex("ff5d00")
    }

    enum class SelectionState {
        UNSELECTED,
        PRIMARY,
        SECONDARY
    }

    var selectionState = when {
        Game.player.primaryWeapon == weapon -> SelectionState.PRIMARY
        Game.player.secondaryWeapon == weapon -> SelectionState.SECONDARY
        else -> SelectionState.UNSELECTED
    }

    private val nameLabel = object : Label(weapon.displayName, 18.0f) {
        override var layer: Int
            get() = this@ShopWeaponSlot.layer + 2
            set(value) {}
    }
    private var priceLabel = if (!isUnlocked) object : CurrencyLabel(weapon.price, 14.0f) {
        override var layer: Int
            get() = this@ShopWeaponSlot.layer + 3
            set(value) {}
    } else null
    private var lock = if (!isUnlocked) object : Lock() {
        override var layer: Int
            get() = this@ShopWeaponSlot.layer + 2
            set(value) {}
    } else null
    private var isUnlocking = false
    private val overlayColor = MutableColor()
    private var wasHovered = false

    private val tooltip = Tooltip(weapon.tooltipText)

    init {
        nameLabel.getX = { x + (width - nameLabel.width) * 0.5f }
        nameLabel.getY = { y + height * 0.3f }
        nameLabel.getWidth = { width }
        nameLabel.getHeight = { height }
        nameLabel.shadowOffsetX = 1.0f
        nameLabel.shadowOffsetY = -1.0f

        priceLabel?.let {
            it.getX = { x + width * 0.6f }
            it.getY = { y + height * 0.15f }
            it.getWidth = { width }
            it.getHeight = { height }
        }

        lock?.let {
            it.getX = { x + (width - it.width) * 0.5f }
            it.getY = { y + (height - it.height) * 0.5f }
            it.getWidth = { width * 0.45f }
            it.getHeight = { height * 0.45f }
        }
    }

    fun unlock() {
        Game.audio.unlockSound.play()
        Game.player.unlockedWeaponIndices += Weapons.entries.indexOf(weapon)
        isUnlocking = true
        lock?.startUnlockAnimation {
            isUnlocked = true
            priceLabel = null
            lock = null
            isUnlocking = false
        }
    }

    override fun renderElement(delta: Duration, renderer: Renderer) {
        val minX = x
        val minY = y
        val maxX = x + width
        val maxY = y + height

        val isHovered = Game.input.x.toFloat() in minX..maxX && (Game.graphics.height - Game.input.y - 1).toFloat() in minY..maxY

        if (!wasHovered && isHovered)
            Game.audio.hoverSound.play(0.1f)

        wasHovered = isHovered

        if (isUnlocked) {
            val isClickedPrimary = Game.input.isJustTouched(Pointer.MOUSE_LEFT) && isHovered
            val isClickedSecondary = (Game.input.isJustTouched(Pointer.MOUSE_MIDDLE) || Game.input.isJustTouched(Pointer.MOUSE_RIGHT)) && isHovered

            if (isClickedPrimary) {
                if (selectionState == SelectionState.SECONDARY)
                    Game.player.secondaryWeapon = null

                Game.audio.selectPrimarySound.play()
                selectionState = SelectionState.PRIMARY
                Game.player.primaryWeapon = weapon
                onResetSelection()
            } else if (isClickedSecondary) {
                if (selectionState == SelectionState.PRIMARY)
                    Game.player.primaryWeapon = null

                Game.audio.selectSecondarySound.play()
                selectionState = SelectionState.SECONDARY
                Game.player.secondaryWeapon = weapon
                onResetSelection()
            }

            renderer.submit(layer) {
                val background = if (isHovered) Game.textures.weaponBackgroundHoveredNinePatch else Game.textures.weaponBackgroundNinePatch
                background.draw(it, x, y, width, height)
                it.draw(weapon.previewTexture, x + width * 0.15f, y + width * 0.15f, width = width * 0.7f, height = height * 0.7f)

                if (selectionState != SelectionState.UNSELECTED) {
                    val selectionBackground = Game.textures.weaponSelectedNinePatch
                    val color = if (selectionState == SelectionState.PRIMARY) PRIMARY_COLOR else SECONDARY_COLOR

                    selectionBackground.draw(it, x, y, width, height, color = color)
                }
            }
        } else {
            renderer.submit(layer) {
                val background = if (isHovered) Game.textures.weaponBackgroundHoveredNinePatch else Game.textures.weaponBackgroundNinePatch
                background.draw(it, x, y, width, height)
                it.draw(weapon.previewTexture, x + width * 0.15f, y + width * 0.15f, width = width * 0.7f, height = height * 0.7f)

                overlayColor.mix(Color.WHITE, 0.2f, overlayColor)

                Game.textures.weaponMaskNinePatch.draw(it, x, y, width, height, color = MutableColor(lock?.color ?: Color.WHITE).mul(overlayColor).mix(Color(0.3f, 0.3f, 0.3f, 0.5f), 0.7f))
            }

            val isClicked = Game.input.isJustTouched(Pointer.MOUSE_LEFT) && isHovered

            if (isClicked && !isUnlocking) {
                if (Game.player.wallet >= weapon.price) {
                    Game.player.spendCredits(weapon.price)
                    unlock()
                } else
                    overlayColor.set(Color.RED)
            }
        }

        lock?.render(delta, renderer)
        nameLabel.render(delta, renderer)
        priceLabel?.render(delta, renderer)

        if (isHovered)
            tooltip.render(delta, renderer)
    }
}