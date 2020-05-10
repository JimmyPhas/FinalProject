package com.example.finalproject

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.StringReader

@Entity(tableName = "song_table")
data class SongEntity (
    @PrimaryKey(autoGenerate = true)
    var sid : Int,
    var songName : String,
    var artistName : String,
    var totalLength : String,
    var uriID : Int
)

@Entity(tableName = "playlist_table")
data class PlaylistEntity (
    @PrimaryKey(autoGenerate = true)
    var pid : Int,
    var playlistName : String
)

@Entity(tableName = "playlist_songs", foreignKeys = arrayOf(
    ForeignKey(entity = PlaylistEntity::class, parentColumns = arrayOf("pid"), childColumns = arrayOf("playID"), onDelete = ForeignKey.CASCADE),
    ForeignKey(entity = SongEntity::class, parentColumns = arrayOf("sid"), childColumns = arrayOf("songID"), onDelete = ForeignKey.CASCADE)
))
data class PlaylistSongsEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var playID : Int,
    var songID : Int
)

@Entity(tableName = "last_session")
data class LastSessionEntity (
    @PrimaryKey(autoGenerate = true)
    var id : Int,
    var lastSong : Int,
    var lastTime : Int,
    var lastPlaylist : String,
    var songClick : Int,
    var loop : Int,
    var shuffle : Int
)
