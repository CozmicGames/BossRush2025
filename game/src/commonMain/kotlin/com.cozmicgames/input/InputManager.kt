package com.cozmicgames.input

import com.cozmicgames.Game
import com.littlekt.input.*
import com.littlekt.math.geom.Point
import kotlin.js.Date
import kotlin.time.Duration

class InputManager(private val input: Input) : Input {
    val moveLeftControl = Game.controls.add("moveLeft")
    val moveRightControl = Game.controls.add("moveRight")

    init {
        moveLeftControl.addKey(Key.A)
        moveLeftControl.addKey(Key.ARROW_LEFT)
        moveLeftControl.setLeftJoystickAxis(JoystickAxis.X, 0.1f)

        moveRightControl.addKey(Key.D)
        moveRightControl.addKey(Key.ARROW_RIGHT)
        moveRightControl.setLeftJoystickAxis(JoystickAxis.X, 0.1f)
    }

    fun update(delta: Duration, state: InputState) {
        val frame = InputFrame()

        val leftAmount = moveLeftControl.currentValue
        val rightAmount = moveRightControl.currentValue

        frame.timestamp = Date.now()
        frame.deltaX = -leftAmount + rightAmount
        frame.deltaY = 0.0f
        frame.usePrimary = false
        frame.useSecondary = false

        state.inputFrames.add(frame)
    }

    override val axisLeftX: Float
        get() = input.axisLeftX
    override val axisLeftY: Float
        get() = input.axisLeftY
    override val axisRightX: Float
        get() = input.axisRightX
    override val axisRightY: Float
        get() = input.axisRightY
    override val catchKeys: MutableList<Key>
        get() = input.catchKeys
    override val connectedGamepads: List<GamepadInfo>
        get() = input.connectedGamepads
    override val currentEventTime: Long
        get() = input.currentEventTime
    override val deltaX: Int
        get() = input.deltaX
    override val deltaY: Int
        get() = input.deltaY
    override val gamepads: Array<GamepadInfo>
        get() = input.gamepads
    override val inputProcessors: List<InputProcessor>
        get() = input.inputProcessors
    override val isTouching: Boolean
        get() = input.isTouching
    override val justTouched: Boolean
        get() = input.justTouched
    override val pressure: Float
        get() = input.pressure
    override val x: Int
        get() = input.x
    override val y: Int
        get() = input.y

    override fun addInputProcessor(processor: InputProcessor) {
        input.addInputProcessor(processor)
    }

    override fun getDeltaX(pointer: Pointer): Int {
        return input.getDeltaX(pointer)
    }

    override fun getDeltaY(pointer: Pointer): Int {
        return input.getDeltaY(pointer)
    }

    override fun getGamepadButtonPressure(button: GameButton, gamepad: Int): Float {
        return input.getGamepadButtonPressure(button, gamepad)
    }

    override fun getGamepadJoystickDistance(stick: GameStick, gamepad: Int): Point {
        return input.getGamepadJoystickDistance(stick, gamepad)
    }

    override fun getGamepadJoystickXDistance(stick: GameStick, gamepad: Int): Float {
        return input.getGamepadJoystickXDistance(stick, gamepad)
    }

    override fun getGamepadJoystickYDistance(stick: GameStick, gamepad: Int): Float {
        return input.getGamepadJoystickYDistance(stick, gamepad)
    }

    override fun getPressure(pointer: Pointer): Float {
        return input.getPressure(pointer)
    }

    override fun getX(pointer: Pointer): Int {
        return input.getX(pointer)
    }

    override fun getY(pointer: Pointer): Int {
        return input.getY(pointer)
    }

    override fun hideSoftKeyboard() {
        input.hideSoftKeyboard()
    }

    override fun isGamepadButtonJustPressed(button: GameButton, gamepad: Int): Boolean {
        return input.isGamepadButtonJustPressed(button, gamepad)
    }

    override fun isGamepadButtonJustReleased(button: GameButton, gamepad: Int): Boolean {
        return input.isGamepadButtonJustReleased(button, gamepad)
    }

    override fun isGamepadButtonPressed(button: GameButton, gamepad: Int): Boolean {
        return input.isGamepadButtonPressed(button, gamepad)
    }

    override fun isJustTouched(pointer: Pointer): Boolean {
        return input.isJustTouched(pointer)
    }

    override fun isKeyJustPressed(key: Key): Boolean {
        return input.isKeyJustPressed(key)
    }

    override fun isKeyJustReleased(key: Key): Boolean {
        return input.isKeyJustReleased(key)
    }

    override fun isKeyPressed(key: Key): Boolean {
        return input.isKeyPressed(key)
    }

    override fun isTouchJustReleased(pointer: Pointer): Boolean {
        return input.isTouchJustReleased(pointer)
    }

    override fun isTouching(pointer: Pointer): Boolean {
        return input.isTouching(pointer)
    }

    override fun isTouching(totalPointers: Int): Boolean {
        return input.isTouching(totalPointers)
    }

    override fun removeInputProcessor(processor: InputProcessor) {
        input.removeInputProcessor(processor)
    }

    override fun setCursorPosition(x: Int, y: Int) {
        input.setCursorPosition(x, y)
    }

    override fun showSoftKeyboard() {
        input.showSoftKeyboard()
    }
}