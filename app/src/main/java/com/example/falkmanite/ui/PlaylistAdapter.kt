package com.example.falkmanite.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.falkmanite.R
import com.example.falkmanite.databinding.ListItemSongBinding
import com.example.falkmanite.databinding.ListItemSongSelectorBinding

class PlaylistAdapter(
    private val clickListener: ClickListener,
) : RecyclerView.Adapter<PlaylistAdapter.BindViewHolder>() {
    private val songItems = mutableListOf<SongUi>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (getSongUILayout(viewType)) {
            SONG_LAYOUT -> {
                val binding = ListItemSongBinding.inflate(inflater, parent, false)
                SongViewHolder(binding)
            }
            SELECTOR_LAYOUT -> {
                val binding = ListItemSongSelectorBinding.inflate(inflater, parent, false)
                SelectorViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: BindViewHolder, position: Int) {
        holder.bind(songItems[position])
    }

    override fun getItemViewType(position: Int): Int {
        return songItems[position].layout()
    }

    private fun Int.timeString(): String {
        return StringFormatter().format(this)
    }


    fun update(uiState: UiState?) {
        val newSongList = uiState?.tracks ?: emptyList()
        val diffCallback = SongDiffer(songItems, newSongList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        songItems.clear()
        songItems.addAll(newSongList)
        diffResult.dispatchUpdatesTo(this)
    }


    inner class SongViewHolder(binding: ListItemSongBinding) : BindViewHolder(binding.root) {
        private val artist: TextView = binding.songItemTvArtist
        private val title: TextView = binding.songItemTvTitle
        private val duration: TextView = binding.songItemTvDuration
        private val playButton: ImageButton = binding.songItemImgBtnPlayPause

        init {
            playButton.setOnClickListener {
                clickListener.onClick(id)
            }
        }

        override fun bind(item: SongUi) {
            item as SongUi.SongUiBase
            id = item.id
            artist.text = item.artist
            title.text = item.title
            duration.text = item.duration.timeString()
            playButton.setImageResource(
                getPlayButtonIcon(if (item.isPlaying()) PAUSE_ICON else PLAY_ICON)
            )
            itemView.setBackgroundColor(if (item.isCurrent()) SELECTED_COLOR else IDLE_COLOR)
        }
    }

    inner class SelectorViewHolder(binding: ListItemSongSelectorBinding) :
        BindViewHolder(binding.root) {
        private val artist: TextView = binding.songSelectorItemTvArtist
        private val title: TextView = binding.songSelectorItemTvTitle
        private val duration: TextView = binding.songSelectorItemTvDuration
        private val checkBox: CheckBox = binding.songSelectorItemCheckBox

        init {
            itemView.setOnClickListener {
                clickListener.onClick(id)
            }
        }

        override fun bind(item: SongUi) {
            item as SongUi.SongUiSelector
            id = item.id
            artist.text = item.artist
            title.text = item.title
            duration.text = item.duration.timeString()
            switch((item).isSelected())
        }

        private fun switch(selected: Boolean) {
            checkBox.isChecked = selected
            itemView.setBackgroundColor(if (selected) SELECTED_COLOR else IDLE_COLOR)
        }
    }

    abstract inner class BindViewHolder(itemView: View) : ViewHolder(itemView) {
        protected var id = 0

        init {
            itemView.setOnLongClickListener {
                clickListener.onLongClick(id)
                true
            }
        }

        abstract fun bind(item: SongUi)

    }

    interface ClickListener {
        fun onClick(id: Int)
        fun onLongClick(id: Int)
    }

    companion object {
        private const val SONG_LAYOUT = 1
        private const val SELECTOR_LAYOUT = 2
        private const val PLAY_ICON = 11
        private const val PAUSE_ICON = 12
        private const val SELECTED_COLOR = Color.LTGRAY
        private const val IDLE_COLOR = Color.WHITE

        private fun getSongUILayout(id: Int) = when(id) {
            R.layout.list_item_song -> SONG_LAYOUT
            R.layout.list_item_song_selector -> SELECTOR_LAYOUT
            else -> throw IllegalArgumentException("Unknown view type")
        }

        private fun getPlayButtonIcon(code: Int) = when(code) {
            PLAY_ICON -> R.drawable.ic_play
            PAUSE_ICON -> R.drawable.ic_pause
            else -> throw IllegalArgumentException("Unknown icon type")
        }
    }

    override fun getItemCount(): Int = songItems.size

}

class SongDiffer(
    private val oldList: List<SongUi>,
    private val newList: List<SongUi>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].isSameAs(newList[newItemPosition])
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem == newItem
    }
}
