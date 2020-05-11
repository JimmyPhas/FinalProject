package com.example.finalproject.ui.playlists

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.example.finalproject.Song
import kotlinx.android.synthetic.main.row_song.view.*


class PlaylistAddRecyclerAdapter(private val songs: ArrayList<Song>, private val activity: FragmentActivity?) : RecyclerView.Adapter<PlaylistAddRecyclerAdapter.MyViewHolder>() {

    var count = 0
    private val Media_Player = "MediaPlayer"
    private val TAG = "MyRecyclerAdapter"
    val newPlaylist = mutableListOf<Song>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        // create a new view
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_song, parent, false)
        for (S in songs) {
            newPlaylist.add(S)
        }
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

        // Set onClickListener to show a toast message for the selected row item in the list
        init {
            itemView.setOnClickListener{


            }

            // Set onLongClickListener to show a toast message and remove the selected row item from the list
            itemView.setOnLongClickListener {

                val selectedItem = adapterPosition
                newPlaylist.removeAt(selectedItem)
                notifyItemRemoved(selectedItem)

                return@setOnLongClickListener true
            }

        }

    }
}