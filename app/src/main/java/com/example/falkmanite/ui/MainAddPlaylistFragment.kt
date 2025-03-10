package com.example.falkmanite.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.falkmanite.R
import com.example.falkmanite.databinding.FragmentMainAddPlaylistBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainAddPlaylistFragment : Fragment() {

    private var _binding: FragmentMainAddPlaylistBinding? = null
    private val binding: FragmentMainAddPlaylistBinding
        get() = _binding ?: throw RuntimeException("FragmentMainAddPlaylistBinding == null")

    private val viewModel: SharedViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainAddPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addPlaylistBtnCancel.setOnClickListener {
            viewModel.backToPlayState()
        }

        binding.addPlaylistBtnOk.setOnClickListener {

            val playlistName = binding.addPlaylistEtPlaylistTitle.text.toString()
            val messageId = when {
                viewModel.isSongSelected().not() -> R.string.add_at_least_one_song_to_your_playlist
                playlistName.isBlank() -> R.string.add_a_name_to_your_playlist
                playlistName == getString(R.string.all_songs) ->
                    R.string.all_songs_is_a_reserved_name_choose_another_playlist_name
                else -> {
                    viewModel.createPlaylist(playlistName)
                    return@setOnClickListener
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.showMessage(getString(messageId))
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {

        const val TAG = "MainAddPlaylistFragment"
        @JvmStatic
        fun newInstance() = MainAddPlaylistFragment()
    }
}