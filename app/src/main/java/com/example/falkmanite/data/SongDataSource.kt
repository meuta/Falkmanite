package com.example.falkmanite.data

import android.content.Context
import android.provider.MediaStore.Audio.Media.*
import com.example.falkmanite.domain.Song
import javax.inject.Singleton

@Singleton
class SongDataSource(private val context: Context) {

    private var songs = mutableListOf<Song>()

    fun readAll() : List<Song> {
        songs = mutableListOf()
        context.contentResolver.query(
            EXTERNAL_CONTENT_URI,
            arrayOf(_ID, ARTIST, TITLE, DURATION),
            null, null, null,
        )?.use { cursor ->

            while (cursor.moveToNext()) {
                val id = cursor.getInt(0)
                val artist = cursor.getString(1) ?: ""
                val title = cursor.getString(2) ?: ""
                val duration = cursor.getInt(3)
//                Log.d(TAG, "readAll: $id, $artist, $title, $duration")
                songs.add(Song(id, title, artist, duration))
            }
        }
        return songs
    }


    fun findById(id: Int): Song? {
        return songs.firstOrNull { it.id == id }
    }

    fun findByIds(songsIds: List<Int>): List<Song> {
        return songs.filter { it.id in songsIds }
    }

    companion object {
        const val TAG = "SongDataSource"
    }
}
