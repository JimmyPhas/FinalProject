package com.example.finalproject

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlin.contracts.Returns

class SongDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        val DATABASE_NAME = "Songs.db"
        const val DATABASE_VERSION = 1

        val SONGS_TABLE = "song_table"
        val PLAYLIST_TABLE = "playlist_table"
        val PLAYLIST_SONGS_TABLE = "playlist_songs"
        val LAST_SESSION = "last_session"

        val _SID = "sid"
        val SONG_NAME = "song_name"
        val ARTIST_NAME = "artist_name"
        val LENGTH = "total_length"
        val URI = "uri_id"

        val _PID = "pid"
        val PLAYLIST_NAME = "playlist_name"

        val P_ID = "playlist_id"
        val S_ID = "song_id"

        val _ID = "id"
        val LAST_SONG = "last_song"
        val LAST_TIME = "last_time"
        val LAST_PLAYLIST = "last_playlist"
        val SONG_CLICK = "song_click"
        val LOOP = "loop"
        val SHUFFLE = "shuffle"


    }

    override fun onCreate(db: SQLiteDatabase?) {
        val SQL_CREATE_SONG_TABLE = "CREATE TABLE ${SONGS_TABLE} ("  +
                "${_SID} INTEGER PRIMARY KEY, " +
                "${SONG_NAME} TEXT," +
                "${ARTIST_NAME} TEXT," +
                "${LENGTH} TEXT," +
                "${URI} INTEGER)"
        db?.execSQL(SQL_CREATE_SONG_TABLE)

        val SQL_CREATE_PLAYLIST_TABLE = "CREATE TABLE ${PLAYLIST_TABLE} ("  +
                "${_PID} INTEGER PRIMARY KEY, " +
                "${PLAYLIST_NAME} TEXT)"
        db?.execSQL(SQL_CREATE_PLAYLIST_TABLE)

        val SQL_CREATE_PLAYLIST_SONG_TABLE = "CREATE TABLE ${PLAYLIST_SONGS_TABLE} ("  +
                "${P_ID} INTEGER," +
                "${S_ID} INTEGER," +
                " FOREIGN KEY(${P_ID}) REFERENCES ${PLAYLIST_SONGS_TABLE}(${_PID})" +
                " FOREIGN KEY(${S_ID}) REFERENCES ${SONGS_TABLE}(${_SID}))"
        db?.execSQL(SQL_CREATE_PLAYLIST_SONG_TABLE)

        val SQL_CREATE_LAST_SESSION = "CREATE TABLE ${LAST_SESSION} ("  +
                "${_ID} INTEGER PRIMARY KEY, " +
                "${LAST_SONG} TEXT," +
                "${LAST_TIME} INTEGER," +
                "${LAST_PLAYLIST} TEXT," +
                "${SONG_CLICK} INTEGER," +
                "${LOOP} INTEGER," +
                "${SHUFFLE} INTEGER)"
        db?.execSQL(SQL_CREATE_LAST_SESSION)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val SQL_DELETE_SONG = "DROP TABLE IF EXISTS ${SONGS_TABLE}"
        db?.execSQL(SQL_DELETE_SONG)
        val SQL_DELETE_PLAYLIST = "DROP TABLE IF EXISTS ${PLAYLIST_TABLE}"
        db?.execSQL(SQL_DELETE_PLAYLIST)
        val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS ${PLAYLIST_SONGS_TABLE}"
        db?.execSQL(SQL_DELETE_TABLE)
        onCreate(db)
    }

    fun insertSong (song: Song) {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(SONG_NAME, song.songName)
        contentValues.put(ARTIST_NAME, song.artistName)
        contentValues.put(LENGTH, song.totalLength)
        contentValues.put(URI, song.uriValue)

        db.insert(SONGS_TABLE, null, contentValues)
    }

    fun updateLast(id: String, song: Int, time: Int, playlist: String, click: Int, loop: Int, shuffle: Int): Boolean {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(_ID, id)
        contentValues.put(LAST_SONG, song)
        contentValues.put(LAST_TIME, time)
        contentValues.put(LAST_PLAYLIST, playlist)
        contentValues.put(SONG_CLICK, click)
        contentValues.put(LOOP, loop)
        contentValues.put(SHUFFLE, shuffle)

        db.update(LAST_SESSION, contentValues, "ID = ?", arrayOf(id))
        return true
    }



    fun playlistSongs(pid : String) : Cursor {
        val db = this.writableDatabase

        val cursor = db.rawQuery("SELECT * FROM ${PLAYLIST_SONGS_TABLE} inner join ${SONGS_TABLE} on ${PLAYLIST_SONGS_TABLE}.${S_ID} = ${SONGS_TABLE}.${_SID} where ${P_ID} = ?", arrayOf(pid))
        return cursor
    }

    val viewAllSongs : Cursor
        get() {
            val db = this.writableDatabase
            val cursor = db.rawQuery("SELECT * FROM ${SONGS_TABLE}", null)
            return cursor
        }

    val viewAllPlaylist : Cursor
        get() {
            val db = this.writableDatabase
            val cursor = db.rawQuery("SELECT * FROM ${PLAYLIST_TABLE}", null)
            return cursor
        }

    val viewLastSession : Cursor
        get() {
            val db = this.writableDatabase
            val cursor = db.rawQuery("SELECT * FROM ${LAST_SESSION}", null)
            return cursor
        }


}