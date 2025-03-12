package com.example.falkmanite.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.falkmanite.R
import com.example.falkmanite.domain.InMemoryCache
import com.example.falkmanite.domain.Mode
import com.example.falkmanite.domain.PlayerState
import com.example.falkmanite.domain.PlayerStateMapper
import com.example.falkmanite.domain.Playlist
import com.example.falkmanite.domain.usecase.AddSongToSelectionUseCase
import com.example.falkmanite.domain.usecase.ChangePlayerModeUseCase
import com.example.falkmanite.domain.usecase.CreatePlaylistUseCase
import com.example.falkmanite.domain.usecase.DeletePlaylistUseCase
import com.example.falkmanite.domain.usecase.LoadAllSongsPlaylistUseCase
import com.example.falkmanite.domain.usecase.LoadPlaylistUseCase
import com.example.falkmanite.domain.usecase.PlayOrPauseTrackUseCase
import com.example.falkmanite.domain.usecase.ReturnToPlayStateUseCase
import com.example.falkmanite.domain.usecase.SetSongProgressUseCase
import com.example.falkmanite.domain.usecase.StopCurrentTrackUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val playOrPauseTrackUseCase: PlayOrPauseTrackUseCase,
    private val addSongToSelectionUseCase: AddSongToSelectionUseCase,
    private val loadAllSongsPlaylistUseCase: LoadAllSongsPlaylistUseCase,
    private val deletePlaylistUseCase: DeletePlaylistUseCase,
    private val loadPlaylistUseCase: LoadPlaylistUseCase,
    private val stopCurrentTrackUseCase: StopCurrentTrackUseCase,
    private val changePlayerModeUseCase: ChangePlayerModeUseCase,
    private val returnToPlayStateUseCase: ReturnToPlayStateUseCase,
    private val createPlaylistUseCase: CreatePlaylistUseCase,
    private val setSongProgressUseCase: SetSongProgressUseCase,
    private val cache: InMemoryCache<PlayerState>,
    private val playerStateUiMapper: PlayerStateMapper<UiState>,
    progressState: ProgressStateFlow,
    progressUiMapper: ProgressMapper,
    @ApplicationContext context: Context
) : ViewModel(), OnTrackCompletionListener {


    private val _uiState = MutableStateFlow<UiState?>(null)
    val uiState: StateFlow<UiState?> = _uiState

    val progress: StateFlow<ProgressStateUi> = progressState.map(progressUiMapper)

    private var _singleMessage = MutableSharedFlow<String>()
    val singleMessage: SharedFlow<String> = _singleMessage

    private var _askPermission = MutableStateFlow<Boolean>(false)
    val askPermission: StateFlow<Boolean> = _askPermission

    fun resetAskPermission() {_askPermission.value = false}

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        if (
            Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2 &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            newPlaylistOfAllSongs(context.getString(R.string.all_songs))
        } else {
            _askPermission.value = true
        }
    }

    fun handleItemSongClick(id: Int) = updateUiState {
        when (cache.read().mode) {
            Mode.PLAY_MUSIC -> playOrPauseTrackUseCase(id)
            Mode.ADD_PLAYLIST -> addSongToSelectionUseCase(id)
        }
    }

    fun playOrPauseCurrentSong() = updateUiState { playOrPauseTrackUseCase(cache.read().currentTrack.id) }

    fun stopCurrentSong() = updateUiState { stopCurrentTrackUseCase() }

    fun handleItemSongLongClick(id: Int) = updateUiState { changePlayerModeUseCase(id) }

    fun selectionState() = updateUiState { changePlayerModeUseCase() }

    suspend fun showMessage(message: String) {
        _singleMessage.emit(message)
    }

    fun createPlaylist(name: String) = scope.launch { updateUiStateSuspend { createPlaylistUseCase(name) } }

    fun isSongSelected() = cache.read().isAnySelected()

    fun loadSongsOfPlaylist(playlist: Playlist) = updateUiState { loadPlaylistUseCase(playlist) }

    fun backToPlayState() = updateUiState { returnToPlayStateUseCase() }

    fun deletePlaylist(playlist: Playlist) = scope.launch { updateUiStateSuspend { deletePlaylistUseCase(playlist) } }

    fun newPlaylistOfAllSongs(title: String) =  scope.launch {
        loadAllSongsPlaylistUseCase(title).let {
            if (it == null) showMessage("no songs found on device")
            updateUiStateSuspend { it }
        }
    }

    fun setSongProgress(sec: Int) { setSongProgressUseCase(sec * 1000) }

    private fun updateUiState(stateProducer: () -> PlayerState) {
        _uiState.value = stateProducer().map(playerStateUiMapper)
    }

    private suspend fun updateUiStateSuspend(stateProducer: suspend () -> PlayerState?) {
        _uiState.value = stateProducer()?.map(playerStateUiMapper)
    }

    override fun onTrackCompletion() {
        cache.save(cache.read().apply { currentTrack.stop() })
        updateUiState { cache.read() }
    }


    companion object {
        const val TAG = "SharedViewModel"
    }
}