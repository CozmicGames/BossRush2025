package com.cozmicgames.multiplayer

import com.littlekt.graphics.g2d.SpriteBatch
import kotlin.time.Duration

class PlayerManager(private val multiplayer: Multiplayer) {
    private val players = arrayListOf<Player>()

    init {
        multiplayer.onPlayerJoin {
            players += Player(it)
        }
    }

    fun updatePlayers(delta: Duration) {
        if (multiplayer.isHost) {
            for (player in players) {
                player.state.getState<Float>("inputX")?.let {
                    player.ship.x += it * player.ship.movementSpeed
                }

                player.state.getState<Float>("inputY")?.let {
                    player.ship.y += it * player.ship.movementSpeed
                }

                player.state.getState<Float>("inputRotation")?.let {
                    player.ship.rotation += it * player.ship.rotationSpeed
                }

                player.state.getState<Boolean>("inputUsePrimary")?.let {
                    if (it)
                        player.ship.primaryFire()
                }

                player.state.getState<Boolean>("inputUseSecondary")?.let {
                    if (it)
                        player.ship.secondaryFire()
                }

                player.state.setState("x", player.ship.x)
                player.state.setState("y", player.ship.y)
                player.state.setState("rotation", player.ship.rotation)
            }
        }

        for (player in players) {
            player.state.getState<Float>("x")?.let { player.ship.x = it }
            player.state.getState<Float>("y")?.let { player.ship.y = it }
            player.state.getState<Float>("rotation")?.let { player.ship.rotation = it }
        }
    }

    fun renderPlayers(batch: SpriteBatch) {
        for (player in players)
            player.ship.render(batch)
    }

    fun getMyPlayerState() = multiplayer.getMyPlayerState()

    fun <T : Any> getGlobalState(name: String) = multiplayer.getState<T>(name)

    fun <T : Any> setGlobalState(name: String, value: T) = multiplayer.setState(name, value)
}