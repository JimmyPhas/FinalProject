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
    private val bundle = Bundle()
    internal var dbHelper = SongDBHelper(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

//        Log.d(TAG, "onCreateViewHolder: ${count++}")

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


//        Log.d(TAG, "onBindViewHolder: $position")
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
            itemView.setOnClickListener {
                val selectedItem = adapterPosition
                val lastSession = mutableListOf<LastSession>()
                try {
                    val cursor1 = dbHelper.viewLastSession
                    if (cursor1 != null) {
                        while (cursor1.moveToNext()) {
                            lastSession.add(
                                LastSession(
                                    cursor1.getInt(1),
                                    cursor1.getInt(2),
                                    cursor1.getString(3),
                                    cursor1.getInt(4),
                                    cursor1.getInt(5),
                                    cursor1.getInt(6)
                                )
                            )
                        }
                    }

                    val cursor2 = dbHelper.updateLast("1", selectedItem, 0, "ALLDBSONGS", 1, lastSession[0].loop, lastSession[0].shuffle)
                    if (cursor2 == true) {
                        Log.d("SONGRECYCLER", "Successful update")
                    }
                } catch (e: Exception){
                    Log.e("SONGRECYCLER", "error: $e")
                }



                val sharedPreferences = activity!!.getSharedPreferences(Media_Player, Context.MODE_PRIVATE)
                val editor = sharedPreferences?.edit()

                if (editor != null) {
                    editor.putString("LastSong", selectedItem.toString())
                }
                if (editor != null) {
                    editor.putString("LastTime", 0.toString())
                }
                if (editor != null) {
                    editor.putString("SongClick", "true")
                }
                if (editor != null) {
                    editor.apply()
                }

                Navigation.findNavController(activity!!, R.id.nav_host_fragment).navigate(R.id.navigation_playing);

            }

            // Set onLongClickListener to show a toast message and remove the selected row item from the list
//            itemView.setOnLongClickListener {
//
//                val selectedItem = adapterPosition
//                songs.removeAt(selectedItem)
//                notifyItemRemoved(selectedItem)
//                Toast.makeText(itemView.context, "Long press, deleting $selectedItem",
//                    Toast.LENGTH_SHORT).show()
//
//                return@setOnLongClickListener true
//            }

        }

    }
}