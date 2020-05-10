package com.example.finalproject

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.ui.playing.PlayingFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_playing.view.*
import kotlinx.android.synthetic.main.row_song.view.*
import java.lang.Exception


class SongRecyclerAdapter(private val songs: ArrayList<Song>, private val activity: FragmentActivity?, context: Context) : RecyclerView.Adapter<SongRecyclerAdapter.MyViewHolder>() {

    var count = 0
    private val Media_Player = "MediaPlayer"
    private val TAG = "MyRecyclerAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        // create a new view
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_song, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val currentItem = songs[position]
        holder.name.text = currentItem.songName
        holder.artist.text = currentItem.artistName
        holder.length.text = currentItem.totalLength

    }

    override fun getItemCount(): Int {
        // Return the size of your dataset (invoked by the layout manager)
        return songs.size
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    inner class MyViewHolder (itemView: View): RecyclerView.ViewHolder (itemView){
        // This class will represent a single row in our recyclerView list
        // This class also allows caching views and reuse them
        val name = itemView.song_name
        val artist = itemView.artist
        val length = itemView.song_length

        // Set onClickListener
        init {
            // if clicks on an item switch to playing fragment and play it immediately
            itemView.setOnClickListener {
                val selectedItem = adapterPosition

                val sharedPreferences = activity!!.getSharedPreferences(Media_Player, Context.MODE_PRIVATE)
                val editor = sharedPreferences?.edit()

                // saves index to shared preferences
                if (editor != null) {
                    editor.putString("LastSong", selectedItem.toString())
                }
                if (editor != null) {
                    editor.putString("LastTime", 0.toString())
                }
                if (editor != null) {
                    // this tells playing fragment that user clicked on an item in the song fragment
                    editor.putString("SongClick", "true")
                }
                if (editor != null) {
                    editor.apply()
                }

                // uses navigation to switch to playing fragment
                Navigation.findNavController(activity!!, R.id.nav_host_fragment).navigate(R.id.navigation_playing);

            }

        }

    }
}