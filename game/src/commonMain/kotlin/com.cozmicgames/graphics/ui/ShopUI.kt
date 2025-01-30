package com.cozmicgames.graphics.ui

import com.cozmicgames.Game
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.elements.Label
import com.cozmicgames.graphics.ui.elements.NinepatchImage
import com.cozmicgames.weapons.Weapons
import kotlin.time.Duration

open class ShopUI : GUIElement() {
    private val shopBackground = object : NinepatchImage(Game.resources.shopBackgroundNinePatch) {
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
    private val walletLabel = object : CurrencyLabel({ Game.players.wallet }, 24.0f) {
        override var layer: Int
            get() = this@ShopUI.layer + 2
            set(value) {}
    }
    private val walletBackground = object : NinepatchImage(Game.resources.walletBackgroundNinePatch) {
        override var layer: Int
            get() = this@ShopUI.layer + 1
            set(value) {}
    }

    init {
        weaponSlots = Weapons.entries.mapIndexed { index, weapon ->
            lateinit var slot: ShopWeaponSlot
            slot = ShopWeaponSlot(weapon, index in Game.players.unlockedWeaponIndices) { selectionState ->
                val player = Game.players.getMyPlayer() ?: throw IllegalStateException("Player not found")

                weaponSlots.forEach {
                    if (it != slot && it.selectionState == selectionState)
                        it.selectionState = ShopWeaponSlot.SelectionState.UNSELECTED
                }

                when (selectionState) {
                    ShopWeaponSlot.SelectionState.PRIMARY -> {
                        player.primaryWeapon = weapon

                        if (player.secondaryWeapon == weapon)
                            player.secondaryWeapon = null
                    }

                    ShopWeaponSlot.SelectionState.SECONDARY -> {
                        player.secondaryWeapon = weapon

                        if (player.primaryWeapon == weapon)
                            player.primaryWeapon = null
                    }

                    else -> {}
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
        if (Game.players.newlyUnlockedWeaponIndex >= 0) {
            weaponSlots.getOrNull(Game.players.newlyUnlockedWeaponIndex)?.unlock()
            Game.players.newlyUnlockedWeaponIndex = -1
        }

        shopBackground.render(delta, renderer)
        shopLabel.render(delta, renderer)
        weaponSlots.forEach {
            it.render(delta, renderer)
        }
        walletBackground.render(delta, renderer)
        walletLabel.render(delta, renderer)
    }
}