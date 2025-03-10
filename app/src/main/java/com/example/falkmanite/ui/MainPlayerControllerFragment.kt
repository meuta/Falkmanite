package com.example.falkmanite.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.falkmanite.databinding.FragmentMainPlayerControllerBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.example.falkmanite.domain.Mode
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainPlayerControllerFragment : Fragment() {

    private var _binding: FragmentMainPlayerControllerBinding? = null
    private val binding: FragmentMainPlayerControllerBinding
        get() = _binding ?: throw RuntimeException("FragmentMainPlayerControllerBinding == null")

    private val viewModel: SharedViewModel by activityViewModels()

    private var isProgressRunning = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainPlayerControllerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.controllerBtnPlayPause.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                if (viewModel.uiState.first().tracks.isNotEmpty()) {
                    viewModel.playOrPauseCurrentSong()
                }
            }
        }

        binding.controllerBtnStop.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                if (viewModel.uiState.first().tracks.isNotEmpty()) viewModel.stopCurrentSong()
            }
        }

        setupSeekbar()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
//                    Log.d(TAG, "onViewCreated: duration.collect = ${it.duration}")
                    setControllerSeekBar(it)
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.progress.collect {
//                    Log.d(TAG, "onViewCreated: progress = ${it.currentSec}")
//                    Log.d(TAG, "onViewCreated: progress = ${it.currentTimeSting}")
                    if (isProgressRunning.not()) binding.controllerSeekBar.progress = it.currentSec
                    binding.controllerTvCurrentTime.text = it.currentTimeSting
                }
            }
        }
    }

    private fun setupSeekbar() {
        val seekAdapter = object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) = Unit

            override fun onStartTrackingTouch(seek: SeekBar) { isProgressRunning = true }

            override fun onStopTrackingTouch(seek: SeekBar) {
//                Log.d(TAG, "setupSeekbar: currentProgress = ${seek.progress}")
                viewModel.setSongProgress(seek.progress)
                isProgressRunning = false
            }
        }

        binding.controllerSeekBar.setOnSeekBarChangeListener(seekAdapter)
    }


    private fun setControllerSeekBar(it: UiState) {
        if (it.tracks.isNotEmpty() && it.state == Mode.PLAY_MUSIC) {
            val max = it.duration
            binding.controllerSeekBar.max = max / 1000
            binding.controllerTvTotalTime.text = StringFormatter().format(max)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {

        const val TAG = "MainControllerFragment"
        @JvmStatic
        fun newInstance() = MainPlayerControllerFragment()
    }
}