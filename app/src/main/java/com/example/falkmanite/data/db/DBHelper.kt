package com.example.falkmanite.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.example.falkmanite.domain.Playlist
import javax.inject.Singleton

@Singleton
class DBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    override fun onCreate(db: SQLiteDatabase?) {
        val query = "CREATE TABLE IF NOT EXISTS $TABLE_NAME ($PLAYLIST_NAME TEXT, $SONG_ID INTEGER)"
        try {
            db?.execSQL(query)
        } catch (e: SQLiteException) {
            e.printStackTrace()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun getSongsOfPlaylist(playlistName: String): List<Int> {

        val db = readableDatabase()

        db?.let {
            val list = mutableListOf<Int>()
            val sql = "SELECT * FROM $TABLE_NAME WHERE $PLAYLIST_NAME = '$playlistName'"
            val cursor = db.rawQuery(sql, null)
            return try {
                while (cursor.moveToNext()) list.add(
                    cursor.getInt(cursor.getColumnIndexOrThrow(SONG_ID))
                )
                list
            } catch (e: SQLiteException) {
                e.printStackTrace()
                emptyList()
            } finally {
                cursor.close()
                db.close()
            }
        }
        return emptyList()
    }

    fun getAllPlaylists(): List<Playlist> {

        val db = readableDatabase()

        db?.let {
            val playlistTitles = mutableSetOf<String>()
            val sql = "SELECT * FROM $TABLE_NAME"
            val cursor = db.rawQuery(sql, null)
            return try {
                while (cursor.moveToNext()) playlistTitles.add(
                    cursor.getString(cursor.getColumnIndexOrThrow(PLAYLIST_NAME))
                )
                playlistTitles.map { Playlist(it, getSongsOfPlaylist(it)) }.toList()
            } catch (e: SQLiteException) {
                e.printStackTrace()
                emptyList()
            } finally {
                cursor.close()
                db.close()
            }
        }
        return emptyList()
    }


    fun addOrUpdatePlaylist(playlistName: String, songs: List<Int>): Playlist {

        val songsBefore = getSongsOfPlaylist(playlistName)
        val songsToRemove = songsBefore.subtract(songs.toSet())
        val songsToAdd = songs.subtract(songsBefore.toSet())

        removeSongsFromPlaylist(playlistName, songsToRemove)
        addSongsToPlaylist(playlistName, songsToAdd)

        return Playlist(playlistName, getSongsOfPlaylist(playlistName))
    }

    private fun addSongToPlaylist(playlistNane: String, songId: Int){

        val db = writableDatabase()

        db?.let {
            val values = ContentValues()
            values.put(PLAYLIST_NAME, playlistNane)
            values.put(SONG_ID, songId)
            db.insert(TABLE_NAME, null, values)
            db.close()
        }
    }

    private fun addSongsToPlaylist(playlistNane: String, songs: Collection<Int>){

        val db = writableDatabase()

        db?.let {
            try {
                songs.forEach {
                    val values = ContentValues()
                    values.put(PLAYLIST_NAME, playlistNane)
                    values.put(SONG_ID, it)
                    db.insert(TABLE_NAME, null, values)
                }
            } catch (e: SQLiteException) {
                e.printStackTrace()
            } finally {
                db.close()
            }
        }

    }

    fun deleteSong(songId: Int) {

        val db = writableDatabase()

        db?.let {
            try {
                db.delete(TABLE_NAME, "$SONG_ID = $songId", null)
            } catch (e: SQLiteException) {
                e.printStackTrace()
            } finally {
                db.close()
            }
        }

    }

    fun removeSongFromPlaylist(playlistName: String, songId: Int) {

        val db = writableDatabase()

        db?.let {
            try {
                db.delete(
                    TABLE_NAME,
                    "$PLAYLIST_NAME = '$playlistName' AND $SONG_ID = $songId",
                    null
                )
            } catch (e: SQLiteException) {
                e.printStackTrace()
            } finally {
                db.close()
            }
        }
    }

    private fun removeSongsFromPlaylist(playlistName: String, songs: Collection<Int>) {

        val db = writableDatabase()

        db?.let {
            songs.forEach {
                db.delete(
                    TABLE_NAME,
                    "$PLAYLIST_NAME = '$playlistName' AND $SONG_ID = $it",
                    null
                )
            }
            db.close()
        }
    }

    fun deletePlaylist(playlistName: String) {

        val db = writableDatabase()

        db?.let {
            db.delete(TABLE_NAME, "$PLAYLIST_NAME = '$playlistName'", null)
            db.close()
        }
    }

    private fun writableDatabase(): SQLiteDatabase? = try {
        this.writableDatabase
    } catch (e: SQLiteException) {
        e.printStackTrace()
        null
    }

    private fun readableDatabase(): SQLiteDatabase? = try {
        this.readableDatabase
    } catch (e: SQLiteException) {
        e.printStackTrace()
        null
    }

    companion object{
        private const val TAG = "DBHelper"
        private const val DATABASE_NAME = "musicPlayerDatabase.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_NAME = "playlist"
        const val PLAYLIST_NAME = "playlistName"
        const val SONG_ID = "songId"
    }

}