package com.cozmicgames.graphics.ui

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.PlayerShip
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.utils.Difficulty
import com.cozmicgames.utils.lerp
import com.littlekt.graphics.Color
import com.littlekt.math.clamp
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class IngameUI(private val ship: PlayerShip, difficulty: Difficulty) : GUIElement() {
    private companion object {
        private val SLIDE_TIME = 0.5.seconds
        private val INVULNERABILITY_COLOR = Color.fromHex("c42430")
    }

    private var primaryWeaponSlot: FightWeaponSlot? = null
    private var secondaryWeaponSlot: FightWeaponSlot? = null
    private val healthbar = object : Healthbar(difficulty) {
        override var layer: Int
            get() = this@IngameUI.layer + 1
            set(value) {}
    }

    private val invulnerableIndicator = CooldownElement(INVULNERABILITY_COLOR, false)

    private var slideInAmount = 0.0f
    private var startSlideInAmount = 0.0f
    private var targetSlideInAmount = 0.0f
    private var timer = 0.0.seconds

    init {
        var numWeaponSlots = 0
        ship.primaryWeapon?.let { numWeaponSlots++ }
        ship.secondaryWeapon?.let { numWeaponSlots++ }

        getX = { (Game.graphics.width - width) * 0.5f }
        getY = { -100.0f + slideInAmount * 100.0f }
        getWidth = { numWeaponSlots * 80.0f + (numWeaponSlots - 1) * 25.0f + 10.0f + difficulty.basePlayerHealth * 15.0f + 25.0f + 35.0f }
        getHeight = { 100.0f }

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
                it.getX = { x + width - 80.0f - 25.0f - 80.0f - 35.0f }
            else
                it.getX = { x + width - 80.0f - 35.0f }

            it.getY = { y + 10.0f }
            it.getWidth = { 80.0f }
            it.getHeight = { 80.0f }
        }

        secondaryWeaponSlot?.let {
            it.getX = { x + width - 80f - 35.0f }
            it.getY = { y + 10.0f }
            it.getWidth = { 80.0f }
            it.getHeight = { 80.0f }
        }

        healthbar.getX = { x + 25.0f }
        healthbar.getY = { y + 20.0f }
        healthbar.getWidth = { difficulty.basePlayerHealth * 15.0f }
        healthbar.getHeight = { 40.0f }

        invulnerableIndicator.getX = { healthbar.x }
        invulnerableIndicator.getY = { healthbar.y + healthbar.height + 5.0f }
        invulnerableIndicator.getWidth = { healthbar.width }
        invulnerableIndicator.getHeight = { 10.0f }
    }

    fun slideIn() {
        startSlideInAmount = slideInAmount
        targetSlideInAmount = 1.0f
        timer = SLIDE_TIME * (1.0 - slideInAmount)
    }

    fun slideOut() {
        startSlideInAmount = slideInAmount
        targetSlideInAmount = 0.0f
        timer = SLIDE_TIME * slideInAmount.toDouble()
    }

    override fun renderElement(delta: Duration, renderer: Renderer) {
        if (slideInAmount != targetSlideInAmount) {
            slideInAmount = lerp(startSlideInAmount, targetSlideInAmount, 1.0f - (timer / SLIDE_TIME).toFloat()).clamp(0.0f, 1.0f)

            timer -= delta
        }

        primaryWeaponSlot?.update(ship.primaryCooldownFactor)
        secondaryWeaponSlot?.update(ship.secondaryCooldownFactor)
        healthbar.update(ship.health)
        invulnerableIndicator.currentValue = 1.0f - ship.invulnerabilityFactor

        renderer.submit(layer) {
            Game.textures.fightBackgroundNinePatch.draw(it, x, y, width, height)
        }

        primaryWeaponSlot?.render(delta, renderer)
        secondaryWeaponSlot?.render(delta, renderer)
        healthbar.render(delta, renderer)
        invulnerableIndicator.render(delta, renderer)
    }
}