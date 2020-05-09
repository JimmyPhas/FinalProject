package com.example.finalproject

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface LyricsService {
    @GET("search.php")
    fun getLyrics(
        @Header("apikey") apikey: String,
        @Query("art") art: String,
        @Query("mus") mus: String): Call<LyricData>
}