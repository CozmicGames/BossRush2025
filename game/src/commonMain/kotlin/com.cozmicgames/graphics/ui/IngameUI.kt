package com.cozmicgames.graphics.ui

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.PlayerShip
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.utils.Difficulty
import kotlin.time.Duration

class IngameUI(private val ship: PlayerShip, difficulty: Difficulty) : GUIElement() {
    private var primaryWeaponSlot: FightWeaponSlot? = null
    private var secondaryWeaponSlot: FightWeaponSlot? = null
    private val healthbar = object : Healthbar(difficulty) {
        override var layer: Int
            get() = this@IngameUI.layer + 1
            set(value) {}
    }

    init {
        ship.primaryWeapon?.let {
            primaryWeaponSlot = object : FightWeaponSlot(it, Type.PRIMARY) {
                override var layer: Int
                    get() = this@IngameUI.layer + 1
                    set(value) {}
            }
        }

        ship.secondaryWeapon?.let {
            secondaryWeaponSlot = object : FightWeaponSlot(it, Type.SECONDARY) {
                override var layer: Int
                    get() = this@IngameUI.layer + 1
                    set(value) {}
            }
        }

        primaryWeaponSlot?.let {
            if (secondaryWeaponSlot != null)
                it.getX = { Game.graphics.width - 80.0f - 25.0f - 80.0f - 25.0f }
            else
                it.getX = { Game.graphics.width - 80.0f - 25.0f }

            it.getY = { 10.0f }
            it.getWidth = { 80.0f }
            it.getHeight = { 80.0f }
        }

        secondaryWeaponSlot?.let {
            it.getX = { Game.graphics.width - 80f - 25.0f }
            it.getY = { 10.0f }
            it.getWidth = { 80.0f }
            it.getHeight = { 80.0f }
        }

        healthbar.getX = { Game.graphics.width - 210.0f }
        healthbar.getY = { 100.0f }
        healthbar.getWidth = { 200.0f }
        healthbar.getHeight = { 25.0f }
    }

    override fun renderElement(delta: Duration, renderer: Renderer) {
        primaryWeaponSlot?.update(ship.primaryCooldownFactor)
        secondaryWeaponSlot?.update(ship.secondaryCooldownFactor)
        healthbar.update(ship.health)

        primaryWeaponSlot?.render(delta, renderer)
        secondaryWeaponSlot?.render(delta, renderer)
        healthbar.render(delta, renderer)
    }
}