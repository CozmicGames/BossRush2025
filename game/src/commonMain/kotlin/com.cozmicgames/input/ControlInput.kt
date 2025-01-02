package com.cozmicgames.input

import com.cozmicgames.Game
import com.littlekt.input.GameButton
import com.littlekt.input.GameStick
import com.littlekt.input.Key
import com.littlekt.input.Pointer
import kotlin.math.absoluteValue

interface ControlInput {
    val isTriggered: Boolean
    val currentValue: Float

    fun update(action: ControlAction)
}

class KeyControlInput : ControlInput {
    val keys = hashSetOf<Key>()

    override var isTriggered = false
        private set

    override var currentValue = 0.0f
        private set

    override fun update(action: ControlAction) {
        isTriggered = keys.any { Game.input.isKeyJustPressed(it) }
        currentValue = if (keys.any { Game.input.isKeyPressed(it) }) 1.0f else 0.0f
    }
}

class MouseButtonControlInput : ControlInput {
    val buttons = hashSetOf<Pointer>()

    override var isTriggered = false
        private set

    override var currentValue = 0.0f
        private set

    override fun update(action: ControlAction) {
        isTriggered = buttons.any { Game.input.isJustTouched(it) }
        currentValue = if (buttons.any { Game.input.isTouching(it) }) 1.0f else 0.0f
    }
}

class MouseDeltaXControlInput : ControlInput {
    override var isTriggered = false
        private set

    override var currentValue = 0.0f
        private set

    override fun update(action: ControlAction) {
        currentValue = Game.input.deltaX.toFloat()
    }
}

class MouseDeltaYControlInput : ControlInput {
    override var isTriggered = false
        private set

    override var currentValue = 0.0f
        private set

    override fun update(action: ControlAction) {
        currentValue = Game.input.deltaY.toFloat()
    }
}

class GamepadButtonControlInput : ControlInput {
    val buttons = hashSetOf<GameButton>()

    override var isTriggered = false
        private set

    override var currentValue = 0.0f
        private set

    override fun update(action: ControlAction) {
        isTriggered = buttons.any { Game.input.isGamepadButtonJustPressed(it, Game.controls.activeGamepad) }
        currentValue = if (buttons.any { Game.input.isGamepadButtonPressed(it, Game.controls.activeGamepad) }) 1.0f else 0.0f
    }
}

enum class JoystickAxis {
    X, Y
}

class GamepadLeftJoystickAxisControlInput(var axis: JoystickAxis, var threshold: Float = 0.25f) : ControlInput {
    override var isTriggered = false
        private set

    override var currentValue = 0.0f
        private set

    override fun update(action: ControlAction) {
        val value = when (axis) {
            JoystickAxis.X -> Game.input.getGamepadJoystickXDistance(GameStick.LEFT, Game.controls.activeGamepad)
            JoystickAxis.Y -> Game.input.getGamepadJoystickYDistance(GameStick.LEFT, Game.controls.activeGamepad)
        }
        isTriggered = value.absoluteValue >= threshold
        currentValue = value
    }
}

class GamepadRightJoystickAxisControlInput(var axis: JoystickAxis, var threshold: Float = 0.25f) : ControlInput {
    override var isTriggered = false
        private set

    override var currentValue = 0.0f
        private set

    override fun update(action: ControlAction) {
        val value = when (axis) {
            JoystickAxis.X -> Game.input.getGamepadJoystickXDistance(GameStick.RIGHT, Game.controls.activeGamepad)
            JoystickAxis.Y -> Game.input.getGamepadJoystickYDistance(GameStick.RIGHT, Game.controls.activeGamepad)
        }
        isTriggered = value.absoluteValue >= threshold
        currentValue = value
    }
}
