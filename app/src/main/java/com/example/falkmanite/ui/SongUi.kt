package com.example.falkmanite.ui

import com.example.falkmanite.R


abstract class SongUi(
    open val id: Int,
    open val title: String,
    open val artist: String,
    open val duration: Int
) {
//    fun <T> map(mapper: SongMapper<T>): T {
//        return mapper(id, title, artist, duration, state(), state())
//    }

    abstract fun layout() : Int

    fun isSameAs(other: SongUi): Boolean {
        return this.layout() == other.layout() && this.id == other.id
    }

    data class SongUiBase(
        override val id: Int,
        override val title: String,
        override val artist: String,
        override val duration: Int,
        private val isCurrent: Boolean,
        private val isPlaying: Boolean
    ) : SongUi(id, title, artist, duration) {
        override fun layout() = R.layout.list_item_song
        fun isPlaying() = isPlaying
        fun isCurrent() = isCurrent
    }

    data class SongUiSelector(
        override val id: Int,
        override val title: String,
        override val artist: String,
        override val duration: Int,
        private val selected: Boolean,
    ) : SongUi(id, title, artist, duration) {
        override fun layout() = R.layout.list_item_song_selector
        fun isSelected() = selected
    }
}
