package com.example.finalproject.ui.playlists

import android.content.Context
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.finalproject.Playlist
import com.example.finalproject.R
import com.example.finalproject.Song
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_playlist_add.*
import kotlinx.android.synthetic.main.fragment_playlist_add.view.*


class PlaylistAdd : Fragment() {

    private val Media_Player = "MediaPlayer"
    private val AllSongs = mutableListOf<Song>()
    private val allplaylists = mutableListOf<Playlist>()
    private val boxes = mutableListOf<CheckBox>()
    private val CreateNewPlaylist = mutableListOf<Song>()
//    lateinit var adaptor: PlaylistAddRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_playlist_add, container, false)

        val sharedPreferences = this.activity?.getSharedPreferences(Media_Player, Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        val gson = Gson()


        val allSongs = sharedPreferences?.getString("AllSongs", "")?:""
        val allPlaylists = sharedPreferences?.getString("AllPlaylists", "")?:""

        if (allPlaylists.isNotEmpty()) {
            val sType = object : TypeToken<List<Playlist>>() {}.type
            val allPlaylist = gson.fromJson<List<Playlist>>(allPlaylists, sType)

            for (P in allPlaylist) {
                allplaylists.add(P)
            }
        }


        var counter = 0
        if (allSongs.isNotEmpty()) {
            val sType = object : TypeToken<List<Song>>() {}.type
            val savedSongs = gson.fromJson<List<Song>>(allSongs, sType)

            for (S in savedSongs) {
                val cb = CheckBox(this.context)
                cb.text = S.songName + " - " + S.artistName
                cb.isChecked = false
                cb.id = counter
                root.containercheck.addView(cb)
                boxes.add(cb)
                AllSongs.add(S)
                counter++
            }
        }


        root.savebutton.setOnClickListener {
            for (B in boxes) {
                if (B.isChecked) {
                    CreateNewPlaylist.add(AllSongs[B.id])
                }
            }
            val title = nameinput.text.toString()

            if (title.isNullOrBlank()) {
                Toast.makeText(context, "Please enter a playlist title.", Toast.LENGTH_LONG).show()
            }
            else if (CreateNewPlaylist.isEmpty()) {
                Toast.makeText(context, "Please select songs", Toast.LENGTH_LONG).show()
            }
            else {
                val createdPlaylist = Playlist(title, CreateNewPlaylist)
                allplaylists.add(createdPlaylist)

                val AllPlaylist = gson.toJson(allplaylists)

                if (editor != null) {
                    editor.putString("AllPlaylists", AllPlaylist)
                }
                if (editor != null) {
                    editor.apply()
                }

                this.activity?.let { it1 -> Navigation.findNavController(it1, R.id.nav_host_fragment).navigate(R.id.navigation_playlists) }
            }
        }

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        val sharedPreferences = this.activity?.getSharedPreferences(Media_Player, Context.MODE_PRIVATE)
//        val gson = Gson()
//
//        val allSongs = sharedPreferences?.getString("AllSongs", "")?:""
//        val createPlaylist = sharedPreferences?.getString("newPlaylist", "")?:""
//
//        if (allSongs.isNotEmpty()) {
//            val sType = object : TypeToken<List<Song>>() {}.type
//            val savedSongs = gson.fromJson<List<Song>>(allSongs, sType)
//
//            for (S in savedSongs) {
//                AllSongs.add(S)
//            }
//        }



//        ALLSONGS.adapter = PlaylistAddRecyclerAdapter(AllSongs as ArrayList<Song>, this.activity)
//        ALLSONGS.layoutManager = LinearLayoutManager(this.context)
    }

}
