package com.cozmicgames.multiplayer

import com.cozmicgames.Game
import com.cozmicgames.weapons.AreaEffectGrowthType
import com.cozmicgames.weapons.AreaEffectSourceType
import com.cozmicgames.weapons.AreaEffectType
import com.cozmicgames.weapons.ProjectileType
import com.littlekt.math.geom.degrees
import com.littlekt.util.seconds
import kotlin.time.Duration

class PlayerManager(private val multiplayer: Multiplayer) {
    private val playersInternal = arrayListOf<Player>()

    val players get() = playersInternal as List<Player>

    val isHost get() = multiplayer.isHost

    init {
        multiplayer.onPlayerJoin {
            val player = Player(it)
            playersInternal += player

            it.onQuit {
                playersInternal.remove(player)
                Game.world.remove(player.ship)
            }
        }
    }

    fun getById(id: String) = playersInternal.find { it.state.id == id }

    fun update(delta: Duration) {
        if (multiplayer.isHost) {
            for (player in playersInternal) {
                player.state.setState("x", player.ship.x)
                player.state.setState("y", player.ship.y)
                player.state.setState("rotation", player.ship.rotation.degrees)

                val spawnProjectileType = ProjectileType.entries.getOrNull(player.state.getState("spawnProjectileType") ?: -1)
                val spawnProjectileX = player.state.getState<Float>("spawnProjectileX")
                val spawnProjectileY = player.state.getState<Float>("spawnProjectileY")
                val spawnProjectileDirection = player.state.getState<Float>("spawnProjectileDirection")?.degrees
                val spawnProjectileSpeed = player.state.getState<Float>("spawnProjectileSpeed")

                if (spawnProjectileType != null && spawnProjectileX != null && spawnProjectileY != null && spawnProjectileDirection != null && spawnProjectileSpeed != null) {
                    Game.projectiles.spawnProjectile(player.ship, spawnProjectileType, spawnProjectileX, spawnProjectileY, spawnProjectileDirection, spawnProjectileSpeed)

                    player.state.setState("spawnProjectileType", null)
                    player.state.setState("spawnProjectileX", null)
                    player.state.setState("spawnProjectileY", null)
                    player.state.setState("spawnProjectileDirection", null)
                    player.state.setState("spawnProjectileSpeed", null)
                }

                val stopBeamProjectile = player.state.getState<Boolean>("stopBeamProjectile")

                if (stopBeamProjectile == true) {
                    Game.projectiles.stopBeamProjectile(player.ship)

                    player.state.setState("stopBeamProjectile", null)
                }

                val spawnAreaEffectType = AreaEffectType.entries.getOrNull(player.state.getState("spawnAreaEffectType") ?: -1)
                val spawnAreaEffectSourceType = AreaEffectSourceType.entries.getOrNull(player.state.getState("spawnAreaEffectSourceType") ?: -1)
                val spawnAreaEffectGrowthType = AreaEffectGrowthType.entries.getOrNull(player.state.getState("spawnAreaEffectGrowthType") ?: -1)
                val spawnAreaEffectRadius = player.state.getState<Float>("spawnAreaEffectRadius")
                val spawnAreaEffectGrowRate = player.state.getState<Float>("spawnAreaEffectGrowRate")
                val spawnAreaEffectDuration = player.state.getState<Float>("spawnAreaEffectDuration")?.seconds

                if (spawnAreaEffectType != null && spawnAreaEffectSourceType != null && spawnAreaEffectGrowthType != null && spawnAreaEffectRadius != null && spawnAreaEffectGrowRate != null && spawnAreaEffectDuration != null) {
                    Game.areaEffects.spawnEffect(player.ship, spawnAreaEffectType, spawnAreaEffectSourceType, spawnAreaEffectGrowthType, spawnAreaEffectRadius, spawnAreaEffectGrowRate, spawnAreaEffectDuration)

                    player.state.setState("spawnAreaEffectType", null)
                    player.state.setState("spawnAreaEffectSourceType", null)
                    player.state.setState("spawnAreaEffectGrowthType", null)
                    player.state.setState("spawnAreaEffectRadius", null)
                    player.state.setState("spawnAreaEffectDuration", null)
                }

                val eventCount = player.state.getState<Int>("sendEventCount")
                if (eventCount != null) {
                    for (i in 0 until eventCount) {
                        val event = player.state.getState<String>("sendEvent$i")
                        if (event != null)
                            Game.events.addProcessEvent(event)
                    }

                    player.state.setState("sendEventCount", null)
                }
            }

            for (player in playersInternal)
                Game.events.sendProcessEvents(player.state)

            Game.events.clearProcessEvents()
        }

        for (player in playersInternal) {
            player.state.getState<Float>("x")?.let { player.ship.x = it }
            player.state.getState<Float>("y")?.let { player.ship.y = it }
            player.state.getState<Float>("rotation")?.let { player.ship.rotation = it.degrees }
        }
    }

    fun getMyPlayerState() = multiplayer.getMyPlayerState()

    fun getMyPlayer() = playersInternal.find { it.state.id == getMyPlayerState().id }

    fun <T : Any> getGlobalState(name: String) = multiplayer.getState<T>(name)

    fun <T : Any> setGlobalState(name: String, value: T) = multiplayer.setState(name, value)
}