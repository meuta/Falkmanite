package com.example.falkmanite

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.falkmanite.databinding.ActivityMainBinding
import com.example.falkmanite.domain.UiStateMapper
import com.example.falkmanite.service.PlayerServiceConnection
import com.example.falkmanite.ui.ListDialog
import com.example.falkmanite.ui.PlaylistAdapter
import com.example.falkmanite.ui.SharedViewModel
import com.example.falkmanite.ui.Toaster
import com.example.falkmanite.ui.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val uiStateToFragmentMapper = UiStateMapper.ToFragment()
    private var selectPlaylistDialog = ListDialog("", emptyList())
    private var deletePlaylistDialog = ListDialog("", emptyList())
    private lateinit var binding: ActivityMainBinding

    private val viewModel: SharedViewModel by viewModels()


    @Inject
    lateinit var playerServiceConnection: PlayerServiceConnection


    private val playlistAdapter = PlaylistAdapter(
        object : PlaylistAdapter.ClickListener {
            override fun onClick(id: Int) = viewModel.handleItemSongClick(id)
            override fun onLongClick(id: Int) = viewModel.handleItemSongLongClick(id)
        }
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarMainActivity)

        addMenuProvider(menuProvider)

        playerServiceConnection.startAndBindService()

        binding.mainSongList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = playlistAdapter
            itemAnimator = null
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
//                    Log.d(TAG, "AllSongs uiState = ${songs.map { it.map { it.id to it.trackState } }}")
//                    Log.d(TAG, "playlists = ${it?.playlists?.map { it.title to it.songsIds }}")
//                    Log.d(TAG, "songs = ${state?.tracks?.map { (it as? SongUi.SongUiBase)?.let { listOf(it.id, it.isCurrent(), it.isPlaying()) } } }")

                    playlistAdapter.update(state)
                    preparePlaylistDialogs(state)
                    setBottomController(state)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.singleMessage.collect() {
                    if (it.isNotBlank()) Toaster(it).show(this@MainActivity)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.progressFlow.collect {
//                    Log.d(TAG, "onCreate: progress = ${it.currentTimeSting}")
//                    Log.d(TAG, "onCreate: progress to isFinished = ${it.currentTimeSting to it.isFinished}")
//                    if (it.isFinished )Log.d(TAG, "onCreate: isFinished = ${it.isFinished}")
                    it.complete(viewModel)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.askPermission.collect {
                    if (it) {
                        checkPermissionAndPerform { viewModel.newPlaylistOfAllSongs(getString(R.string.all_songs)) }
                        viewModel.resetAskPermission()
                    }
                }
            }
        }
    }


    private fun checkPermissionAndPerform(callback: () -> Unit) {
        if (
            Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2 &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            callback()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_STORAGE_PERMISSION_CODE
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewModel.newPlaylistOfAllSongs(getString(R.string.all_songs))
            } else {
                Toast.makeText(
                    this,
                    "Songs cannot be loaded without permission",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun preparePlaylistDialogs(uiState: UiState?) {
        uiState?.let {
            selectPlaylistDialog =
                ListDialog(getString(R.string.choose_playlist_to_load), uiState.playlists) {
                    viewModel.loadSongsOfPlaylist(it)
                }
            deletePlaylistDialog = ListDialog(
                getString(R.string.choose_playlist_to_delete),
                uiState.playlists.filterNot { it.title == getString(R.string.all_songs) }) {
                viewModel.deletePlaylist(it)
            }
        }
    }


    private val menuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.main_activity_menu, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {

                R.id.mainMenuAddPlaylist -> {
                    if (binding.mainSongList.childCount == 0) {
                        lifecycleScope.launch {
                            viewModel.showMessage(getString(R.string.no_songs_click_search))
                        }
                    } else {
                        viewModel.selectionState()
                    }
                    true
                }

                R.id.mainMenuLoadPlaylist -> {
                    checkPermissionAndPerform { selectPlaylistDialog.show(this@MainActivity) }
                    true
                }

                R.id.mainMenuDeletePlaylist -> {
                    checkPermissionAndPerform { deletePlaylistDialog.show(this@MainActivity) }
                    true
                }

                else -> false
            }
        }
    }

    private fun setBottomController(uiState: UiState?) {
        val fragmentInstance =
            uiState?.map(uiStateToFragmentMapper) ?: UiState.mapDefault(uiStateToFragmentMapper)
        val tag = fragmentInstance.javaClass.simpleName
        if (supportFragmentManager.findFragmentByTag(tag) == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.mainFragmentContainer, fragmentInstance, tag)
                .commit()
        }
    }


    override fun onResume() {
        super.onResume()
        viewModel.updateUiFromCache()
        playerServiceConnection.startAndBindService()
    }


    companion object {
        private const val TAG = "MainActivity"
        private const val READ_STORAGE_PERMISSION_CODE = 1
    }
}
