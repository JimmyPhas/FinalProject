package com.example.finalproject

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.util.Log

class MyService : Service() {
    private var myMediaPlayer : MediaPlayer? = null
    private val Media_Player = "MediaPlayer"
    private var track = false
    private val currentPlaylist = mutableListOf<Song>()
    private var totalTime: Int = 0
    private var shufflestate: Boolean = false
    private var loopstate: Boolean = false
    private var trackIndex: Int = 0
    private var nowPlaying: Int = 0
//    private var pos: Int = 0

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val sharedPreferences = getSharedPreferences(Media_Player, Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        val gson = Gson()

        val savedPlaylist = sharedPreferences?.getString("LastPlaylist", "")?:""
        if (savedPlaylist.isNotEmpty()) {
            val sType = object : TypeToken<List<Song>>() {}.type
            val savedPlaylist = gson.fromJson<List<Song>>(savedPlaylist, sType)

            for (S in savedPlaylist) {
                currentPlaylist.add(S)
            }
        }

        trackIndex = intent?.getIntExtra("song", 0)!!
        loopstate = intent?.getBooleanExtra("loop", false)
        shufflestate = intent?.getBooleanExtra("shuffle", false)
        val songTime = intent?.getIntExtra("time", 0)
        myMediaPlayer = MediaPlayer.create(this, currentPlaylist[trackIndex].uriValue)
        if (songTime != null) {
            myMediaPlayer?.seekTo(songTime)
        }
        playMusic()

        Thread {
            if (myMediaPlayer != null) {
                track = true
                while (track == true) {
                    val pos = myMediaPlayer!!.getCurrentPosition()
                    if (editor != null) {
                        editor.putString("LastTime", pos.toString())
                    }
                    if (editor != null) {
                        editor.apply()
                    }
                    if (pos == totalTime ) {
                        // checks loopstate if false stop playing and reset ui
                        if (loopstate == false) {
                            myMediaPlayer!!.release()
                            stopSelf()
                            if (editor != null) {
                                editor.putString("LastTime", 0.toString())
                            }
                            if (editor != null) {
                                editor.apply()
                            }
                        } else {
                            // if loop true play next song
                            playNextSong()
                        }
                    }
                    Thread.sleep(500)
                }
            }
            else {
                if (loopstate == false) {
                    stopSelf()
                }
            }
        }.start()


        return START_STICKY
    }

    // when the song is over automatically play the next song
    fun playNextSong() {
        val sharedPreferences = getSharedPreferences(Media_Player, Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        // checks shuffle if false play song in playlist in order
        if (shufflestate == false) {
            myMediaPlayer?.pause()
            trackIndex = (trackIndex + 1) % currentPlaylist.size
            nowPlaying = currentPlaylist[trackIndex].uriValue
            myMediaPlayer = MediaPlayer.create(this, nowPlaying)
            myMediaPlayer?.seekTo(0)
        }
        else {
            // if true play random song in playlist
            myMediaPlayer?.pause()
            trackIndex = (0 until currentPlaylist.size).random()
            nowPlaying = currentPlaylist[trackIndex].uriValue
            myMediaPlayer = MediaPlayer.create(this, nowPlaying)
            myMediaPlayer?.seekTo(0)
        }
        if (editor != null) {
            editor.putString("LastSong", trackIndex.toString())
        }
        if (editor != null) {
            editor.apply()
        }
        playMusic()
    }

    fun playMusic() {
        myMediaPlayer?.start()
        totalTime = myMediaPlayer?.duration ?: 0
    }

    override fun onDestroy() {
        super.onDestroy()
        if (myMediaPlayer != null) {
            track = false
            myMediaPlayer?.release()
        }
    }

    override fun onBind(intent: Intent): IBinder? {

        return null
    }
}
