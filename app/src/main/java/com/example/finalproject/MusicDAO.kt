package com.example.finalproject

import androidx.room.*

@Dao
interface MusicDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSong(song: SongEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlaylist(name: PlaylistEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLastSesh(session: LastSessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlaylistSongs(songs: PlaylistSongsEntity)

    @Update
    fun updateLastSesh(session: LastSessionEntity)

    @Delete
    fun deletePlaylist(name: PlaylistEntity)

    @Delete
    fun deletePlaylistSong(song: PlaylistSongsEntity)

    @Query("SELECT * FROM song_table")
    fun viewAllSongs() : List<SongEntity>

    @Query("SELECT * FROM playlist_table")
    fun viewAllPlaylists() : List<PlaylistEntity>

    @Query("SELECT * FROM playlist_songs INNER JOIN song_table ON playlist_songs.songID = song_table.sid WHERE playID LIKE :playlistID")
    fun getPlaylist(playlistID: Int) : PlaylistSongsEntity

    @Query("SELECT * FROM last_session WHERE id LIKE :sessionID")
    fun getSession(sessionID: Int) : LastSessionEntity
}