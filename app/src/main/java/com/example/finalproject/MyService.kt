package com.example.finalproject

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder

class MyService : Service() {
    private var myMediaPlayer : MediaPlayer? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val songID = intent?.getIntExtra("song", 0)
        val currentPlaylist = intent?.getStringExtra("playlist")
        myMediaPlayer = songID?.let { MediaPlayer.create(this, it) }
        myMediaPlayer?.start()


        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        myMediaPlayer?.stop()
    }

    override fun onBind(intent: Intent): IBinder? {

        return null
    }
}
