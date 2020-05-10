package com.example.finalproject.ui.songs

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_songs.*
import java.lang.Exception
import androidx.appcompat.app.AlertDialog
import androidx.room.Room


class SongsFragment : Fragment() {

    private val Media_Player = "MediaPlayer"
    private val mySongs = mutableListOf<Song>()

    private lateinit var songsViewModel: SongsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        songsViewModel =
            ViewModelProviders.of(this).get(SongsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_songs, container, false)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val sharedPreferences =
            this.activity?.getSharedPreferences(Media_Player, Context.MODE_PRIVATE)
        val gson = Gson()
        val editor = sharedPreferences?.edit()

        val allSongs = sharedPreferences?.getString("AllSongs", "") ?: ""

        // gets all songs saved in shared preferences
        if (allSongs.isNotEmpty()) {
            val sType = object : TypeToken<List<Song>>() {}.type
            val savedPlaylist = gson.fromJson<List<Song>>(allSongs, sType)

            for (S in savedPlaylist) {
                mySongs.add(S)
            }
        } else {
            // makes songs from the files in the raw folder
            // had trouble trying to read from sd card so i went with this
            mySongs.add(Song("Angel", "Big Bad Bosses", "3:40", R.raw.big_bad_bosses_angel))
            mySongs.add(Song("Egg Man", "Big Bad Bosses", "3:25", R.raw.big_bad_bosses_egg_man))
            mySongs.add(
                Song(
                    "I'm the Boss",
                    "Big Bad Bosses",
                    "4:23",
                    R.raw.big_bad_bosses_im_the_boss
                )
            )
            mySongs.add(Song("Princess", "Big Bad Bosses", "3:57", R.raw.big_bad_bosses_princess))
            mySongs.add(
                Song(
                    "Brand New Day",
                    "Dr. Horrible Sing Along Blog",
                    "1:49",
                    R.raw.brand_new_day
                )
            )
            mySongs.add(Song("My Eyes", "Dr. Horrible Sing Along Blog", "2:47", R.raw.my_eyes))
            mySongs.add(
                Song(
                    "My Freeze Ray",
                    "Dr. Horrible Sing Along Blog",
                    "1:54",
                    R.raw.my_freeze_ray
                )
            )
            mySongs.add(Song("Acid Dreams", "MAX", "3:17", R.raw.max_acid_dreams))
            mySongs.add(Song("Basement Party", "MAX", "3:22", R.raw.max_basement_party))
            mySongs.add(Song("Worship", "MAX", "3:06", R.raw.max_worship))
            mySongs.add(Song("Duke Nukem", "Story Break", "1:12:24", R.raw.sb41_dukenukem))
            mySongs.add(
                Song(
                    "Carmen Sandiego",
                    "Story Break",
                    "1:26:37",
                    R.raw.story_break_carmen_sandiego
                )
            )
            mySongs.add(Song("Guess Who", "Story Break", "1:02:04", R.raw.story_break_guess_who))
        }


        val saveAllSongs = gson.toJson(mySongs)
        if (editor != null) {
            editor.putString("AllSongs", saveAllSongs)
        }
        if (editor != null) {
            editor.putString("LastPlaylist", saveAllSongs)
        }
        if (editor != null) {
            editor.apply()
        }

        recycler_songs.adapter =
            this.context?.let { SongRecyclerAdapter(mySongs as ArrayList<Song>, this.activity, it) }
        recycler_songs.layoutManager = LinearLayoutManager(this.context)
    }
}