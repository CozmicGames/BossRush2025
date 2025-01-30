package com.cozmicgames.multiplayer

import com.cozmicgames.Game
import com.cozmicgames.entities.worldObjects.PlayerShip
import com.cozmicgames.weapons.Weapon
import com.cozmicgames.weapons.Weapons
import com.littlekt.Releasable
import com.littlekt.graphics.Color
import com.littlekt.graphics.MutableColor
import com.littlekt.graphics.OrthographicCamera
import com.littlekt.graphics.Texture

class Player(val state: PlayerState, val color: Color, val avatarIndex: Int, val index: Int) : Releasable {
    val isReadyToStart get() = state.getState<Boolean>("readyToStart") == true

    var primaryWeapon: Weapon? = Weapons.REELGUN
    var secondaryWeapon: Weapon? = Weapons.REELGUN

    val ship = PlayerShip(this)
    val indicatorColor = MutableColor(1.0f, 1.0f, 1.0f, 0.0f)

    val camera by lazy { OrthographicCamera(Game.graphics.width.toFloat(), Game.graphics.height.toFloat()) }

    var avatarTexture: Texture? = null
        private set

    init {
        Game.resources.requestAvatarTexture(avatarIndex) {
            avatarTexture = it
        }
    }

    override fun release() {
        avatarTexture?.release()
    }
}