package com.cozmicgames.input

import com.littlekt.input.GameButton
import com.littlekt.input.Key
import com.littlekt.input.Pointer
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.KClass

class ControlAction(var name: String) {
    var deadZone = 0.01f
    var rampUpSpeed = Float.MAX_VALUE
    var rampDownSpeed = Float.MAX_VALUE

    val state get() = abs(currentValueRaw) >= deadZone

    var currentValue = 0.0f
        private set

    var currentValueRaw = 0.0f
        private set

    var isTriggered = false
        private set

    var isEnabled = true

    private val inputs = hashMapOf<KClass<*>, ControlInput>()

    fun update(delta: Float) {
        if (!isEnabled) {
            isTriggered = false
            currentValueRaw = 0.0f
            currentValue = 0.0f
            return
        }

        if (inputs.isEmpty())
            return

        inputs.forEach { (_, input) ->
            input.update(this)
        }

        isTriggered = inputs.any { (_, input) -> input.isTriggered }

        val currentValueInput = inputs.maxBy { (_, input) -> input.currentValue }.value
        currentValueRaw = currentValueInput.currentValue

        currentValue = if (currentValueRaw <= deadZone)
            max(0.0f, abs(currentValue) - rampDownSpeed * delta)
        else
            min(currentValueRaw, abs(currentValue) + rampUpSpeed * delta)

        currentValue *= currentValueInput.currentValueSign
        currentValueRaw *= currentValueInput.currentValueSign
    }

    fun addKey(key: Key) {
        val input = inputs.getOrPut(KeyControlInput::class) { KeyControlInput() } as KeyControlInput

        input.keys += key
    }

    fun removeKey(key: Key) {
        val input = (inputs[KeyControlInput::class] as? KeyControlInput) ?: return
        input.keys -= key

        if (input.keys.isEmpty())
            inputs.remove(KeyControlInput::class)
    }

    fun clearKeys() {
        inputs.remove(KeyControlInput::class)
    }

    fun getKeys(): Set<Key> {
        val input = (inputs[KeyControlInput::class] as? KeyControlInput) ?: return emptySet()
        return input.keys.toSet()
    }

    fun addMouseButton(button: Pointer) {
        val input = inputs.getOrPut(MouseButtonControlInput::class) { MouseButtonControlInput() } as MouseButtonControlInput

        input.buttons += button
    }

    fun removeMouseButton(button: Pointer) {
        val input = (inputs[MouseButtonControlInput::class] as? MouseButtonControlInput) ?: return
        input.buttons -= button

        if (input.buttons.isEmpty())
            inputs.remove(MouseButtonControlInput::class)
    }

    fun clearMouseButtons() {
        inputs.remove(MouseButtonControlInput::class)
    }

    fun getMouseButtons(): Set<Pointer> {
        val input = (inputs[MouseButtonControlInput::class] as? MouseButtonControlInput) ?: return emptySet()
        return input.buttons.toSet()
    }

    fun setDeltaX() {
        inputs.getOrPut(MouseDeltaXControlInput::class) { MouseDeltaXControlInput() }
    }

    fun unsetDeltaX() {
        inputs.remove(MouseDeltaXControlInput::class)
    }

    fun isDeltaX(): Boolean {
        return MouseDeltaXControlInput::class in inputs
    }

    fun setDeltaY() {
        inputs.getOrPut(MouseDeltaYControlInput::class) { MouseDeltaYControlInput() }
    }

    fun unsetDeltaY() {
        inputs.remove(MouseDeltaYControlInput::class)
    }

    fun isDeltaY(): Boolean {
        return MouseDeltaYControlInput::class in inputs
    }

    fun setGamepadButton(button: GameButton) {
        val input = inputs.getOrPut(GamepadButtonControlInput::class) { GamepadButtonControlInput() } as GamepadButtonControlInput

        input.buttons += button
    }

    fun removeGamepadButton(button: GameButton) {
        val input = (inputs[GamepadButtonControlInput::class] as? GamepadButtonControlInput) ?: return
        input.buttons -= button

        if (input.buttons.isEmpty())
            inputs.remove(GamepadButtonControlInput::class)
    }

    fun clearGamepadButtons() {
        inputs.remove(GamepadButtonControlInput::class)
    }

    fun getGamepadButtons(): Set<GameButton> {
        val input = (inputs[GamepadButtonControlInput::class] as? GamepadButtonControlInput) ?: return emptySet()
        return input.buttons.toSet()
    }

    fun setLeftJoystickAxis(axis: JoystickAxis, threshold: Float = 0.25f) {
        val input = inputs.getOrPut(GamepadLeftJoystickAxisControlInput::class) { GamepadLeftJoystickAxisControlInput(axis, threshold) } as GamepadLeftJoystickAxisControlInput

        input.axis = axis
        input.threshold = threshold
    }

    fun unsetLeftJoystickAxis() {
        inputs.remove(GamepadLeftJoystickAxisControlInput::class)
    }

    fun isLeftJoystickAxis(): Boolean {
        return GamepadLeftJoystickAxisControlInput::class in inputs
    }

    fun setRightJoystickAxis(axis: JoystickAxis, threshold: Float = 0.25f) {
        val input = inputs.getOrPut(GamepadRightJoystickAxisControlInput::class) { GamepadRightJoystickAxisControlInput(axis, threshold) } as GamepadRightJoystickAxisControlInput

        input.axis = axis
        input.threshold = threshold
    }

    fun unsetRightJoystickAxis() {
        inputs.remove(GamepadRightJoystickAxisControlInput::class)
    }

    fun isRightJoystickAxis(): Boolean {
        return GamepadRightJoystickAxisControlInput::class in inputs
    }
}
