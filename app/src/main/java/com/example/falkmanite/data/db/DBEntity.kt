package com.example.falkmanite.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist")
data class DBEntity(
    @PrimaryKey(autoGenerate = true)
    val dBEntityId: Long,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo("songsId")
    val songId: Int
){
    companion object {
        const val UNDEFINED_ID = 0L
    }
}