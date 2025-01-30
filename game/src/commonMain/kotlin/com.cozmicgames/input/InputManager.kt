package com.cozmicgames.input

import com.cozmicgames.Constants
import com.cozmicgames.Game
import com.littlekt.input.*
import com.littlekt.math.geom.Point
import com.littlekt.math.geom.radians
import com.littlekt.math.geom.shortDistanceTo
import com.littlekt.util.seconds
import kotlin.js.Date
import kotlin.math.atan2
import kotlin.time.Duration
import kotlin.time.DurationUnit

class InputManager(private val input: Input) : Input {
    private val moveLeftControl = Game.controls.add("moveLeft")
    private val moveRightControl = Game.controls.add("moveRight")
    private val moveUpControl = Game.controls.add("moveUp")
    private val moveDownControl = Game.controls.add("moveDown")
    private val rotateControl = Game.controls.add("rotate")
    private val usePrimaryControl = Game.controls.add("usePrimary")
    private val useSecondaryControl = Game.controls.add("useSecondary")

    private var recordInputState: InputState? = null
    private var recordStartTime = 0.0
    private var recordDuration = 0.0
    private var recordCallback: ((InputState) -> Unit)? = null

    init {
        moveLeftControl.rampUpSpeed = Constants.INPUT_RAMP_UP_SPEED
        moveLeftControl.rampDownSpeed = Constants.INPUT_RAMP_DOWN_SPEED

        moveRightControl.rampUpSpeed = Constants.INPUT_RAMP_UP_SPEED
        moveRightControl.rampDownSpeed = Constants.INPUT_RAMP_DOWN_SPEED

        moveUpControl.rampUpSpeed = Constants.INPUT_RAMP_UP_SPEED
        moveUpControl.rampDownSpeed = Constants.INPUT_RAMP_DOWN_SPEED

        moveDownControl.rampUpSpeed = Constants.INPUT_RAMP_UP_SPEED
        moveDownControl.rampDownSpeed = Constants.INPUT_RAMP_DOWN_SPEED

        moveLeftControl.addKey(Key.A)
        moveLeftControl.addKey(Key.ARROW_LEFT)
        moveLeftControl.setLeftJoystickAxis(JoystickAxis.X, 0.1f)

        moveRightControl.addKey(Key.D)
        moveRightControl.addKey(Key.ARROW_RIGHT)
        moveRightControl.setLeftJoystickAxis(JoystickAxis.X, 0.1f)

        moveUpControl.addKey(Key.W)
        moveUpControl.addKey(Key.ARROW_UP)
        moveUpControl.setLeftJoystickAxis(JoystickAxis.Y, 0.1f)

        moveDownControl.addKey(Key.S)
        moveDownControl.addKey(Key.ARROW_DOWN)
        moveDownControl.setLeftJoystickAxis(JoystickAxis.Y, 0.1f)

        rotateControl.setDeltaY()
        rotateControl.setRightJoystickAxis(JoystickAxis.Y, 0.1f)

        usePrimaryControl.addKey(Key.SPACE)
        usePrimaryControl.addMouseButton(Pointer.MOUSE_LEFT)
        usePrimaryControl.setGamepadButton(GameButton.R1)

        useSecondaryControl.addKey(Key.SHIFT_LEFT)
        useSecondaryControl.addMouseButton(Pointer.MOUSE_MIDDLE)
        useSecondaryControl.addMouseButton(Pointer.MOUSE_RIGHT)
        useSecondaryControl.setGamepadButton(GameButton.R2)
    }

    fun recordInputState(inputState: InputState, duration: Duration, callback: (InputState) -> Unit) {
        recordInputState = inputState
        recordDuration = duration.toDouble(DurationUnit.MILLISECONDS)
        recordStartTime = Date.now()
        recordCallback = callback
    }

    fun update(delta: Duration, frame: InputFrame) {
        val player = Game.player

        val leftAmount = moveLeftControl.currentValue
        val rightAmount = moveRightControl.currentValue
        val upAmount = moveUpControl.currentValue
        val downAmount = moveDownControl.currentValue

        val currentTime = Date.now()

        val deltaX = (-leftAmount + rightAmount) * delta.seconds
        val deltaY = (-downAmount + upAmount) * delta.seconds

        val shipPosition = player.camera.worldToScreen(Game.context, player.ship.x, player.ship.y)
        val angle = atan2((player.camera.virtualHeight - y - 1) - shipPosition.y, x - shipPosition.x).radians
        val deltaRotation = player.ship.rotation.shortDistanceTo(angle).radians * delta.seconds

        frame.timestamp = currentTime
        frame.deltaX = deltaX
        frame.deltaY = deltaY
        frame.deltaRotation = deltaRotation
        frame.usePrimary = if (player.ship.primaryWeapon?.canContinoousFire == true) usePrimaryControl.state else usePrimaryControl.isTriggered
        frame.useSecondary = if (player.ship.secondaryWeapon?.canContinoousFire == true) useSecondaryControl.state else useSecondaryControl.isTriggered

        if (recordInputState != null && currentTime - recordStartTime >= recordDuration) {
            recordCallback?.invoke(recordInputState!!)
            recordInputState = null
            recordCallback = null
        } else
            recordInputState?.inputFrames?.add(frame)
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