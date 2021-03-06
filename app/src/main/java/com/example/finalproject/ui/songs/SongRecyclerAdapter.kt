package com.example.finalproject.ui.songs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.example.finalproject.Song
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.row_song.view.*


class SongRecyclerAdapter(private val songs: ArrayList<Song>, private val activity: FragmentActivity?) : RecyclerView.Adapter<SongRecyclerAdapter.MyViewHolder>() {

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
                val gson = Gson()

                val allsongs = sharedPreferences?.getString("AllSongs", "")?:""
                val AllSongs = mutableListOf<Song>()
                if (allsongs.isNotEmpty()) {
                    val sType = object : TypeToken<List<Song>>() {}.type
                    val All = gson.fromJson<List<Song>>(allsongs, sType)

                    for (S in All) {
                        AllSongs.add(S)
                    }
                }
                val saveAllSongs = gson.toJson(AllSongs)

                // saves index to shared preferences
                if (editor != null) {
                    editor.putString("LastSong", selectedItem.toString())
                }
                if (editor != null) {
                    editor.putString("LastTime", 0.toString())
                }
                if (editor != null) {
                    editor.putString("LastPlaylist", saveAllSongs)
                }
                if (editor != null) {
                    // this tells playing fragment that user clicked on an item in the song fragment
                    editor.putString("SongClick", "true")
                }
                if (editor != null) {
                    editor.apply()
                }

                // uses navigation to switch to playing fragment
                Navigation.findNavController(activity!!,
                    R.id.nav_host_fragment
                ).navigate(R.id.navigation_playing);

            }

        }

    }
}