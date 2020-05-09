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


class SongsFragment : Fragment() {

    private val Media_Player = "MediaPlayer"
    private val mySongs = mutableListOf<Song>()
    private val mySongsID = mutableListOf<Int>()
    private val songList = mutableListOf<String>()
    private val PERMISSION = 1
    private val bundle = Bundle()
    internal var dbHelper = this.context?.let { SongDBHelper(it) }


    private lateinit var songsViewModel: SongsViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        songsViewModel =
                ViewModelProviders.of(this).get(SongsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_songs, container, false)

//        swipe_refresh_layout.setOnRefreshListener {
//            swipe_refresh_layout.adapter = MyRecyclerAdapter(updatedDataSet)
//            swipe_refresh_layout.isRefreshing = false
//        }

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        try {
            val cursor = dbHelper?.viewAllSongs
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    mySongs.add(
                        Song(
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getInt(4)
                        )
                    )
                }
            }
            else {
                // makes songs from the files in the raw folder
                // had trouble trying to read from sd card so i went with this
                mySongs.add(Song("Angel", "Big Bad Bosses", "3:40", R.raw.big_bad_bosses_angel))
                mySongs.add(Song("Egg Man", "Big Bad Bosses", "3:25", R.raw.big_bad_bosses_egg_man))
                mySongs.add(Song("I'm the Boss", "Big Bad Bosses", "4:23", R.raw.big_bad_bosses_im_the_boss))
                mySongs.add(Song("Princess", "Big Bad Bosses", "3:57", R.raw.big_bad_bosses_princess))
                mySongs.add(Song("Brand New Day", "Dr. Horrible Sing Along Blog", "1:49", R.raw.brand_new_day))
                mySongs.add(Song("My Eyes", "Dr. Horrible Sing Along Blog", "2:47", R.raw.my_eyes))
                mySongs.add(Song("My Freeze Ray", "Dr. Horrible Sing Along Blog", "1:54", R.raw.my_freeze_ray))
                mySongs.add(Song("Acid Dreams", "MAX", "3:17", R.raw.max_acid_dreams))
                mySongs.add(Song("Basement Party", "MAX", "3:22", R.raw.max_basement_party))
                mySongs.add(Song("Worship", "MAX", "3:06", R.raw.max_worship))
                mySongs.add(Song("Duke Nukem", "Story Break", "1:12:24", R.raw.sb41_dukenukem))
                mySongs.add(Song("Carmen Sandiego", "Story Break", "1:26:37", R.raw.story_break_carmen_sandiego))
                mySongs.add(Song("Guess Who", "Story Break", "1:02:04", R.raw.story_break_guess_who))

                for (S in mySongs) {
                    try {
                        dbHelper?.insertSong(S)
                    } catch (e:Exception) {
                        Log.e("SONGFRAGMENT", "error $e")
                    }
                }

            }
        } catch (e: Exception){
            Log.e("SONGFRAGMENT", "error: $e")
        }

        val sharedPreferences = this.activity?.getSharedPreferences(Media_Player, Context.MODE_PRIVATE)
        val gson = Gson()
        val editor = sharedPreferences?.edit()

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

//    private fun getAllSongs(size: Int) : ArrayList<Song>{
//        if (this.context?.let { checkSelfPermission(it,Manifest.permission.READ_EXTERNAL_STORAGE) } != PackageManager.PERMISSION_GRANTED) {
//
//            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                requestPermissions( new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION)
//            }
//
//        }
//
//        val permissionCheck = this.context?.let {
//            ContextCompat.checkSelfPermission(
//                it,
//            Manifest.permission.READ_EXTERNAL_STORAGE)
//        };
//
//        if (this.context?.let {
//                ContextCompat.checkSelfPermission(
//                    it,
//                    Manifest.permission.READ_EXTERNAL_STORAGE)
//            }
//            != PackageManager.PERMISSION_GRANTED) {
//
//
//            if (ActivityCompat.shouldShowRequestPermissionRationale(
//                    this.context as Activity,
//                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
//
//            } else {
//                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION);


//        val sharedPreferences = this.activity?.getSharedPreferences(Media_Player, Context.MODE_PRIVATE)
//        val allSongs = sharedPreferences?.getString("AllSongs", "")?:""
//        val gson = Gson()
//
//        if (allSongs.isNotEmpty()) {
//            val sType = object : TypeToken<List<Song>>() {}.type
//            val allSongs = gson.fromJson<List<Song>>(allSongs, sType)
//
//            for (S in allSongs) {
//                allsongs.add(S)
//            }
//        }

//        var contentResolver: ContentResolver? = context?.getContentResolver();
//        var uri: Uri? = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//        var cursor: Cursor? = uri?.let {
//            contentResolver?.query(it, null, null, null, null)
//        }
//
//        if (cursor == null) {
//            Toast.makeText(this.context, "Something Went Wrong.", Toast.LENGTH_LONG)
//        } else if (!cursor.moveToFirst()) {
//            Toast.makeText(this.context, "No Music Found on SD Card.", Toast.LENGTH_LONG)
//        } else {
//            val Title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
//            //Getting Song ID From Cursor.
//            val id = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
//            do {
//                // You can also get the Song ID using cursor.getLong(id).
//                //long SongID = cursor.getLong(id);
//                val SongTitle = cursor.getString(Title)
//                songList.add(SongTitle)
//            } while (cursor.moveToNext())
//        }
//
//        // A helper function to create specified amount of dummy contact data
//        val songs = ArrayList<Song>()
//
//        val test = ArrayList<String>()
//        File("res/raw/").walk().forEach {
//            test.add(it.toString())
//        }
//
//        for (i in 0..size){
//            val song = Song(test[i], "jerry $i", "$i:00", 0)
//            songs.add(song)
//        }
//
//        return songs
//    }


}
