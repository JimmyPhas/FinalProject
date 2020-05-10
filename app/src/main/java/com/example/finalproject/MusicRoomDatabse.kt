package com.example.finalproject

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(SongEntity::class, PlaylistEntity::class, PlaylistSongsEntity::class, LastSessionEntity::class), version = 1)
abstract class MusicRoomDatabse : RoomDatabase() {
    abstract fun musicDAO() : MusicDAO

}