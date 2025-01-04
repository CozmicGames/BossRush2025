package com.cozmicgames.multiplayer

import com.cozmicgames.Game
import com.cozmicgames.weapons.ProjectileType
import com.littlekt.graphics.g2d.SpriteBatch
import com.littlekt.math.geom.degrees
import kotlin.time.Duration

class PlayerManager(private val multiplayer: Multiplayer) {
    private val players = arrayListOf<Player>()

    val isHost get() = multiplayer.isHost

    init {
        multiplayer.onPlayerJoin {
            players += Player(it)
        }
    }

    fun update(delta: Duration) {
        if (multiplayer.isHost) {
            for (player in players) {
                player.state.getState<Float>("inputX")?.let {
                    player.ship.deltaX = it
                }

                player.state.getState<Float>("inputY")?.let {
                    player.ship.deltaY = it
                }

                player.state.getState<Float>("inputRotation")?.let {
                    player.ship.deltaRotation = it
                }

                player.state.getState<Boolean>("inputUsePrimary")?.let {
                    if (it)
                        player.ship.primaryFire()
                }

                player.state.getState<Boolean>("inputUseSecondary")?.let {
                    if (it)
                        player.ship.secondaryFire()
                }

                player.ship.update(delta)

                player.state.setState("x", player.ship.x)
                player.state.setState("y", player.ship.y)
                player.state.setState("rotation", player.ship.rotation.degrees)

                val spawnProjectileType = ProjectileType.entries.getOrNull(player.state.getState("spawnProjectileType") ?: -1)
                val spawnProjectileX = player.state.getState<Float>("spawnProjectileX")
                val spawnProjectileY = player.state.getState<Float>("spawnProjectileY")
                val spawnProjectileDirectionX = player.state.getState<Float>("spawnProjectileDirectionX")
                val spawnProjectileDirectionY = player.state.getState<Float>("spawnProjectileDirectionY")
                val spawnProjectileSpeed = player.state.getState<Float>("spawnProjectileSpeed")

                if (spawnProjectileType != null && spawnProjectileX != null && spawnProjectileY != null && spawnProjectileDirectionX != null && spawnProjectileDirectionY != null && spawnProjectileSpeed != null) {
                    Game.projectiles.spawnProjectile(spawnProjectileType, spawnProjectileX, spawnProjectileY, spawnProjectileDirectionX, spawnProjectileDirectionY, spawnProjectileSpeed)

                    player.state.setState("spawnProjectileType", null)
                    player.state.setState("spawnProjectileX", null)
                    player.state.setState("spawnProjectileY", null)
                    player.state.setState("spawnProjectileDirectionX", null)
                    player.state.setState("spawnProjectileDirectionY", null)
                    player.state.setState("spawnProjectileSpeed", null)
                }
            }
        }

        for (player in players) {
            player.state.getState<Float>("x")?.let { player.ship.x = it }
            player.state.getState<Float>("y")?.let { player.ship.y = it }
            player.state.getState<Float>("rotation")?.let { player.ship.rotation = it.degrees }
        }
    }

    fun renderPlayers(batch: SpriteBatch) {
        for (player in players)
            player.ship.render(batch)
    }

    fun getMyPlayerState() = multiplayer.getMyPlayerState()

    fun getMyPlayer() = players.find { it.state.id == getMyPlayerState().id }

    fun <T : Any> getGlobalState(name: String) = multiplayer.getState<T>(name)

    fun <T : Any> setGlobalState(name: String, value: T) = multiplayer.setState(name, value)
}