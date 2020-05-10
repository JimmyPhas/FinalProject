package com.example.finalproject.ui.playlists

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.*
import com.example.finalproject.ui.playing.PlayingFragment
import com.example.finalproject.ui.songs.SongsFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_playlists.*

class PlaylistsFragment : Fragment() {

    private val Media_Player = "MediaPlayer"
    private val allplaylists = mutableListOf<Playlist>()

    private lateinit var playlistsViewModel: PlaylistsViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        playlistsViewModel =
                ViewModelProviders.of(this).get(PlaylistsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_playlists, container, false)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val sharedPreferences = this.activity?.getSharedPreferences(Media_Player, Context.MODE_PRIVATE)
        val allPlaylists = sharedPreferences?.getString("AllPlaylists", "")?:""
        val gson = Gson()


        if (allPlaylists.isNotEmpty()) {
            val sType = object : TypeToken<List<Playlist>>() {}.type
            val allPlaylist = gson.fromJson<List<Playlist>>(allPlaylists, sType)

            for (P in allPlaylist) {
                allplaylists.add(P)
            }
        }

        recycler_playlists.adapter = PlaylistRecyclerAdapter(allplaylists as ArrayList<Playlist>, this.activity)
        recycler_playlists.layoutManager = LinearLayoutManager(this.context)
    }


}
