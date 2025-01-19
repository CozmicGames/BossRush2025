package com.cozmicgames.bosses.boss2

import com.cozmicgames.Constants
import com.cozmicgames.Game
import com.cozmicgames.bosses.Boss
import com.cozmicgames.entities.worldObjects.PlayerShip
import com.cozmicgames.entities.worldObjects.ProjectileSource
import com.cozmicgames.entities.worldObjects.animations.HitAnimation
import com.cozmicgames.entities.worldObjects.animations.ParalyzeAnimation
import com.cozmicgames.entities.worldObjects.animations.WorldObjectAnimation
import com.cozmicgames.graphics.RenderLayers
import com.cozmicgames.physics.RectangleCollisionShape
import com.cozmicgames.utils.Difficulty
import com.littlekt.graphics.g2d.shape.ShapeRenderer
import com.littlekt.math.geom.cosine
import com.littlekt.math.geom.degrees
import com.littlekt.math.geom.sine
import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class Boss2(override val difficulty: Difficulty) : Boss, ProjectileSource {
    companion object {
        const val FULL_HEALTH = 4

        private val INVULNERABLE_TIME = 2.0.seconds
        private val PARALYZED_TIME = 5.0.seconds

        private const val HEAD_SCALE = 2.0f
        private const val SWORD_SCALE = 2.0f
        private const val SHIELD_SCALE = 4.0f
        private const val BODY_SCALE = 2.0f
        private const val FIN_SCALE = 2.0f
        private const val TAIL_SCALE = 2.0f

        private const val HEAD_LAYER = RenderLayers.ENEMY_BEGIN + 20
        private const val SWORD_LAYER = RenderLayers.ENEMY_BEGIN + 9
        private const val SHIELD_LAYER = RenderLayers.ENEMY_BEGIN + 7
        private const val HEART_LAYER = RenderLayers.ENEMY_BEGIN + 5
        private const val BODY_LAYER = RenderLayers.ENEMY_BEGIN + 10
        private const val DORSAL_FIN_LAYER = RenderLayers.ENEMY_BEGIN + 5
        private const val SIDE_FIN_LAYER = RenderLayers.ENEMY_BEGIN + 25
        private const val TAIL_LAYER = RenderLayers.ENEMY_BEGIN + 5

        private const val DORSAL_FIN_BODY_PART_INDEX = (Constants.BOSS2_BODY_PARTS * 0.3f).toInt()
        private const val SIDE_FIN_BODY_PART_INDEX = (Constants.BOSS2_BODY_PARTS * 0.15f).toInt()
        private const val TAIL_BODY_PART_INDEX = Constants.BOSS2_BODY_PARTS - 1
    }

    override val projectileSourceId = "boss2"
    override val muzzleX get() = sword.x //TODO: Fix this
    override val muzzleY get() = sword.y
    override val muzzleRotation get() = sword.rotation

    override var health = FULL_HEALTH
    override var x = 0.0f
    override var y = 0.0f
    override var rotation = 0.0.degrees

    val isInvulnerable get() = isInvulnerableTimer > 0.0.seconds

    override val isParalyzed get() = isParalyzedTimer > 0.0.seconds

    var isFlipped = false
        private set

    private val head = Head(this, HEAD_SCALE, HEAD_LAYER)
    private val sword = Sword(this, SWORD_SCALE, SWORD_LAYER)
    private val shield = Shield(this, SHIELD_SCALE, SHIELD_LAYER)
    private val heart = Heart(this, HEART_LAYER)
    private val body = Body(this, BODY_SCALE, BODY_LAYER)
    private val dorsalFin = Fin(this, false, FIN_SCALE, DORSAL_FIN_LAYER)
    private val sideFin = Fin(this, true, FIN_SCALE, SIDE_FIN_LAYER)
    private val tail = Tail(this, TAIL_SCALE, TAIL_LAYER)
    private var isInvulnerableTimer = 0.0.seconds
    private var isParalyzedTimer = 0.0.seconds

    override val movementController = Boss2MovementController(this)

    fun flip() {
        isFlipped = !isFlipped
    }

    fun getFilteredPlayerShips(): List<PlayerShip> {
        val players = Game.players.players
        val filteredPlayers = mutableListOf<PlayerShip>()
        for (player in players)
            if (!isFlipped && player.ship.x < x || isFlipped && player.ship.x > x)
                filteredPlayers += player.ship
        return filteredPlayers
    }

    override fun addToWorld() {
        Game.world.add(head)
        Game.world.add(sword)
        Game.world.add(shield)
        Game.world.add(heart)
        body.parts.forEach(Game.world::add)
        Game.world.add(dorsalFin)
        Game.world.add(sideFin)
        Game.world.add(tail)
    }

    override fun removeFromWorld() {
        Game.world.remove(head)
        Game.world.remove(sword)
        Game.world.remove(shield)
        Game.world.remove(heart)
        body.parts.forEach(Game.world::remove)
        Game.world.remove(dorsalFin)
        Game.world.remove(sideFin)
        Game.world.remove(tail)
    }

    override fun addToPhysics() {
        Game.physics.addCollider(head.collider)
        Game.physics.addHittable(head)

        Game.physics.addCollider(sword.collider)
        Game.physics.addCollider(shield.collider)

        Game.physics.addCollider(heart.collider)
        Game.physics.addHittable(heart)

        body.parts.forEach {
            it.collider?.apply(Game.physics::addCollider)
        }

        Game.physics.addHittable(body)

        Game.physics.addCollider(dorsalFin.collider)
        Game.physics.addCollider(sideFin.collider)
        Game.physics.addCollider(tail.collider)
    }

    override fun removeFromPhysics() {
        Game.physics.removeCollider(head.collider)
        Game.physics.removeHittable(head)

        Game.physics.removeCollider(sword.collider)
        Game.physics.removeCollider(shield.collider)

        Game.physics.removeCollider(heart.collider)
        Game.physics.removeHittable(heart)

        body.parts.forEach {
            it.collider?.apply(Game.physics::removeCollider)
        }

        Game.physics.removeHittable(body)

        Game.physics.removeCollider(dorsalFin.collider)
        Game.physics.removeCollider(sideFin.collider)
        Game.physics.removeCollider(tail.collider)
    }

    override fun update(delta: Duration) {
        if (Game.players.isHost) {
            isInvulnerableTimer -= delta
            if (isInvulnerableTimer <= 0.0.seconds)
                isInvulnerableTimer = 0.0.seconds

            isParalyzedTimer -= delta
            if (isParalyzedTimer <= 0.0.seconds)
                isParalyzedTimer = 0.0.seconds

            if (!isParalyzed && !movementController.isAttacking && getFilteredPlayerShips().size * 2 < Game.players.players.size)
                flip()

            body.x = x
            body.y = y
            body.rotation = rotation

            movementController.update(delta)
            body.update(delta, movementController.movement.bodyMovement)
            shield.update(delta, movementController.movement.shieldMovement)

            val cos = rotation.cosine
            val sin = rotation.sine

            val headOffsetX = if (isFlipped) head.width * 0.5f else -head.width * 0.5f

            head.x = x + cos * headOffsetX
            head.y = y + sin * headOffsetX
            head.rotation = rotation
            head.collider.update(head.x, head.y)

            val swordOffsetX = if (isFlipped) head.width * 0.78f + sword.width * 0.5f else -(head.width * 0.78f + sword.width * 0.5f)

            sword.x = x + cos * swordOffsetX
            sword.y = y + sin * swordOffsetX
            sword.rotation = rotation
            (sword.collider.shape as RectangleCollisionShape).angle = rotation
            sword.collider.update(sword.x, sword.y)

            val shieldOffsetX = if (isFlipped) head.width * 0.25f else -head.width * 0.25f
            val shieldOffsetY = -head.height * 0.35f

            shield.x = x + cos * shieldOffsetX - sin * shieldOffsetY
            shield.y = y + sin * shieldOffsetX + cos * shieldOffsetY
            shield.rotation = rotation
            shield.collider.update(shield.x, shield.y)

            val heartOffsetX = if (isFlipped) head.width * 0.25f else -head.width * 0.25f
            val heartOffsetY = -head.height * 0.35f

            heart.x = x + cos * heartOffsetX - sin * heartOffsetY
            heart.y = y + sin * heartOffsetX + cos * heartOffsetY
            heart.rotation = rotation

            val dorsalFinBodyPart = body.parts[DORSAL_FIN_BODY_PART_INDEX]
            val dorsalFinOffsetX = dorsalFinBodyPart.width * 0.1f
            val dorsalFinOffsetY = dorsalFinBodyPart.height * 0.4f
            val dorsalFinAngle = dorsalFinBodyPart.rotation

            val dorsalFinCos = dorsalFinAngle.cosine
            val dorsalFinSin = dorsalFinAngle.sine

            dorsalFin.x = dorsalFinBodyPart.x + dorsalFinCos * dorsalFinOffsetX - dorsalFinSin * dorsalFinOffsetY
            dorsalFin.y = dorsalFinBodyPart.y + dorsalFinSin * dorsalFinOffsetX + dorsalFinCos * dorsalFinOffsetY
            dorsalFin.rotation = dorsalFinAngle - (if (isFlipped) -(10.0).degrees else 10.0.degrees)

            val sideFinBodyPart = body.parts[SIDE_FIN_BODY_PART_INDEX]
            val sideFinOffsetX = sideFinBodyPart.width * -0.1f
            val sideFinOffsetY = -sideFinBodyPart.height * 0.4f
            val sideFinAngle = sideFinBodyPart.rotation

            val sideFinCos = sideFinAngle.cosine
            val sideFinSin = sideFinAngle.sine

            sideFin.x = sideFinBodyPart.x + sideFinCos * sideFinOffsetX - sideFinSin * sideFinOffsetY
            sideFin.y = sideFinBodyPart.y + sideFinSin * sideFinOffsetX + sideFinCos * sideFinOffsetY
            sideFin.rotation = sideFinAngle - (if (isFlipped) -(8.0).degrees else 8.0.degrees)

            val tailBodyPart = body.parts[TAIL_BODY_PART_INDEX]
            val tailOffsetX = if (isFlipped) tailBodyPart.width * 0.4f else -tailBodyPart.width * 0.4f
            val tailAngle = tailBodyPart.rotation

            val tailCos = tailAngle.cosine
            val tailSin = tailAngle.sine

            tail.x = tailBodyPart.x + tailCos * tailOffsetX
            tail.y = tailBodyPart.y + tailSin * tailOffsetX
            tail.rotation = tailAngle

            Game.players.setGlobalState("boss2x", x)
            Game.players.setGlobalState("boss2y", y)
            Game.players.setGlobalState("boss2rotation", rotation)

            Game.players.setGlobalState("boss2headX", head.x)
            Game.players.setGlobalState("boss2headY", head.y)
            Game.players.setGlobalState("boss2headRotation", head.rotation)

            Game.players.setGlobalState("boss2swordX", sword.x)
            Game.players.setGlobalState("boss2swordY", sword.y)
            Game.players.setGlobalState("boss2swordRotation", sword.rotation)

            Game.players.setGlobalState("boss2shieldX", shield.x)
            Game.players.setGlobalState("boss2shieldY", shield.y)
            Game.players.setGlobalState("boss2shieldRotation", shield.rotation)

            Game.players.setGlobalState("boss2heartX", heart.x)
            Game.players.setGlobalState("boss2heartY", heart.y)
            Game.players.setGlobalState("boss2heartRotation", heart.rotation)

            Game.players.setGlobalState("boss2dorsalfinX", dorsalFin.x)
            Game.players.setGlobalState("boss2dorsalfinY", dorsalFin.y)
            Game.players.setGlobalState("boss2dorsalfinRotation", dorsalFin.rotation)

            Game.players.setGlobalState("boss2sidefinX", sideFin.x)
            Game.players.setGlobalState("boss2sidefinY", sideFin.y)
            Game.players.setGlobalState("boss2sidefinRotation", sideFin.rotation)

            Game.players.setGlobalState("boss2tailX", tail.x)
            Game.players.setGlobalState("boss2tailY", tail.y)
            Game.players.setGlobalState("boss2tailRotation", tail.rotation)
        } else {
            x = Game.players.getGlobalState("boss2x") ?: 0.0f
            y = Game.players.getGlobalState("boss2y") ?: 0.0f
            rotation = Game.players.getGlobalState("boss2rotation") ?: 0.0.degrees

            head.x = Game.players.getGlobalState("boss2headX") ?: 0.0f
            head.y = Game.players.getGlobalState("boss2headY") ?: 0.0f
            head.rotation = Game.players.getGlobalState("boss2headRotation") ?: 0.0.degrees

            sword.x = Game.players.getGlobalState("boss2swordX") ?: 0.0f
            sword.y = Game.players.getGlobalState("boss2swordY") ?: 0.0f
            sword.rotation = Game.players.getGlobalState("boss2swordRotation") ?: 0.0.degrees

            shield.x = Game.players.getGlobalState("boss2shieldX") ?: 0.0f
            shield.y = Game.players.getGlobalState("boss2shieldY") ?: 0.0f
            shield.rotation = Game.players.getGlobalState("boss2shieldRotation") ?: 0.0.degrees

            heart.x = Game.players.getGlobalState("boss2heartX") ?: 0.0f
            heart.y = Game.players.getGlobalState("boss2heartY") ?: 0.0f
            heart.rotation = Game.players.getGlobalState("boss2heartRotation") ?: 0.0.degrees

            dorsalFin.x = Game.players.getGlobalState("boss2dorsalfinX") ?: 0.0f
            dorsalFin.y = Game.players.getGlobalState("boss2dorsalfinY") ?: 0.0f
            dorsalFin.rotation = Game.players.getGlobalState("boss2dorsalfinRotation") ?: 0.0.degrees

            sideFin.x = Game.players.getGlobalState("boss2sidefinX") ?: 0.0f
            sideFin.y = Game.players.getGlobalState("boss2sidefinY") ?: 0.0f
            sideFin.rotation = Game.players.getGlobalState("boss2sidefinRotation") ?: 0.0.degrees

            tail.x = Game.players.getGlobalState("boss2tailX") ?: 0.0f
            tail.y = Game.players.getGlobalState("boss2tailY") ?: 0.0f
            tail.rotation = Game.players.getGlobalState("boss2tailRotation") ?: 0.0.degrees
        }
    }

    fun paralyze() {
        if (isInvulnerable)
            return

        addEntityAnimation { ParalyzeAnimation(PARALYZED_TIME, 0.7f) }

        if (Game.players.isHost) {
            movementController.onParalyze()
            isParalyzedTimer = PARALYZED_TIME
            shield.intensity = 0.0f
        }
    }

    fun hit() {
        if (isInvulnerable)
            return

        cancelEntityAnimation<ParalyzeAnimation>()
        addEntityAnimation { HitAnimation(INVULNERABLE_TIME) }

        if (Game.players.isHost) {
            health--
            movementController.onHit()

            if (health <= 0) {
                //TODO: Handle boss death
            } else {
                isInvulnerableTimer = INVULNERABLE_TIME
                isParalyzedTimer = 0.0.seconds
            }
        }
    }

    fun addEntityAnimation(block: () -> WorldObjectAnimation) {
        head.addEntityAnimation(block())
        sword.addEntityAnimation(block())
        shield.addEntityAnimation(block())
        body.parts.forEach { it.addEntityAnimation(block()) }
        dorsalFin.addEntityAnimation(block())
        sideFin.addEntityAnimation(block())
        tail.addEntityAnimation(block())
    }

    inline fun <reified T : WorldObjectAnimation> cancelEntityAnimation() {
        cancelEntityAnimation(T::class)
    }

    fun <T : WorldObjectAnimation> cancelEntityAnimation(type: KClass<T>) {
        head.cancelEntityAnimation(type)
        sword.cancelEntityAnimation(type)
        shield.cancelEntityAnimation(type)
        body.parts.forEach { it.cancelEntityAnimation(type) }
        dorsalFin.cancelEntityAnimation(type)
        sideFin.cancelEntityAnimation(type)
        tail.cancelEntityAnimation(type)
    }

    override fun drawDebug(renderer: ShapeRenderer) {
        head.collider.drawDebug(renderer)
        sword.collider.drawDebug(renderer)
        shield.collider.drawDebug(renderer)
        heart.collider.drawDebug(renderer)
        body.parts.forEach { it.collider?.drawDebug(renderer) }
        dorsalFin.collider.drawDebug(renderer)
        tail.collider.drawDebug(renderer)
    }
}