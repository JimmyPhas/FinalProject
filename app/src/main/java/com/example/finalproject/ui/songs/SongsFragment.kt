package com.example.finalproject.ui.songs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_songs.*


class SongsFragment : Fragment() {

    private val Media_Player = "MediaPlayer"
    private val mySongs = mutableListOf<Song>()

    private lateinit var songsViewModel: SongsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        songsViewModel = ViewModelProviders.of(this).get(SongsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_songs, container, false)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val sharedPreferences = this.activity?.getSharedPreferences(Media_Player, Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        val gson = Gson()

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
            mySongs.add(Song("I'm the Boss","Big Bad Bosses","4:23", R.raw.big_bad_bosses_im_the_boss))
            mySongs.add(Song("Princess", "Big Bad Bosses", "3:57", R.raw.big_bad_bosses_princess))
            mySongs.add(Song("Brand New Day","Dr. Horrible Sing Along Blog","1:49",R.raw.brand_new_day))
            mySongs.add(Song("My Eyes", "Dr. Horrible Sing Along Blog", "2:47", R.raw.my_eyes))
            mySongs.add(Song("My Freeze Ray", "Dr. Horrible Sing Along Blog", "1:54", R.raw.my_freeze_ray))
            mySongs.add(Song("Acid Dreams", "MAX", "3:17", R.raw.max_acid_dreams))
            mySongs.add(Song("Basement Party", "MAX", "3:22", R.raw.max_basement_party))
            mySongs.add(Song("Worship", "MAX", "3:06", R.raw.max_worship))
            mySongs.add(Song("Duke Nukem", "Story Break", "1:12:24", R.raw.sb41_dukenukem))
            mySongs.add(Song("Carmen Sandiego", "Story Break", "1:26:37", R.raw.story_break_carmen_sandiego))
            mySongs.add(Song("Guess Who", "Story Break", "1:02:04", R.raw.story_break_guess_who))
        }


        val StoryBreak = Playlist("StoryBreak", mutableListOf(
            Song("Carmen Sandiego", "Story Break", "1:26:37", R.raw.story_break_carmen_sandiego),
            Song("Guess Who", "Story Break", "1:02:04", R.raw.story_break_guess_who),
            Song("Duke Nukem", "Story Break", "1:12:24", R.raw.sb41_dukenukem)))

        val Max = Playlist("MAX", mutableListOf(
            Song("Worship", "MAX", "3:06", R.raw.max_worship),
            Song("Basement Party", "MAX", "3:22", R.raw.max_basement_party),
            Song("Acid Dreams", "MAX", "3:17", R.raw.max_acid_dreams)))

        val DrHorrible = Playlist("DrHorrible", mutableListOf(
            Song("My Freeze Ray", "Dr. Horrible Sing Along Blog", "1:54", R.raw.my_freeze_ray),
            Song("My Eyes", "Dr. Horrible Sing Along Blog", "2:47", R.raw.my_eyes),
            Song("Brand New Day","Dr. Horrible Sing Along Blog","1:49",R.raw.brand_new_day)))

        val BBB = Playlist("BBB", mutableListOf(
            Song("Angel", "Big Bad Bosses", "3:40", R.raw.big_bad_bosses_angel),
            Song("Egg Man", "Big Bad Bosses", "3:25", R.raw.big_bad_bosses_egg_man),
            Song("I'm the Boss","Big Bad Bosses","4:23", R.raw.big_bad_bosses_im_the_boss),
            Song("Princess", "Big Bad Bosses", "3:57", R.raw.big_bad_bosses_princess)))

        val allPlaylist = mutableListOf<Playlist>()
        allPlaylist.add(StoryBreak)
        allPlaylist.add(Max)
        allPlaylist.add(DrHorrible)
        allPlaylist.add(BBB)

        val saveAllSongs = gson.toJson(mySongs)
        val AllPlaylist = gson.toJson(allPlaylist)

        if (editor != null) {
            editor.putString("AllSongs", saveAllSongs)
        }
        if (editor != null) {
            editor.putString("AllPlaylists", AllPlaylist)
        }
        if (editor != null) {
            editor.apply()
        }

        recycler_songs.adapter = SongRecyclerAdapter(mySongs as ArrayList<Song>, this.activity)
        recycler_songs.layoutManager = LinearLayoutManager(this.context)
    }
}