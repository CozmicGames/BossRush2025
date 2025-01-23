package com.cozmicgames.entities.worldObjects

import com.cozmicgames.Game
import com.cozmicgames.utils.Difficulty
import com.littlekt.math.geom.degrees
import kotlin.time.Duration

class AsteroidManager(val difficulty: Difficulty) {
    private val asteroids = Array(1000) { AsteroidWorldObject(it) }
    private val activeAsteroids = BooleanArray(1000)

    fun initialize() {
        if (Game.players.isHost) {
            repeat(30) {
                activeAsteroids[it] = true
                val asteroid = asteroids[it]

                val x = Game.physics.minX + Game.random.nextFloat() * (Game.physics.maxX - Game.physics.minX)
                val y = Game.physics.minY + Game.random.nextFloat() * (Game.physics.maxY - Game.physics.minY)

                asteroid.reset(x, y)

                Game.world.add(asteroid)

                if (difficulty != Difficulty.EASY)
                    Game.physics.addCollider(asteroid.collider)
            }
        }
    }

    fun update(delta: Duration, fightStarted: Boolean) {
        if (Game.players.isHost) {
            activeAsteroids.forEachIndexed { index, isActive ->
                if (isActive) {
                    val asteroid = asteroids[index]
                    asteroid.update(delta, fightStarted)

                    if (asteroid.x > Game.physics.maxX + 500.0f) {
                        Game.world.remove(asteroid)
                        Game.physics.removeCollider(asteroid.collider)
                        activeAsteroids[index] = false
                    }
                }
            }

            var activeAsteroidsCount = 0

            repeat(1000) {
                if (activeAsteroids[it]) {
                    val asteroid = asteroids[it]
                    Game.players.setGlobalState("asteroid${activeAsteroidsCount}x", asteroid.x)
                    Game.players.setGlobalState("asteroid${activeAsteroidsCount}y", asteroid.y)
                    Game.players.setGlobalState("asteroid${activeAsteroidsCount}rotation", asteroid.rotation.degrees)
                    Game.players.setGlobalState("asteroid${activeAsteroidsCount}size", asteroid.size)
                    activeAsteroidsCount++
                }
            }

            Game.players.setGlobalState("asteroidCount", activeAsteroidsCount)

            if (activeAsteroidsCount < 20)
                spawnAsteroid(difficulty)
        } else {
            asteroids.forEach {
                Game.world.remove(it)
            }

            val activeAsteroids = Game.players.getGlobalState("asteroidCount") ?: 0

            repeat(activeAsteroids) {
                val asteroid = asteroids[it]
                asteroid.x = Game.players.getGlobalState("asteroid${it}x") ?: 0.0f
                asteroid.y = Game.players.getGlobalState("asteroid${it}y") ?: 0.0f
                asteroid.rotation = (Game.players.getGlobalState("asteroid${it}rotation") ?: 0.0f).degrees
                asteroid.size = Game.players.getGlobalState("asteroid${it}size") ?: 0.0f
                Game.world.add(asteroid)
            }
        }
    }

    fun spawnAsteroid(difficulty: Difficulty) {
        var freeIndex = -1

        for (i in activeAsteroids.indices) {
            if (!activeAsteroids[i]) {
                freeIndex = i
                break
            }
        }

        if (freeIndex == -1)
            return

        activeAsteroids[freeIndex] = true
        val asteroid = asteroids[freeIndex]

        val x = Game.physics.minX - 500.0f
        val y = Game.physics.minY + Game.random.nextFloat() * (Game.physics.maxY - Game.physics.minY)

        asteroid.reset(x, y)

        Game.world.add(asteroid)

        if (difficulty != Difficulty.EASY)
            Game.physics.addCollider(asteroid.collider)
    }
}