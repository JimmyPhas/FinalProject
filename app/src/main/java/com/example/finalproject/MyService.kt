package com.example.finalproject

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MyService : Service() {
    private var myMediaPlayer : MediaPlayer? = null
    private val Media_Player = "MediaPlayer"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val sharedPreferences = getSharedPreferences(Media_Player, Context.MODE_PRIVATE)
        val gson = Gson()

        val currentPlaylist = sharedPreferences?.getString("LastPlaylist", "")?:""
        var lastPlaylist: List<Song>
        if (currentPlaylist.isNotEmpty()) {
            val sType = object : TypeToken<List<Song>>() {}.type
            val savedPlaylist = gson.fromJson<List<Song>>(currentPlaylist, sType)
        }

        val songID = intent?.getIntExtra("song", 0)
        myMediaPlayer = songID?.let { MediaPlayer.create(this, it) }
        myMediaPlayer?.start()


        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        val sharedPreferences = getSharedPreferences(Media_Player, Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        val gson = Gson()

        val pos = myMediaPlayer!!.getCurrentPosition()/1000
        // saves the current timestamp
        if (editor != null) {
            editor.putString("LastTime", pos.toString())
        }
        if (editor != null) {
            editor.apply()
        }

        myMediaPlayer?.stop()
    }

    override fun onBind(intent: Intent): IBinder? {

        return null
    }
}
