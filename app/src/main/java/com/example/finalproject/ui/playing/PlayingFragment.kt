package com.example.finalproject.ui.playing

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.finalproject.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_playing.*
import kotlinx.android.synthetic.main.fragment_playing.view.*
import java.lang.Exception


class PlayingFragment : Fragment() {

//    private lateinit var mp: MediaPlayer
    private var myMediaPlayer : MediaPlayer? = null
    private var totalTime: Int = 0
    private var playstate: Boolean = false
    private var loopstate: Boolean = false
    private var shufflestate: Boolean = false
    private val myHandler = Handler()
    private var track: Boolean = false
    private var nowPlaying: Int = 0
    private var lastPlaylist = mutableListOf<Song>()
//    private var allSongTitles = mutableListOf<Song>()
    private var trackIndex: Int = 0
    private var prevSongs = mutableListOf<Int>()
    private var stackCurser: Int = 0
    private val lastSession = mutableListOf<LastSession>()
    internal var dbHelper = this.context?.let { SongDBHelper(it) }

    private val Media_Player = "MediaPlayer"


    private lateinit var playingViewModel: PlayingViewModel


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        playingViewModel =
                ViewModelProviders.of(this).get(PlayingViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_playing, container, false)

        // remembers lest values of last session
        val sharedPreferences = this.activity?.getSharedPreferences(Media_Player, Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        val currentPlaylist = sharedPreferences?.getString("LastPlaylist", "")?:""
        val currentSong = sharedPreferences?.getString("LastSong", "")?:""
        val currentTime = sharedPreferences?.getString("LastTime", "")?:""
//        val allSongs = sharedPreferences?.getString("AllSongs", "")?:""
        val loop = sharedPreferences?.getString("Loop", "")?:""
        val shuffle = sharedPreferences?.getString("Shuffle", "")?:""
        val gson = Gson()

        val myIntent = Intent(getActivity(), MyService::class.java)

        try {
            val cursor1 = dbHelper?.viewLastSession
            if (cursor1 != null) {
                while (cursor1.moveToNext()) {
                    lastSession.add(
                        LastSession(
                            cursor1.getInt(1),
                            cursor1.getInt(2),
                            cursor1.getString(3),
                            cursor1.getInt(4),
                            cursor1.getInt(5),
                            cursor1.getInt(6)
                        )
                    )
                }
            }
        } catch (e: Exception){
            Log.e("SONGRECYCLER", "error: $e")
        }

        // sets the settings and last session values
        if (loop.isNotEmpty()) {
            val sType = object : TypeToken<Boolean>() {}.type
            val saveLoop = gson.fromJson<Boolean>(loop, sType)

            loopstate = saveLoop
            if (loopstate == true) {
                root.repeatbutton.setImageResource(R.drawable.ic_repeat_black_24dp)
            }
            else {
                root.repeatbutton.setImageResource(R.drawable.ic_repeat_one_black_24dp)
            }
        }

        if (shuffle.isNotEmpty()) {
            val sType = object : TypeToken<Boolean>() {}.type
            val saveShuffle = gson.fromJson<Boolean>(shuffle, sType)

            shufflestate = saveShuffle
            if (shufflestate == true) {
                root.shufflebutton.setImageResource(R.drawable.ic_shuffle_black_24dp)
            }
            else {
                root.shufflebutton.setImageResource(R.drawable.ic_trending_flat_black_24dp)
            }
        }

        // array of all songs, used to keep playing or select next or previous song
//        if (allSongs.isNotEmpty()) {
//            val sType = object : TypeToken<List<Song>>() {}.type
//            val savedSongList = gson.fromJson<List<Song>>(allSongs, sType)
//
//            for (S in savedSongList) {
//                allSongTitles.add(S)
//            }
//        }

        // this array holds the resId values so that i can properly set myMusicPlayer
        if (currentPlaylist.isNotEmpty()) {
            val sType = object : TypeToken<List<Song>>() {}.type
            val savedPlaylist = gson.fromJson<List<Song>>(currentPlaylist, sType)

            for (S in savedPlaylist) {
                lastPlaylist.add(S)
            }
        }

        if (lastPlaylist.isNotEmpty()) {
            if (currentSong.isNotEmpty()) {
                val sType = object : TypeToken<Int>() {}.type
                trackIndex = gson.fromJson(currentSong, sType)
                nowPlaying = lastPlaylist[trackIndex].uriValue
                myIntent.putExtra("song", nowPlaying)
            }
            else {
                nowPlaying = lastPlaylist[trackIndex].uriValue
                myIntent.putExtra("song", nowPlaying)
            }
            myMediaPlayer = MediaPlayer.create(this.context, nowPlaying)
            root.songname?.text = lastPlaylist[trackIndex].songName + " - " + lastPlaylist[trackIndex].artistName
        }
        else {
            trackIndex = 10
            nowPlaying = lastPlaylist[trackIndex].uriValue
            myIntent.putExtra("song", nowPlaying)
            myMediaPlayer = MediaPlayer.create(this.context, nowPlaying)
            root.songname?.text = lastPlaylist[trackIndex].songName + " - " + lastPlaylist[trackIndex].artistName
        }

        // listener of the play button
        root.playbutton.setOnClickListener {
            // checks if we are currently playing a song
            if (playstate == false) {
                // gets the timestamp of the last time this song was played
                if (currentTime.isNotEmpty()) {
                    val sType = object : TypeToken<Int>() {}.type
                    val lastTime = gson.fromJson<Int>(currentTime, sType)
                    myMediaPlayer?.seekTo(lastTime*1000)
                }

                // sets the UI values
                playbutton.setImageResource(R.drawable.ic_pause_black_24dp)
                // used to update the seekbar
                track = true
//                startService(myIntent) todo(skdbgvskjbngolajgnvaljgbnvlsajbvnojlsadbvjsdbvnskdjvbskjbvskjbskjdvbsodvbnobnsvb)
//                getActivity()?.startService(myIntent)
                myMediaPlayer?.start()
                // sets the seekbar
                totalTime = myMediaPlayer?.duration ?: 0
                view?.totalTime?.text  = createTimeLabel(totalTime)
                view?.playbar?.max = totalTime/1000
                view?.playbar?.progress = 0
                playstate = true
                view?.songname?.text = lastPlaylist[trackIndex].songName + " - " + lastPlaylist[trackIndex].artistName
            } else {
                // if playing a song pause it
                playbutton.setImageResource(R.drawable.ic_play_arrow_black_24dp)
                val pos = myMediaPlayer!!.getCurrentPosition()/1000
                // saves the current timestamp
                if (editor != null) {
                    editor.putString("LastTime", pos.toString())
                }
                if (editor != null) {
                    editor.apply()
                }
//                 todo(iaubgsjbgljsnglsknvbslknbvslkbnvsldkvnslkbnvslkbnsljbnsllnkjsdvlnksfbvklnjsfbkln)
//                getActivity()?.stopService(myIntent)
                myMediaPlayer?.pause()
                // tells program to stop updating seekbar
                track = false
                playstate = false
            }
        }


        // updates the seek bar every second
        this.activity?.runOnUiThread(object : Runnable {
            override fun run() {
                if (track == true) {
                    // if end of song go to next one or stop playing automatically
                    if ((myMediaPlayer!!.getCurrentPosition()/ 1000) == (totalTime / 1000)) {
                        if (loopstate == false) {
                            playbutton.setImageResource(R.drawable.ic_play_arrow_black_24dp)
                            playstate = false
                        }
                        else {
                            playNextSong()
                        }
                    }
                    val mCurrentPosition: Int = myMediaPlayer!!.getCurrentPosition() / 1000
                    playbar.setProgress(mCurrentPosition)
                    view?.playTime?.text = createTimeLabel(mCurrentPosition * 1000)
                }

                myHandler.postDelayed(this, 1000)
            }
        })

        // listens for user change on the seek bar and changes the timestamp of song to where the user clicked
        root.playbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    myMediaPlayer?.seekTo(progress * 1000)
                    val pos = myMediaPlayer!!.getCurrentPosition()/1000
                    if (editor != null) {
                        editor.putString("LastTime", pos.toString())
                    }
                    if (editor != null) {
                        editor.apply()
                    }
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
                myIntent.putExtra("song", nowPlaying)
                // todo(akjnfojsnglsjngvlsnbvlsknbvslkbnvlsnbolwsjbolwsbnpoilswnb opilwsnbvowsbnbvrolbwosb)
                myMediaPlayer = MediaPlayer.create(this.context, nowPlaying)
                myMediaPlayer?.start()
                getActivity()?.startService(myIntent)
            }
            // if shuffle on select a random song in the array
            else {
                myMediaPlayer?.release()
                trackIndex = (0 until lastPlaylist.size).random()
                nowPlaying = lastPlaylist[trackIndex].uriValue
                myIntent.putExtra("song", nowPlaying)
                // todo(akjdbgvihedbfvliasubeilvb ilaeywfbviyab sdlfiv bialdufb viuab)
                myMediaPlayer = MediaPlayer.create(this.context, nowPlaying)
                myMediaPlayer?.start()
                getActivity()?.startService(myIntent)
            }
            // changes the UI according to the song playing
            track = true
            totalTime = myMediaPlayer?.duration ?: 0
            view?.totalTime?.text  = createTimeLabel(totalTime)
            view?.playbar?.max = totalTime/1000
            view?.playbar?.progress = 0
            view?.songname?.text = lastPlaylist[trackIndex].songName + " - " + lastPlaylist[trackIndex].artistName
            playbutton.setImageResource(R.drawable.ic_pause_black_24dp)
            playstate = true
        }

        // plays the previous song using the stack, if nothing in the stack plays a random song or previous song in array
        root.prevbutton.setOnClickListener {
            if (stackCurser == 0) {
                if (shufflestate == false) {
                    myMediaPlayer?.release()
                    trackIndex = (trackIndex - 1) % lastPlaylist.size
                    nowPlaying = lastPlaylist[trackIndex].uriValue
                    myIntent.putExtra("song", nowPlaying)
                    // todo(xdectfvygbuhjiftvyugbhjknrgvgyubhjfbdvubjknfxdbvubjkhnfdxbrvguibjknfbdvbjknfbdxvbjnkfbdxvsbjknfbdjkn bdfxknjl )
                    myMediaPlayer = MediaPlayer.create(this.context, nowPlaying)
                    myMediaPlayer?.start()
                    getActivity()?.startService(myIntent)
                } else {
                    myMediaPlayer?.release()
                    trackIndex = (0 until lastPlaylist.size).random()
                    nowPlaying = lastPlaylist[trackIndex].uriValue
                    myIntent.putExtra("song", nowPlaying)
                    // todo(hsfdvb ikwsrbgowbsrngo ubfoubv dkfjb ijdbfgoudbfrug jvbbsdreoubg oudnrfbodnfbdjfbour fbgoswerngobrs)
                    myMediaPlayer = MediaPlayer.create(this.context, nowPlaying)
                    myMediaPlayer?.start()
                    getActivity()?.startService(myIntent)
                }
            }
            else {
                myMediaPlayer?.release()
                songStack("pop", trackIndex)
                nowPlaying = lastPlaylist[trackIndex].uriValue
                myIntent.putExtra("song", nowPlaying)
                // todo(ikjhbvisdhbfvi usbf viuosbrvu bsfiuvb usobv isjb isbvius bg oquaeb goabgvo)
                myMediaPlayer = MediaPlayer.create(this.context, nowPlaying)
                myMediaPlayer?.start()
                getActivity()?.startService(myIntent)
            }
            totalTime = myMediaPlayer?.duration ?: 0
            view?.totalTime?.text  = createTimeLabel(totalTime)
            view?.playbar?.max = totalTime/1000
            view?.playbar?.progress = 0
            view?.songname?.text = lastPlaylist[trackIndex].songName + " - " + lastPlaylist[trackIndex].artistName
            playstate = true
        }

        // toggles if user wants to repeat the playlist or just play one song and stop
        root.repeatbutton.setOnClickListener {
            if (loopstate == false) {
                loopstate = true
                myMediaPlayer?.isLooping = true
                repeatbutton.setImageResource(R.drawable.ic_repeat_black_24dp)
                if (editor != null) {
                    editor.putString("Loop", "true")
                }
                if (editor != null) {
                    editor.apply()
                }
            }
            else {
                loopstate = false
                myMediaPlayer?.isLooping = false
                repeatbutton.setImageResource(R.drawable.ic_repeat_one_black_24dp)
                if (editor != null) {
                    editor.putString("Loop", "false")
                }
                if (editor != null) {
                    editor.apply()
                }
            }
        }

        // toggles if user wants to play in random order or in order
        root.shufflebutton.setOnClickListener {
            if (shufflestate == false) {
                shufflestate = true
                shufflebutton.setImageResource(R.drawable.ic_shuffle_black_24dp)
                if (editor != null) {
                    editor.putString("Shuffle", "true")
                }
                if (editor != null) {
                    editor.apply()
                }
            }
            else {
                shufflestate = false
                shufflebutton.setImageResource(R.drawable.ic_trending_flat_black_24dp)
                if (editor != null) {
                    editor.putString("Shuffle", "false")
                }
                if (editor != null) {
                    editor.apply()
                }
            }
        }


        return root
    }

    // when the song is over automatically play the next song
    fun playNextSong() {
        val myIntent = Intent(getActivity(), MyService::class.java)
        songStack("push", trackIndex)
        if (shufflestate == false) {
            myMediaPlayer?.release()
            trackIndex = (trackIndex + 1) % lastPlaylist.size
            nowPlaying = lastPlaylist[trackIndex].uriValue
            myIntent.putExtra("song", nowPlaying)
            // todo(hubvisb vio sbvoubsouvbsoubvousbvousbvousbvo usb vobsvob)
            myMediaPlayer = MediaPlayer.create(this.context, nowPlaying)
            myMediaPlayer?.start()
//            getActivity()?.startService(myIntent)
        }
        else {
            myMediaPlayer?.release()
            trackIndex = (0 until lastPlaylist.size).random()
            nowPlaying = lastPlaylist[trackIndex].uriValue
            myIntent.putExtra("song", nowPlaying)
            // todo(hsbdv oisbdouv hbougb ouboueqbf ouqabfeouq abegovbqeigbeidkvgbswaoubvoaubvgouawbvobofbvg)
            myMediaPlayer = MediaPlayer.create(this.context, nowPlaying)
            myMediaPlayer?.start()
//            getActivity()?.startService(myIntent)
        }
        track = true
        totalTime = myMediaPlayer?.duration ?: 0
        view?.totalTime?.text  = createTimeLabel(totalTime)
        view?.playbar?.max = totalTime/1000
        view?.playbar?.progress = 0
        view?.songname?.text = lastPlaylist[trackIndex].songName + " - " + lastPlaylist[trackIndex].artistName
        playstate = true

    }

    // used when user clicks a song from the song fragment
    override fun onStart() {
        super.onStart()

        val sharedPreferences = this.activity?.getSharedPreferences(Media_Player, Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        val gson = Gson()
        val myIntent = Intent(getActivity(), MyService::class.java)

        // if the user click on an item from the song fragment retrieve the value
        val fromSong = sharedPreferences?.getString("SongClick", "")?:""
        if (fromSong.isNotEmpty()) {
            val sType = object : TypeToken<String>() {}.type
            val fromsong = gson.fromJson<String>(fromSong, sType)
            // plays song automatically when fragment switches from song to playing
            if (fromsong == "true") {
                playbutton.setImageResource(R.drawable.ic_pause_black_24dp)
                // todo(hbfviub eougb owsurbg ousbousbn  vousbvoabeg oubsdkjvbsou bvb osjnv os)
//                getActivity()?.startService(myIntent)
                myMediaPlayer?.start()
                totalTime = myMediaPlayer?.duration ?: 0
                view?.totalTime?.text  = createTimeLabel(totalTime)
                view?.playbar?.max = totalTime/1000
                view?.playbar?.progress = 0
                playstate = true
                track = true
                if (editor != null) {
                    editor.putString("SongClick", "false")
                }
                if (editor != null) {
                    editor.apply()
                }
            }
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

    // stops tracking and changes playstate
    override fun onStop() {
        super.onStop()
        track = false
        playstate = false
    }

    override fun onPause() {
        super.onPause()
        // stops tracking for seekbar
        track = false
//        val myIntent = Intent(getActivity(), MyService::class.java)

        // saves the state of the player before closing
        if (myMediaPlayer != null ) {
            val sharedPreferences = this.activity?.getSharedPreferences(Media_Player, Context.MODE_PRIVATE)
            val gson = Gson()

            val saveLastPlaylist = gson.toJson(lastPlaylist)
            val editor = sharedPreferences?.edit()

            if (editor != null) {
                editor.putString("LastSong", trackIndex.toString())
            }
            if ((editor != null) && playstate == true) {
                val pos = myMediaPlayer!!.getCurrentPosition()/1000
                editor.putString("LastTime", pos.toString())
            }
            if (editor != null) {
                editor.putString("LastPlaylist", saveLastPlaylist)
            }
            if (editor != null) {
                editor.apply()
            }

            playbutton.setImageResource(R.drawable.ic_play_arrow_black_24dp)
            playstate = false
//            getActivity()?.stopService(myIntent)
            myMediaPlayer?.release()
        }
    }

//    override fun onResume() {
//        super.onResume()
//        track = true
//    }

    // formats the text for the time
    fun createTimeLabel(time: Int): String {
        var timeLabel = ""
        var min = time / 1000 / 60
        var sec = time / 1000 % 60
        var hr = time / 1000 / 60 / 60

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

}


