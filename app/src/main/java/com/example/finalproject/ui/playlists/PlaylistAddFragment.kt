package com.example.finalproject.ui.playlists

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.R
import com.example.finalproject.Song
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_playlist_add.*
import kotlinx.android.synthetic.main.fragment_playlist_add.view.*

class PlaylistAdd : Fragment() {

    private val Media_Player = "MediaPlayer"
    private val AllSongs = mutableListOf<Song>()
    private val CreateNewPlaylist = mutableListOf<Song>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_playlist_add, container, false)

        root.savebutton.setOnClickListener {
            val playlistName = nameinput.text
            Toast.makeText(context, "Long press, clicked $playlistName", Toast.LENGTH_SHORT).show()
        }

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val sharedPreferences = this.activity?.getSharedPreferences(Media_Player, Context.MODE_PRIVATE)
        val gson = Gson()

        val allSongs = sharedPreferences?.getString("AllSongs", "")?:""
        val createPlaylist = sharedPreferences?.getString("newPlaylist", "")?:""

        if (allSongs.isNotEmpty()) {
            val sType = object : TypeToken<List<Song>>() {}.type
            val savedSongs = gson.fromJson<List<Song>>(allSongs, sType)

            for (S in savedSongs) {
                AllSongs.add(S)
            }
        }
        if (createPlaylist.isNotEmpty()) {
            val sType = object : TypeToken<List<Song>>() {}.type
            val addPlaylist = gson.fromJson<List<Song>>(createPlaylist, sType)

            for (S in addPlaylist) {
                CreateNewPlaylist.add(S)
            }
        }

        ALLSONGS.adapter = PlaylistAddRecyclerAdapter(AllSongs as ArrayList<Song>, this.activity)
        ALLSONGS.layoutManager = LinearLayoutManager(this.context)
    }

}
