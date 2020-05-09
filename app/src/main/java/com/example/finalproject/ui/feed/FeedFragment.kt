package com.example.finalproject.ui.feed

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.finalproject.LyricData
import com.example.finalproject.LyricsService
import com.example.finalproject.R
import kotlinx.android.synthetic.main.fragment_feed.*
import kotlinx.android.synthetic.main.fragment_feed.view.*
import kotlinx.android.synthetic.main.fragment_feed.view.songname
import android.text.method.ScrollingMovementMethod;
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FeedFragment : Fragment() {

    private lateinit var playingViewModel: FeedViewModel

    private val BASE_URL = "https://api.vagalume.com.br/"
    private val API = "1c9031fbe39d0b7a91d27ca877881edc"



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        playingViewModel =
            ViewModelProviders.of(this).get(FeedViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_feed, container, false)

        root.lyricssearch.setOnClickListener {
            val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
            val lyricSearchAPI = retrofit.create(LyricsService::class.java)


            if (songname.text.toString().isNullOrBlank()) {
                Toast.makeText(this.context, "Please enter a song title.", Toast.LENGTH_LONG).show()
            }
            else if (artistname.text.toString().isNullOrBlank()) {
                Toast.makeText(this.context, "Please enter aa artist.", Toast.LENGTH_LONG).show()
            }
            else if (songname.text.toString().isNullOrBlank() && artistname.text.toString().isNullOrBlank()) {
                Toast.makeText(this.context, "Please enter a song title and artist.", Toast.LENGTH_LONG).show()
            }
            else {
                val songtitle = songname.text.toString()
                val artistname = artistname.text.toString()

                lyricSearchAPI.getLyrics(API, artistname, songtitle).enqueue(object : Callback<LyricData> {
                    override fun onFailure(call: Call<LyricData>, t: Throwable) {
                        Log.d("Lyrics", "onFailure : $t")
                    }

                    override fun onResponse(call: Call<LyricData>, response: Response<LyricData>) {
                        val body = response.body()

                        if (body?.mus == null) {
                            Toast.makeText(context, "Unable to retrieve $songtitle.", Toast.LENGTH_LONG).show()
                            Log.d("Lyrics", "Valid response not received")
                            return
                        }
                        else {
                            lyrics.text = body.mus[0].text
                            lyrics.setMovementMethod(ScrollingMovementMethod());
                        }
                    }

                })

            }

            val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
        }



        return root
    }

}
