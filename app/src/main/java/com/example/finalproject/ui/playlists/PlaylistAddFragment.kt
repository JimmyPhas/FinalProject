package com.example.finalproject.ui.playlists

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.R
import com.example.finalproject.Song
import com.example.finalproject.ui.songs.SongRecyclerAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_playlist_add.*
import kotlinx.android.synthetic.main.fragment_playlist_add.view.*
import kotlinx.android.synthetic.main.fragment_playlist_add.view.recyclerView
import kotlinx.android.synthetic.main.fragment_songs.*

class PlaylistAdd : Fragment() {

    private val Media_Player = "MediaPlayer"
    private val AllSongs = mutableListOf<Song>()

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

        }

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val sharedPreferences = this.activity?.getSharedPreferences(Media_Player, Context.MODE_PRIVATE)
        val gson = Gson()

        val allSongs = sharedPreferences?.getString("AllSongs", "")?:""

        if (allSongs.isNotEmpty()) {
            val sType = object : TypeToken<List<Song>>() {}.type
            val savedSongs = gson.fromJson<List<Song>>(allSongs, sType)

            for (S in savedSongs) {
                AllSongs.add(S)
            }


            recyclerView.adapter = AddPlaylistRecyclerAdapter(AllSongs as ArrayList<Song>, this.activity)
            recyclerView.layoutManager = LinearLayoutManager(this.context)
        }
    }

}
