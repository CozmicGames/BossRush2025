package com.cozmicgames.graphics.ui

import com.cozmicgames.Game
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.elements.Label
import com.cozmicgames.graphics.ui.elements.NinepatchImage
import com.cozmicgames.weapons.Weapons
import kotlin.time.Duration

open class ShopUI : GUIElement() {
    private val shopBackground = object : NinepatchImage(Game.textures.shopBackgroundNinePatch) {
        override var layer: Int
            get() = this@ShopUI.layer
            set(value) {}
    }
    private val shopLabel = object : Label("Bay Shop", 48.0f) {
        override var layer: Int
            get() = this@ShopUI.layer + 1
            set(value) {}
    }
    private lateinit var weaponSlots: List<ShopWeaponSlot>
    private val walletLabel = object : CurrencyLabel({ Game.player.wallet }, 24.0f) {
        override var layer: Int
            get() = this@ShopUI.layer + 2
            set(value) {}
    }
    private val walletBackground = object : NinepatchImage(Game.textures.walletBackgroundNinePatch) {
        override var layer: Int
            get() = this@ShopUI.layer + 1
            set(value) {}
    }

    init {
        weaponSlots = Weapons.entries.mapIndexed { index, weapon ->
            lateinit var slot: ShopWeaponSlot
            slot = ShopWeaponSlot(weapon, index in Game.player.unlockedWeaponIndices) {
                weaponSlots.forEach {
                    if (it !== slot && it.selectionState == slot.selectionState)
                        it.selectionState = ShopWeaponSlot.SelectionState.UNSELECTED
                }
            }
            slot.layer = layer + 1
            slot
        }

        val spacing = 10.0f

        weaponSlots.forEachIndexed { index, slot ->
            slot.getX = { spacing + index * (130.0f + spacing) }
            slot.getY = { y + 10 }
            slot.getWidth = { 130.0f }
            slot.getHeight = { 130.0f }
        }

        walletBackground.getX = { 360.0f }
        walletBackground.getY = { 150.0f }
        walletBackground.getWidth = { 200.0f }
        walletBackground.getHeight = { 38.0f }

        walletLabel.getX = { walletBackground.x + 130.0f }
        walletLabel.getY = { walletBackground.y + 6.0f }

        shopLabel.getX = { spacing }
        shopLabel.getY = { 170.0f }
        shopLabel.getWidth = { 250.0f }
        shopLabel.shadowOffsetX = 3.0f
        shopLabel.shadowOffsetY = -3.0f

        shopBackground.getX = { 0.0f }
        shopBackground.getY = { 0.0f }
        shopBackground.getWidth = { Game.graphics.width.toFloat() }
        shopBackground.getHeight = { 210.0f }
    }

    override fun renderElement(delta: Duration, renderer: Renderer) {
        shopBackground.render(delta, renderer)
        shopLabel.render(delta, renderer)
        weaponSlots.forEach {
            it.render(delta, renderer)
        }
        walletBackground.render(delta, renderer)
        walletLabel.render(delta, renderer)
    }
}