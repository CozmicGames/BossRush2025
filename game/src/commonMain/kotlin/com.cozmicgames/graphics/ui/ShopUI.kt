package com.cozmicgames.graphics.ui

import com.cozmicgames.Game
import com.cozmicgames.graphics.RenderLayers
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.ui.elements.Label
import com.cozmicgames.graphics.ui.elements.NinepatchImage
import com.cozmicgames.weapons.Weapons
import kotlin.time.Duration

class ShopUI : GUIElement() {
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
    private lateinit var weaponSlots: List<WeaponSlot>
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
        getWidth = { 290.0f }
        getHeight = { 550.0f }

        weaponSlots = Weapons.entries.mapIndexed { index, weapon ->
            lateinit var slot: WeaponSlot
            slot = WeaponSlot(weapon, index in Game.players.unlockedWeaponIndices) { selectionState ->
                val player = Game.players.getMyPlayer() ?: throw IllegalStateException("Player not found")

                weaponSlots.forEach {
                    if (it != slot && it.selectionState == selectionState)
                        it.selectionState = WeaponSlot.SelectionState.UNSELECTED
                }

                when (selectionState) {
                    WeaponSlot.SelectionState.PRIMARY -> {
                        player.primaryWeapon = weapon

                        if (player.secondaryWeapon == weapon)
                            player.secondaryWeapon = null
                    }

                    WeaponSlot.SelectionState.SECONDARY -> {
                        player.secondaryWeapon = weapon

                        if (player.primaryWeapon == weapon)
                            player.primaryWeapon = null
                    }

                    else -> {}
                }
            }
            slot.layer = RenderLayers.UI_BEGIN + 1
            slot
        }

        repeat(3) {
            val weaponSlot = weaponSlots[it]
            weaponSlot.getX = { x + 10.0f }
            weaponSlot.getY = { y + 290.0f - (it * (130.0f + 10.0f)) }
            weaponSlot.getWidth = { 130.0f }
            weaponSlot.getHeight = { 130.0f }
        }

        repeat(3) {
            val weaponSlot = weaponSlots[it + 3]
            weaponSlot.getX = { x + 10.0f + 130.0f + 10.0f }
            weaponSlot.getY = { y + 290.0f - (it * (130.0f + 10.0f)) }
            weaponSlot.getWidth = { 130.0f }
            weaponSlot.getHeight = { 130.0f }
        }

        walletLabel.getX = { x + 180.0f }
        walletLabel.getY = { y + 443.0f }

        walletBackground.getX = { x + 50.0f }
        walletBackground.getY = { y + 435.0f }
        walletBackground.getWidth = { width - 100.0f }
        walletBackground.getHeight = { 38.0f }

        shopLabel.getX = { x + 145.0f }
        shopLabel.getY = { y + 508.0f }
        shopLabel.shadowOffsetX = 3.0f
        shopLabel.shadowOffsetY = -3.0f

        shopBackground.getX = { x }
        shopBackground.getY = { y }
        shopBackground.getWidth = { 290.0f }
        shopBackground.getHeight = { 550.0f }
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