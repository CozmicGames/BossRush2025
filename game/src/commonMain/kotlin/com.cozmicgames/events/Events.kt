package com.cozmicgames.events

import com.cozmicgames.Game
import com.cozmicgames.physics.Grabbable
import com.cozmicgames.utils.AfterFightAction
import com.cozmicgames.utils.Difficulty
import com.cozmicgames.utils.FightResults
import com.cozmicgames.weapons.Weapons
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

object Events {
    fun startGame() = "startGame"

    fun enterTutorial(id: String) = "enterTutorial:$id"

    fun exitTutorial(id: String) = "exitTutorial:$id"

    fun hit(id: String): String = "hit:$id"

    fun impulseHit(id: String, x: Float, y: Float, strength: Float): String = "impulseHit:$id,$x,$y,$strength"

    fun grab(id: String, fromId: String): String = "grab:$id,$fromId"

    fun release(id: String, impulseX: Float = 0.0f, impulseY: Float = 0.0f): String = "release:$id,$impulseX,$impulseY"

    fun setPrimaryWeapon(id: String, index: Int): String = "setPrimaryWeapon:$id,$index"

    fun setSecondaryWeapon(id: String, index: Int): String = "setSecondaryWeapon:$id,$index"

    fun unlockWeapon(index: Int): String = "unlockWeapon:$index"

    fun startFight(index: Int): String = "startFight:$index"

    fun retry(difficulty: Difficulty): String = "retry:${difficulty.ordinal}"

    fun exitFight(): String = "returnToBay"

    fun results(duration: Duration, difficulty: Difficulty, bossFullHealth: Int, bossFinalHealth: Int, playerAverageFinalHealth: Int, shotsFired: Int, shotsHit: Int): String {
        return "results:${duration.inWholeMilliseconds},${difficulty.ordinal},$bossFullHealth,$bossFinalHealth,$playerAverageFinalHealth,$shotsFired,$shotsHit"
    }

    fun unlockBoss(index: Int): String = "unlockBoss:$index"

    fun process(event: String) {
        when {
            event.startsWith("startGame") -> {
                Game.game.startGame = true
            }

            event.startsWith("enterTutorial") -> {
                val id = event.substringAfter(":")
                Game.players.getByID(id)?.isReadyToStart = false
            }

            event.startsWith("exitTutorial") -> {
                val id = event.substringAfter(":")
                Game.players.getByID(id)?.isReadyToStart = true
            }

            event.startsWith("hit") -> {
                val id = event.substringAfter(":")
                val hittable = Game.physics.hittables[id]
                hittable?.onDamageHit()
            }

            event.startsWith("impulseHit") -> {
                val data = event.substringAfter(":")
                val parts = data.split(",")
                if (parts.size == 4) {
                    val id = parts[0]
                    val x = parts[1].toFloatOrNull()
                    val y = parts[2].toFloatOrNull()
                    val strength = parts[3].toFloatOrNull()

                    val hittable = Game.physics.hittables[id]
                    if (hittable != null && x != null && y != null && strength != null)
                        hittable.onImpulseHit(x, y, strength)
                }
            }

            event.startsWith("grab") -> {
                val data = event.substringAfter(":")
                val parts = data.split(",")
                if (parts.size == 2) {
                    val id = parts[0]
                    val fromId = parts[1]

                    val hittable = Game.physics.hittables[id]
                    if (hittable is Grabbable)
                        hittable.onGrabbed(fromId)
                }
            }

            event.startsWith("release") -> {
                val data = event.substringAfter(":")
                val parts = data.split(",")

                if (parts.size == 3) {
                    val id = parts[0]
                    val impulseX = parts[1].toFloatOrNull()
                    val impulseY = parts[2].toFloatOrNull()

                    val hittable = Game.physics.hittables[id]
                    if (hittable is Grabbable && impulseX != null && impulseY != null)
                        hittable.onReleased(impulseX, impulseY)
                }
            }

            event.startsWith("stopParticleEffect") -> {
                val id = event.substringAfter(":")
                Game.particles.remove(id)
            }

            event.startsWith("setPrimaryWeapon") -> {
                val data = event.substringAfter(":")
                val parts = data.split(",")
                if (parts.size == 2) {
                    val id = parts[0]
                    val index = parts[1].toIntOrNull()
                    val player = Game.players.getByID(id)
                    if (player != null && index != null)
                        player.primaryWeapon = Weapons.entries.getOrNull(index)
                }
            }

            event.startsWith("setSecondaryWeapon") -> {
                val data = event.substringAfter(":")
                val parts = data.split(",")
                if (parts.size == 2) {
                    val id = parts[0]
                    val index = parts[1].toIntOrNull()
                    val player = Game.players.getByID(id)
                    if (player != null && index != null)
                        player.secondaryWeapon = Weapons.entries.getOrNull(index)
                }
            }

            event.startsWith("unlockWeapon") -> {
                val index = event.substringAfter(":").toIntOrNull()
                if (index != null)
                    Game.game.newlyUnlockedWeaponIndex = index
            }

            event.startsWith("startFight") -> {
                val index = event.substringAfter(":")
                Game.game.startFightIndex = index.toIntOrNull()
            }

            event.startsWith("retry") -> {
                val difficulty = Difficulty.entries.getOrNull(event.substringAfter(":").toIntOrNull() ?: -1)

                when (difficulty) {
                    Difficulty.EASY -> Game.game.afterFightAction = AfterFightAction.RETRY_EASY
                    Difficulty.NORMAL -> Game.game.afterFightAction = AfterFightAction.RETRY_NORMAL
                    Difficulty.HARD -> Game.game.afterFightAction = AfterFightAction.RETRY_HARD
                    else -> {}
                }
            }

            event.startsWith("returnToBay") -> {
                Game.game.afterFightAction = AfterFightAction.EXIT
            }

            event.startsWith("results") -> {
                val data = event.substringAfter(":")
                val parts = data.split(",")
                if (parts.size == 7) {
                    val duration = parts[0].toLongOrNull()?.milliseconds
                    val difficulty = Difficulty.entries.getOrNull(parts[1].toIntOrNull() ?: -1)
                    val bossFullHealth = parts[2].toIntOrNull()
                    val bossFinalHealth = parts[3].toIntOrNull()
                    val playerAverageFinalHealth = parts[4].toIntOrNull()
                    val shotsFired = parts[5].toIntOrNull()
                    val shotsHit = parts[6].toIntOrNull()

                    if (duration != null && difficulty != null && bossFullHealth != null && bossFinalHealth != null && playerAverageFinalHealth != null && shotsFired != null && shotsHit != null)
                        Game.game.currentFightResults = FightResults(duration, difficulty, bossFullHealth, bossFinalHealth, playerAverageFinalHealth, shotsFired, shotsHit)
                }
            }

            event.startsWith("unlockBoss") -> {
                val index = event.substringAfter(":").toIntOrNull()
                if (index != null)
                    Game.game.newlyUnlockedBossIndex = index
            }
        }
    }
}