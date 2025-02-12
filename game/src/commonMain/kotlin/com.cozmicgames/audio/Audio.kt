package com.cozmicgames.audio

import com.littlekt.Context
import com.littlekt.Releasable
import com.littlekt.async.KT
import com.littlekt.async.KtScope
import com.littlekt.audio.AudioClip
import com.littlekt.audio.AudioStream
import com.littlekt.file.vfs.readAudioClip
import com.littlekt.file.vfs.readAudioStream
import com.littlekt.math.clamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class Audio() : Releasable {
    companion object {
        private val FADE_TIME = 1.0.seconds
    }

    interface Sound : Releasable {
        val isLooping: Boolean

        fun play(volume: Float = 1.0f, looping: Boolean = false)
        fun stop()
    }

    interface Music : Releasable {
        var fadeVolume: Float

        fun play(volume: Float = 1.0f)

        fun stop()
    }

    inner class SingleSound(val clip: AudioClip) : Sound {
        override var isLooping = false

        override fun play(volume: Float, looping: Boolean) {
            try {
                if (isLooping)
                    return

                if (looping) {
                    isLooping = true
                    loopingSounds += this
                }

                clip.play(volume * soundVolume, looping)
            } catch (_: Exception) {
            }
        }

        override fun stop() {
            try {
                if (isLooping) {
                    isLooping = false
                    loopingSounds -= this
                }

                clip.stop()
            } catch (_: Exception) {
            }
        }

        override fun release() {
            stop()
            clip.release()
        }
    }

    inner class MultiSound(val clips: List<AudioClip>) : Sound {
        override var isLooping = false

        override fun play(volume: Float, looping: Boolean) {
            try {
                if (isLooping)
                    return

                if (looping) {
                    isLooping = true
                    loopingSounds += this
                }

                clips.random().play(volume * soundVolume, looping)
            } catch (_: Exception) {
            }
        }

        override fun stop() {
            clips.forEach {
                try {
                    if (isLooping) {
                        isLooping = false
                        loopingSounds -= this
                    }

                    it.stop()
                } catch (_: Exception) {
                }
            }
        }

        override fun release() {
            stop()
            clips.forEach { it.release() }
        }
    }

    inner class SingleMusic(val stream: AudioStream) : Music {
        private var startVolume = 0.0f

        override var fadeVolume = 1.0f
            set(value) {
                stream.volume = value * startVolume
                field = value
            }

        override fun play(volume: Float) {
            try {
                startVolume = volume * musicVolume

                KtScope.launch(Dispatchers.KT) {
                    stream.play(startVolume, true)
                }

                currentMusic = this
            } catch (_: Exception) {
            }
        }

        override fun stop() {
            try {
                stream.stop()
            } catch (_: Exception) {
            }
        }

        override fun release() {
            stop()
            stream.release()
        }
    }

    var soundVolume = 0.5f
    var musicVolume = 0.5f

    lateinit var themeMusic: Music
    lateinit var fightMusic: Music

    lateinit var hoverSound: Sound
    lateinit var clickSound: Sound
    lateinit var unlockSound: Sound
    lateinit var selectPrimarySound: Sound
    lateinit var selectSecondarySound: Sound
    lateinit var hitShipSound: Sound
    lateinit var hitEnemySound: Sound
    lateinit var baySound: Sound
    lateinit var shootSound: Sound
    lateinit var beamSound: Sound
    lateinit var screamSound: Sound
    lateinit var enginesSound: Sound
    lateinit var explosionSound: Sound
    lateinit var teleportSound: Sound

    private var loopingSounds = arrayListOf<Sound>()
    private var currentMusic: Music? = null
    private var nextMusic: Music? = null
    private var nextMusicVolume = 0.0f
    private var fadeTimer = 0.0.seconds
    private var isFadingIn = false

    suspend fun load(context: Context) {
        themeMusic = SingleMusic(context.resourcesVfs["audio/theme.wav"].readAudioStream())
        fightMusic = SingleMusic(context.resourcesVfs["audio/fight.wav"].readAudioStream())

        hoverSound = SingleSound(context.resourcesVfs["audio/hover.ogg"].readAudioClip())
        clickSound = SingleSound(context.resourcesVfs["audio/click.ogg"].readAudioClip())
        unlockSound = SingleSound(context.resourcesVfs["audio/unlock.ogg"].readAudioClip())
        selectPrimarySound = SingleSound(context.resourcesVfs["audio/select_primary.ogg"].readAudioClip())
        selectSecondarySound = SingleSound(context.resourcesVfs["audio/select_secondary.ogg"].readAudioClip())
        hitShipSound = SingleSound(context.resourcesVfs["audio/hit_ship.ogg"].readAudioClip())
        hitEnemySound = SingleSound(context.resourcesVfs["audio/hit_enemy.ogg"].readAudioClip())
        baySound = SingleSound(context.resourcesVfs["audio/bay.ogg"].readAudioClip())
        shootSound = SingleSound(context.resourcesVfs["audio/shoot.ogg"].readAudioClip())
        beamSound = SingleSound(context.resourcesVfs["audio/beam.ogg"].readAudioClip())
        screamSound = MultiSound((0..1).map { context.resourcesVfs["audio/scream$it.ogg"].readAudioClip() })
        enginesSound = SingleSound(context.resourcesVfs["audio/engines.ogg"].readAudioClip())
        explosionSound = SingleSound(context.resourcesVfs["audio/explosion.ogg"].readAudioClip())
        teleportSound = SingleSound(context.resourcesVfs["audio/teleport.ogg"].readAudioClip())
    }

    fun stopLoopingSounds() {
        val loopingSounds = arrayListOf<Sound>()
        loopingSounds += this.loopingSounds
        loopingSounds.forEach { it.stop() }
    }

    fun update(delta: Duration) {
        if (nextMusic != null) {
            currentMusic?.fadeVolume = 1.0f - (fadeTimer / FADE_TIME).toFloat().clamp(0.0f, 1.0f)

            if (currentMusic == null || currentMusic?.fadeVolume == 0.0f) {
                currentMusic?.stop()
                currentMusic = nextMusic
                currentMusic?.play(nextMusicVolume)
                isFadingIn = true
                fadeTimer = 0.0.seconds
                nextMusic = null
            }

            fadeTimer += delta
        }

        if (isFadingIn) {
            currentMusic?.fadeVolume = (fadeTimer / FADE_TIME).toFloat().clamp(0.0f, 1.0f)
            if (currentMusic?.fadeVolume == 1.0f || fadeTimer >= FADE_TIME)
                isFadingIn = false

            fadeTimer += delta
        }
    }

    fun fadeMusicTo(music: Music, volume: Float) {
        music.fadeVolume = 0.0f
        nextMusicVolume = volume
        nextMusic = music
        fadeTimer = 0.0.seconds
        isFadingIn = false
    }

    override fun release() {
        themeMusic.release()
        fightMusic.release()

        hoverSound.release()
        clickSound.release()
        unlockSound.release()
        selectPrimarySound.release()
        selectSecondarySound.release()
        hitShipSound.release()
        hitEnemySound.release()
        baySound.release()
        shootSound.release()
        beamSound.release()
        screamSound.release()
        enginesSound.release()
        explosionSound.release()
        teleportSound.release()
    }
}