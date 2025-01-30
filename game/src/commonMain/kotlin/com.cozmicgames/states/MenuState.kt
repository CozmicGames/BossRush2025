package com.cozmicgames.states

import com.cozmicgames.Game
import com.cozmicgames.graphics.Background
import com.cozmicgames.graphics.Renderer
import com.cozmicgames.graphics.Transition
import com.cozmicgames.graphics.ui.GUICamera
import com.cozmicgames.graphics.ui.GameLogo
import com.cozmicgames.graphics.ui.PlayerSlot
import com.cozmicgames.graphics.ui.elements.Label
import com.cozmicgames.graphics.ui.elements.TextButton
import com.littlekt.graphics.Color
import com.littlekt.graphics.HAlign
import kotlin.time.Duration

class MenuState : GameState {
    private var returnState: GameState = this

    private lateinit var guiCamera: GUICamera
    private lateinit var background: Background
    private lateinit var transitionOut: Transition
    private lateinit var logo: GameLogo

    private lateinit var playerSlots: List<PlayerSlot>
    private lateinit var startButton: TextButton
    private lateinit var waitingLabel: Label
    private lateinit var tutorialButton: TextButton
    private lateinit var roomCodeLabel: Label

    private var isFirstFrame = true
    private var showMenu = false

    override fun begin() {
        guiCamera = GUICamera()
        background = Background(Game.resources.background)
        transitionOut = Transition(fromOpenToClose = true)
        logo = GameLogo()

        val playerSlotSize = 110.0f
        val playerSlotSpacing = 20.0f
        val playerSlotToButtonSpacing = 40.0f
        val buttonWidth = 200.0f
        val buttonSpacing = 10.0f

        val buttonHeight = (playerSlotSize - buttonSpacing) * 0.5f

        val startY = 40.0f
        val startX = (Game.graphics.width - (4 * playerSlotSize + 3 * playerSlotSpacing + playerSlotToButtonSpacing + buttonWidth)) * 0.5f

        val playerSlots = arrayListOf<PlayerSlot>()
        repeat(4) {
            val slot = PlayerSlot(it)
            slot.getX = { startX + it * (playerSlotSize + playerSlotSpacing) }
            slot.getY = { startY }
            slot.getWidth = { playerSlotSize }
            slot.getHeight = { playerSlotSize }

            playerSlots += slot
        }
        this.playerSlots = playerSlots

        if (Game.players.isHost) {
            startButton = TextButton("Start", Color.fromHex("33984b"), fontSize = 28.0f) {
                transitionOut.start { returnState = BayState() }
            }

            startButton.getX = { startX + (4 * playerSlotSize + 3 * playerSlotSpacing + playerSlotToButtonSpacing) }
            startButton.getY = { startY + buttonHeight + buttonSpacing }
            startButton.getWidth = { buttonWidth }
            startButton.getHeight = { buttonHeight }
        } else {
            waitingLabel = Label("Waiting", 28.0f)
            waitingLabel.background = Game.resources.buttonNormalNinePatch
            waitingLabel.backgroundColor.set(Color.fromHex("33984b"))

            waitingLabel.getX = { startX + (4 * playerSlotSize + 3 * playerSlotSpacing + playerSlotToButtonSpacing) }
            waitingLabel.getY = { startY + buttonHeight + buttonSpacing }
            waitingLabel.getWidth = { buttonWidth }
            waitingLabel.getHeight = { buttonHeight }
        }

        tutorialButton = TextButton("Tutorial", Color.fromHex("e07438"), fontSize = 28.0f) {
            transitionOut.start { returnState = TutorialState() }
        }

        tutorialButton.getX = { startX + (4 * playerSlotSize + 3 * playerSlotSpacing + playerSlotToButtonSpacing) }
        tutorialButton.getY = { startY }
        tutorialButton.getWidth = { buttonWidth }
        tutorialButton.getHeight = { buttonHeight }

        roomCodeLabel = Label("Room: ${Game.players.roomCode}", 20.0f, Color.WHITE)
        roomCodeLabel.getX = { 5.0f }
        roomCodeLabel.getY = { 10.0f }
        roomCodeLabel.hAlign = HAlign.LEFT

        Game.resources.themeSound.play(0.5f, true)
    }

    override fun resize(width: Int, height: Int) {
        guiCamera.resize(width, height)
    }

    override fun render(delta: Duration): () -> GameState {
        if (isFirstFrame) {
            //TODO: Player overview, start and tutorial button
            logo.startAnimation {
                showMenu = true
            }
            isFirstFrame = false
        }

        val pass = Game.graphics.beginMainRenderPass()

        pass.render(guiCamera.camera) { renderer: Renderer ->
            background.render(delta, renderer)
            transitionOut.render(delta, renderer)
            logo.render(delta, renderer)

            if (showMenu) {
                playerSlots.forEach {
                    it.render(delta, renderer)
                }

                startButton.render(delta, renderer)
                tutorialButton.render(delta, renderer)
                roomCodeLabel.render(delta, renderer)
            }
        }

        pass.end()

        return { returnState }
    }
}