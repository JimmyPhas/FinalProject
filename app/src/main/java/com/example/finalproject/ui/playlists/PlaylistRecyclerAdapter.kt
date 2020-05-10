package com.example.finalproject.ui.playlists

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.Playlist
import com.example.finalproject.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.row_playlist.view.*



class PlaylistRecyclerAdapter(private val playlists: ArrayList<Playlist>, private val activity: FragmentActivity?) : RecyclerView.Adapter<PlaylistRecyclerAdapter.MyViewHolder>() {

    var count = 0
    private val Media_Player = "MediaPlayer"
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

                val sharedPreferences = activity!!.getSharedPreferences(Media_Player, Context.MODE_PRIVATE)
                val editor = sharedPreferences?.edit()
                val gson = Gson()

                val allPlaylists = sharedPreferences?.getString("AllPlaylists", "")?:""
                val allplaylists = mutableListOf<Playlist>()
                if (allPlaylists.isNotEmpty()) {
                    val sType = object : TypeToken<List<Playlist>>() {}.type
                    val allPlaylist = gson.fromJson<List<Playlist>>(allPlaylists, sType)

                    for (P in allPlaylist) {
                        allplaylists.add(P)
                    }
                }

                val clicked = gson.toJson(allplaylists[selectedItem].songs)
                // saves index to shared preferences
                if (editor != null) {
                    editor.putString("LastSong", 0.toString())
                }
                if (editor != null) {
                    editor.putString("LastTime", 0.toString())
                }
                if (editor != null) {
                    editor.putString("LastPlaylist", clicked)
                }
                if (editor != null) {
                    // this tells playing fragment that user clicked on an item in the song fragment
                    editor.putString("PlaylistClick", "true")
                }
                if (editor != null) {
                    editor.apply()
                }

                // uses navigation to switch to playing fragment
                Navigation.findNavController(activity!!,
                    R.id.nav_host_fragment
                ).navigate(R.id.navigation_playing);
            }

//            // Set onLongClickListener to show a toast message and remove the selected row item from the list
//            itemView.setOnLongClickListener {
//
//                val selectedItem = adapterPosition
//                playlists.removeAt(selectedItem)
//                notifyItemRemoved(selectedItem)
//                Toast.makeText(itemView.context, "Long press, deleting $selectedItem",
//                    Toast.LENGTH_SHORT).show()
//
//                return@setOnLongClickListener true
//            }

        }

    }
}