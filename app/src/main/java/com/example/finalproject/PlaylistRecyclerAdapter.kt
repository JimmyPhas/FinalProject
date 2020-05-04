package com.example.finalproject

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row_playlist.view.*



class PlaylistRecyclerAdapter(private val playlists: ArrayList<Playlist>) : RecyclerView.Adapter<PlaylistRecyclerAdapter.MyViewHolder>() {

    var count = 0
    private val TAG = "MyRecyclerAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

//        Log.d(TAG, "onCreateViewHolder: ${count++}")

        // create a new view
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_playlist, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val currentItem = playlists[position]
        holder.name.text = currentItem.playlistName

//        Log.d(TAG, "onBindViewHolder: $position")
    }

    override fun getItemCount(): Int {
        // Return the size of your dataset (invoked by the layout manager)
        return playlists.size
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    inner class MyViewHolder (itemView: View): RecyclerView.ViewHolder (itemView){
        // This class will represent a single row in our recyclerView list
        // This class also allows caching views and reuse them
        val name = itemView.playlist_name

        // Set onClickListener to show a toast message for the selected row item in the list
        init {
            itemView.setOnClickListener{
                val selectedItem = adapterPosition
                Toast.makeText(itemView.context, "You clicked on $selectedItem",
                    Toast.LENGTH_SHORT).show()
            }

            // Set onLongClickListener to show a toast message and remove the selected row item from the list
            itemView.setOnLongClickListener {

                val selectedItem = adapterPosition
                playlists.removeAt(selectedItem)
                notifyItemRemoved(selectedItem)
                Toast.makeText(itemView.context, "Long press, deleting $selectedItem",
                    Toast.LENGTH_SHORT).show()

                return@setOnLongClickListener true
            }

        }

    }
}