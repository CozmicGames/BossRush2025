package com.cozmicgames.entities.worldObjects

import com.cozmicgames.Game
import com.cozmicgames.utils.Difficulty
import kotlin.time.Duration

class AsteroidManager(val difficulty: Difficulty, val maxAsteroids: Int) {
    private val asteroids = Array(maxAsteroids) { Asteroid(it) }
    private val activeAsteroids = BooleanArray(maxAsteroids)

    fun initialize() {
        repeat(maxAsteroids / 50) {
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

    fun update(delta: Duration, isFighting: Boolean) {
        activeAsteroids.forEachIndexed { index, isActive ->
            if (isActive) {
                val asteroid = asteroids[index]
                asteroid.update(delta, isFighting)

                if (asteroid.x > Game.physics.maxX + 500.0f) {
                    Game.world.remove(asteroid)
                    Game.physics.removeCollider(asteroid.collider)
                    activeAsteroids[index] = false
                }
            }
        }

        var activeAsteroidsCount = 0

        repeat(1000) {
            if (activeAsteroids[it])
                activeAsteroidsCount++
        }

        if (activeAsteroidsCount < 20)
            spawnAsteroid(difficulty)
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