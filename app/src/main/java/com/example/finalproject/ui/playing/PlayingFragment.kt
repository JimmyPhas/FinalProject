package com.example.finalproject.ui.playing

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.telephony.ServiceState
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.room.Room
import com.example.finalproject.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_playing.*
import kotlinx.android.synthetic.main.fragment_playing.view.*
import java.lang.Exception


class PlayingFragment : Fragment() {

    private var myMediaPlayer : MediaPlayer? = null
    private var totalTime: Int = 0
    private var playstate: Boolean = false
    private var loopstate: Boolean = false
    private var shufflestate: Boolean = false
    private val myHandler = Handler()
    private var track: Boolean = false
    private var nowPlaying: Int = 0
    private var lastPlaylist = mutableListOf<Song>()
    private var trackIndex: Int = 0
    private var prevSongs = mutableListOf<Int>()
    private var stackCurser: Int = 0
    private val Media_Player = "MediaPlayer"
    private lateinit var fragView: View


    private lateinit var playingViewModel: PlayingViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val myIntent = Intent(this.context, MyService::class.java)
        getActivity()?.stopService(myIntent)
    }


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        playingViewModel =
                ViewModelProviders.of(this).get(PlayingViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_playing, container, false)
        fragView = root

        getSharedPrefs()

        // listener of the play button
        root.playbutton.setOnClickListener {
            // checks if we are currently playing a song
            if (playstate == false) {
                // sets the UI values
                playbutton.setImageResource(R.drawable.ic_pause_black_24dp)
                playMusic()

            }
            else {
                // if playing a song, pause it
                playbutton.setImageResource(R.drawable.ic_play_arrow_black_24dp)
                myMediaPlayer?.pause()
                // tells program to stop updating seekbar
                track = false
                playstate = false
            }
        }

        // updates the seek bar every second
        this.activity?.runOnUiThread(object : Runnable {
            override fun run() {
                // if track is true update the seekbar otherwise dont (this is important or else app crashes when changing off of the fragment)
                if (track == true) {
                    // if end of song go to next one or stop playing automatically
                    if ((myMediaPlayer!!.getCurrentPosition()/ 1000) == (totalTime / 1000)) {
                        // checks loopstate if false stop playing and reset ui
                        if (loopstate == false) {
                            playbutton.setImageResource(R.drawable.ic_play_arrow_black_24dp)
                            playstate = false
                            track = false
                            myMediaPlayer!!.release()
                            playbar.setProgress(0)
                        }
                        else {
                            // if loop true play next song
                            playNextSong()
                        }
                    }
                    val mCurrentPosition: Int = myMediaPlayer!!.getCurrentPosition() / 1000
                    playbar.setProgress(mCurrentPosition)
                    view?.playTime?.text = createTimeLabel(mCurrentPosition * 1000)
                }

                // waits a second
                myHandler.postDelayed(this, 1000)
            }
        })

        // listens for user change on the seek bar and changes the mediaplayer to where the user clicked
        root.playbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    myMediaPlayer?.seekTo(progress * 1000)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        // goes to the next song in the playlist
        root.nextbutton.setOnClickListener {
            // pushes current song to a stack
            songStack("push", trackIndex)
            // checks if shuffle is on, if not play next song in array
            if (shufflestate == false) {
                myMediaPlayer?.release()
                trackIndex = (trackIndex + 1) % lastPlaylist.size
                nowPlaying = lastPlaylist[trackIndex].uriValue
                myMediaPlayer = MediaPlayer.create(this.context, nowPlaying)
            }
            // if shuffle on select a random song in the array
            else {
                myMediaPlayer?.release()
                trackIndex = (0 until lastPlaylist.size).random()
                nowPlaying = lastPlaylist[trackIndex].uriValue
                myMediaPlayer = MediaPlayer.create(this.context, nowPlaying)
            }
            playMusic()
        }

        // plays the previous song using the stack, if nothing in the stack plays a random song or previous song in array
        root.prevbutton.setOnClickListener {
            // if stack is empty
            if (stackCurser == 0) {
                // if shuffle is off play the index of song before the current song in the playlist
                if (shufflestate == false) {
                    myMediaPlayer?.release()
                    trackIndex = (trackIndex - 1) % lastPlaylist.size
                    nowPlaying = lastPlaylist[trackIndex].uriValue
                    myMediaPlayer = MediaPlayer.create(this.context, nowPlaying)
                } else {
                    // if shuffle on play random song
                    myMediaPlayer?.release()
                    trackIndex = (0 until lastPlaylist.size).random()
                    nowPlaying = lastPlaylist[trackIndex].uriValue
                    myMediaPlayer = MediaPlayer.create(this.context, nowPlaying)
                }
            }
            else {
                // if stack is not empty pop the previous song played
                myMediaPlayer?.release()
                songStack("pop", trackIndex)
                nowPlaying = lastPlaylist[trackIndex].uriValue
                myMediaPlayer = MediaPlayer.create(this.context, nowPlaying)
            }
            playMusic()
        }

        // toggles if user wants to repeat the playlist or just play one song and stop
        root.repeatbutton.setOnClickListener {
            if (loopstate == false) {
                loopstate = true
                myMediaPlayer?.isLooping = true
                repeatbutton.setImageResource(R.drawable.ic_repeat_black_24dp)
            }
            else {
                loopstate = false
                myMediaPlayer?.isLooping = false
                repeatbutton.setImageResource(R.drawable.ic_repeat_one_black_24dp)
            }
        }

        // toggles if user wants to play in random order or in order
        root.shufflebutton.setOnClickListener {
            if (shufflestate == false) {
                shufflestate = true
                shufflebutton.setImageResource(R.drawable.ic_shuffle_black_24dp)
            }
            else {
                shufflestate = false
                shufflebutton.setImageResource(R.drawable.ic_trending_flat_black_24dp)
            }
        }

        return root
    }

    // when the song is over automatically play the next song
    fun playNextSong() {
        // push current song to stack
        songStack("push", trackIndex)
        // checks shuffle if false play song in playlist in order
        if (shufflestate == false) {
            myMediaPlayer?.release()
            trackIndex = (trackIndex + 1) % lastPlaylist.size
            nowPlaying = lastPlaylist[trackIndex].uriValue
            myMediaPlayer = MediaPlayer.create(this.context, nowPlaying)
        }
        else {
            // if true play random song in playlist
            myMediaPlayer?.release()
            trackIndex = (0 until lastPlaylist.size).random()
            nowPlaying = lastPlaylist[trackIndex].uriValue
            myMediaPlayer = MediaPlayer.create(this.context, nowPlaying)
        }
        playMusic()
    }

    // used when user clicks a song from the song fragment
    override fun onStart() {
        super.onStart()
        val myIntent = Intent(this.context, MyService::class.java)
        val sharedPreferences = this.activity?.getSharedPreferences(Media_Player, Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        val gson = Gson()
        var playmusic = false

        // if the user click on an item from the song fragment retrieve the index and play
        val fromSong = sharedPreferences?.getString("SongClick", "")?:""
        val fromPlaylist = sharedPreferences?.getString("PlaylistClick", "")?:""
        val servicestate = sharedPreferences?.getString("serviceState", "")?:""

        if (fromSong.isNotEmpty()) {
            val sType = object : TypeToken<String>() {}.type
            val fromsong = gson.fromJson<String>(fromSong, sType)
            // plays song automatically when fragment switches from song to playing
            if (fromsong == "true") {
                playbutton.setImageResource(R.drawable.ic_pause_black_24dp)
                if (editor != null) {
                    editor.putString("SongClick", "false")
                }
                if (editor != null) {
                    editor.apply()
                }
                playmusic = true
            }
        }
        if (fromPlaylist.isNotEmpty()) {
            val sType = object : TypeToken<String>() {}.type
            val fromplaylist = gson.fromJson<String>(fromPlaylist, sType)
            // plays song automatically when fragment switches from song to playing
            if (fromplaylist == "true") {
                playbutton.setImageResource(R.drawable.ic_pause_black_24dp)
                if (editor != null) {
                    editor.putString("PlaylistClick", "false")
                }
                if (editor != null) {
                    editor.apply()
                }
                playmusic = true
            }
        }

        if (servicestate.isNotEmpty()) {
            val sType = object : TypeToken<String>() {}.type
            val state = gson.fromJson<String>(servicestate, sType)

            if (state == "true") {
                getActivity()?.stopService(myIntent)
                if (editor != null) {
                    editor.putString("serviceState", "false")
                }
                if (editor != null) {
                    editor.apply()
                }
                getSharedPrefs()
                playmusic = true
            }
        }

        if (playmusic == true) {
            playMusic()
        }

    }

    // pushed and pops songs from the stack to use for previous button
    fun songStack(func: String, track: Int) {
        if (func == "pop") {
            stackCurser--
            if (stackCurser != 0) {
                trackIndex = prevSongs[stackCurser]
                stackCurser--
            }
        }
        else if (func == "push") {
            prevSongs.add(track)
            stackCurser++
        }
    }

    override fun onPause() {
        super.onPause()
        // stops tracking for seekbar
        track = false

        // saves the state of the player before closing
        if (myMediaPlayer != null ) {
            val sharedPreferences = this.activity?.getSharedPreferences(Media_Player, Context.MODE_PRIVATE)
            val gson = Gson()

            val saveLastPlaylist = gson.toJson(lastPlaylist)
            val editor = sharedPreferences?.edit()
            val pos = myMediaPlayer!!.getCurrentPosition()

            if (editor != null) {
                editor.putString("Shuffle", shufflestate.toString())
            }
            if (editor != null) {
                editor.putString("Loop", loopstate.toString())
            }
            if (editor != null) {
                editor.putString("LastSong", trackIndex.toString())
            }
            if (editor != null) {
                editor.putString("LastTime", pos.toString())
            }
            if (editor != null) {
                editor.putString("LastPlaylist", saveLastPlaylist)
            }
            if (editor != null) {
                editor.apply()
            }

            playbutton.setImageResource(R.drawable.ic_play_arrow_black_24dp)
            if (playstate == true) {
                startBackgroundService(pos)
            }
            else {
                playstate = false
                myMediaPlayer?.release()
            }
        }
    }

    // formats the text for the time
    fun createTimeLabel(time: Int): String {
        var timeLabel = ""
        var min = time / 1000 / 60
        var sec = time / 1000 % 60
        var hr = time / 1000 / 60 / 60

        // if it is over an hour
        if (min >59) {
            hr = min / 60
            min = min % 60
            timeLabel = "$hr:"
            if (min < 10) {
                timeLabel += "0"
                timeLabel += min
            }
            else {
                timeLabel += min
            }


            if (sec < 10) {
                timeLabel += ":0"
                timeLabel += sec
            }
            else {
                timeLabel += ":"
                timeLabel += sec
            }
        }
        // if it is less than an hour
        else {
            timeLabel = "$min:"
            if (sec < 10) {
                timeLabel += "0"
                timeLabel += sec
            }
            else {
                timeLabel += sec
            }
        }

        return timeLabel
    }

    fun startBackgroundService(time: Int) {
        val myIntent = Intent(this.context, MyService::class.java)
        val sharedPreferences = this.activity?.getSharedPreferences(Media_Player, Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()

        if (editor != null) {
            editor.putString("serviceState", "true")
        }
        if (editor != null) {
            editor.apply()
        }

        myMediaPlayer?.release()
        myIntent.putExtra("song", trackIndex)
        myIntent.putExtra("time", time)
        myIntent.putExtra("loop", loopstate)
        myIntent.putExtra("shuffle", shufflestate)

        getActivity()?.startService(myIntent)
    }

    fun getSharedPrefs() {
        // uses shared preferences to keep track of previous app settings
        val sharedPreferences = this.activity?.getSharedPreferences(Media_Player, Context.MODE_PRIVATE)
        val gson = Gson()

        val currentPlaylist = sharedPreferences?.getString("LastPlaylist", "")?:""
        val allSongs = sharedPreferences?.getString("AllSongs", "")?:""
        val loop = sharedPreferences?.getString("Loop", "")?:""
        val shuffle = sharedPreferences?.getString("Shuffle", "")?:""
        val currentSong = sharedPreferences?.getString("LastSong", "")?:""
        val currentTime = sharedPreferences?.getString("LastTime", "")?:""

        // gets the last playlist that was played in the app
        if (currentPlaylist.isNotEmpty()) {
            val sType = object : TypeToken<List<Song>>() {}.type
            val savedPlaylist = gson.fromJson<List<Song>>(currentPlaylist, sType)

            for (S in savedPlaylist) {
                lastPlaylist.add(S)
            }

        }

        // if last playlist is empty play all songs in app
        if (lastPlaylist.isNotEmpty()) {
            // if last playlist not empty get the last song played
            if (currentSong.isNotEmpty()) {
                val sType = object : TypeToken<Int>() {}.type
                // gets the index of the song in the playlist
                trackIndex = gson.fromJson(currentSong, sType)
                // sets the uri value to now playing
                nowPlaying = lastPlaylist[trackIndex].uriValue
            }
            else {
                nowPlaying = lastPlaylist[trackIndex].uriValue
            }
            // sets the mediaplayer to the song
            myMediaPlayer = MediaPlayer.create(this.context, nowPlaying)
            fragView.songname?.text = lastPlaylist[trackIndex].songName + " - " + lastPlaylist[trackIndex].artistName
        }
        else {
            val sType = object : TypeToken<List<Song>>() {}.type
            val savedPlaylist = gson.fromJson<List<Song>>(allSongs, sType)

            for (S in savedPlaylist) {
                lastPlaylist.add(S)
            }

            trackIndex = 0
            nowPlaying = lastPlaylist[trackIndex].uriValue
            myMediaPlayer = MediaPlayer.create(this.context, nowPlaying)
            fragView.songname?.text = lastPlaylist[trackIndex].songName + " - " + lastPlaylist[trackIndex].artistName
        }

        // gets the time that the last play session stopped
        if (currentTime.isNotEmpty()) {
            val sType = object : TypeToken<Int>() {}.type
            val lastTime = gson.fromJson<Int>(currentTime, sType)

            myMediaPlayer?.seekTo(lastTime)
        }

        // sets the loop value from last session
        if (loop.isNotEmpty()) {
            val sType = object : TypeToken<Boolean>() {}.type
            val saveLoop = gson.fromJson<Boolean>(loop, sType)

            // if false will loop the full playlist
            loopstate = saveLoop
            if (loopstate == true) {
                fragView.repeatbutton.setImageResource(R.drawable.ic_repeat_black_24dp)
            }
            else {
                // else stops after song is done
                fragView.repeatbutton.setImageResource(R.drawable.ic_repeat_one_black_24dp)
            }
        }

        // sets the shuffle value from last session
        if (shuffle.isNotEmpty()) {
            val sType = object : TypeToken<Boolean>() {}.type
            val saveShuffle = gson.fromJson<Boolean>(shuffle, sType)

            // if true plays random song in playlist
            shufflestate = saveShuffle
            if (shufflestate == true) {
                fragView.shufflebutton.setImageResource(R.drawable.ic_shuffle_black_24dp)
            }
            else {
                // if false plays the next song in order
                fragView.shufflebutton.setImageResource(R.drawable.ic_trending_flat_black_24dp)
            }
        }

    }

    // plays the music
    fun playMusic() {
        myMediaPlayer?.start()
        // sets the seekbar and changes the time based on what media is playing
        totalTime = myMediaPlayer?.duration ?: 0
        view?.totalTime?.text  = createTimeLabel(totalTime)
        view?.playbar?.max = totalTime/1000
        view?.playbar?.progress = 0
        // tells the seekbar to change with the music
        track = true
        // tells the app it is currently playing a song
        playstate = true
        // displays current song title
        view?.songname?.text = lastPlaylist[trackIndex].songName + " - " + lastPlaylist[trackIndex].artistName
    }

}


