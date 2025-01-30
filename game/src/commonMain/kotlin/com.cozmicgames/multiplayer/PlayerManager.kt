package com.cozmicgames.multiplayer

import com.cozmicgames.Game
import com.cozmicgames.utils.ShootStatistics
import com.cozmicgames.weapons.AreaEffectGrowthType
import com.cozmicgames.weapons.AreaEffectSourceType
import com.cozmicgames.weapons.AreaEffectType
import com.cozmicgames.weapons.ProjectileType
import com.littlekt.graphics.Color
import com.littlekt.graphics.MutableColor
import com.littlekt.graphics.toRgba8888
import com.littlekt.math.geom.degrees
import com.littlekt.util.seconds
import kotlin.time.Duration

class PlayerManager(private val multiplayer: Multiplayer) {
    val shootStatistics = ShootStatistics()
    var wallet = 10000
        private set

    val unlockedBossIndices = hashSetOf(0, 1, 2, 3)

    val unlockedWeaponIndices = hashSetOf(0)

    var newlyUnlockedBossIndex = -1

    var newlyUnlockedWeaponIndex = -1

    val players get() = playersInternal as List<Player>

    val isHost get() = multiplayer.isHost

    val roomCode get() = multiplayer.roomCode

    private val playersInternal = arrayListOf<Player>()

    private val availablePlayerColors = arrayListOf<Color>()

    private val availableAvatarIndices = arrayListOf<Int>()

    init {
        availablePlayerColors += Color.fromHex("df0024")
        availablePlayerColors += Color.fromHex("f3c300")
        availablePlayerColors += Color.fromHex("00ab9f")
        availablePlayerColors += Color.fromHex("2e6db4")

        repeat(108) {
            availableAvatarIndices += it
        }

        multiplayer.onPlayerJoin {
            if (isHost) {
                val color = availablePlayerColors.removeAt((0 until availablePlayerColors.size).random())
                val avatarIndex = availableAvatarIndices.removeAt((0 until availableAvatarIndices.size).random())
                val playerIndex = if (it == getMyPlayerState())
                    -1
                else
                    playersInternal.size

                it.setState("playerProfileColor", color.toRgba8888())
                it.setState("playerProfileAvatarIndex", avatarIndex)
                it.setState("playerIndex", playerIndex)

                val player = Player(it, color, avatarIndex, playerIndex)
                playersInternal += player
                playersInternal.sortBy { p -> p.index }

                it.onQuit {
                    playersInternal.remove(player)
                    Game.world.remove(player.ship)
                    availablePlayerColors += player.color
                    availableAvatarIndices += player.avatarIndex
                }
            } else {
                val color = MutableColor().setRgba8888(it.getState("playerProfileColor") ?: 0)
                val avatarIndex = it.getState("playerProfileAvatarIndex") ?: 0
                val playerIndex = it.getState("playerIndex") ?: 0

                val player = Player(it, color, avatarIndex, playerIndex)
                playersInternal += player
                playersInternal.sortBy { p -> p.index }

                it.onQuit {
                    //TODO: What if host quits?
                    playersInternal.remove(player)
                    Game.world.remove(player.ship)
                }
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
                val spawnProjectileCount = player.state.getState<Int>("spawnProjectileCount")
                val spawnProjectileSpeed = player.state.getState<Float>("spawnProjectileSpeed")
                val spawnProjectileSpeedFalloff = player.state.getState<Float>("spawnProjectileSpeedFalloff")

                if (spawnProjectileType != null && spawnProjectileX != null && spawnProjectileY != null && spawnProjectileCount != null && spawnProjectileSpeed != null && spawnProjectileSpeedFalloff != null) {
                    val directions = Array(spawnProjectileCount) {
                        player.state.getState<Float>("spawnProjectileDirection$it")?.degrees
                    }

                    if (directions.all { it != null })
                        directions.forEach {
                            Game.projectiles.spawnProjectile(player.ship, spawnProjectileType, spawnProjectileX, spawnProjectileY, it!!, spawnProjectileSpeed, spawnProjectileSpeedFalloff)
                        }

                    player.state.setState("spawnProjectileType", null)
                    player.state.setState("spawnProjectileX", null)
                    player.state.setState("spawnProjectileY", null)
                    player.state.setState("spawnProjectileCount", null)
                    repeat(spawnProjectileCount) {
                        player.state.setState("spawnProjectileDirection$it", null)
                    }
                    player.state.setState("spawnProjectileSpeed", null)
                    player.state.setState("spawnProjectileSpeedFalloff", null)

                    shootStatistics.shotsFired += spawnProjectileCount
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

            setGlobalState("credits", wallet)
        }

        for (player in playersInternal) {
            player.state.getState<Float>("x")?.let { player.ship.x = it }
            player.state.getState<Float>("y")?.let { player.ship.y = it }
            player.state.getState<Float>("rotation")?.let { player.ship.rotation = it.degrees }
        }

        wallet = getGlobalState("credits") ?: 0
    }

    fun getMyPlayerState() = multiplayer.getMyPlayerState()

    fun getMyPlayer() = playersInternal.find { it.state.id == getMyPlayerState().id }

    fun getByID(id: String) = playersInternal.find { it.state.id == id }

    fun <T : Any> getGlobalState(name: String) = multiplayer.getState<T>(name)

    fun <T : Any> setGlobalState(name: String, value: T) = multiplayer.setState(name, value)

    fun gainCredits(amount: Int) {
        if (!isHost)
            return

        wallet += amount
        setGlobalState("wallet", wallet)
    }

    fun spendCredits(amount: Int) {
        if (!isHost)
            return

        wallet -= amount
        setGlobalState("wallet", wallet)
    }
}