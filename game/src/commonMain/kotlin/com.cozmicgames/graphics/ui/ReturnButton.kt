package com.cozmicgames.graphics.ui

import com.cozmicgames.Game
import com.cozmicgames.graphics.ui.elements.IconButton
import com.littlekt.graphics.Color

open class ReturnButton(onClick: () -> Unit) : IconButton(Game.resources.returnIcon, Color.fromHex("0098dc"), 0.8f, onClick = onClick)