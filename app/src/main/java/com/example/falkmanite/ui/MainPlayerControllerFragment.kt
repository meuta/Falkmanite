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
import kotlinx.coroutines.launch
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainPlayerControllerFragment : Fragment() {

    private var _binding: FragmentMainPlayerControllerBinding? = null
    private val binding: FragmentMainPlayerControllerBinding
        get() = _binding ?: throw RuntimeException("FragmentMainPlayerControllerBinding == null")

    private val viewModel: SharedViewModel by activityViewModels()

    private var isProgressTouched = false


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
            viewModel.playOrPauseCurrentSong()
        }

        binding.controllerBtnStop.setOnClickListener {
            viewModel.stopCurrentSong()
        }

        setupSeekbar()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.progressFlow.collect {
//                    Log.d(TAG, "onViewCreated: progress = ${it.currentPositionSec}")
//                    Log.d(TAG, "onViewCreated: progress = ${it.currentPositionSting}")

                    with(binding) {
                        if (!isProgressTouched) {
                            with(controllerSeekBar) { post { progress = it.currentPositionSec } }
                        }
                        controllerTvCurrentTime.text = it.currentPositionSting
                        controllerTvTotalTime.text = it.durationString
                        controllerSeekBar.max = it.durationSec
                    }

                }
            }
        }
    }

    private fun setupSeekbar() {
        val seekAdapter = object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                binding.controllerTvCurrentTime.text = StringFormatter().formatSecToString(progress)
            }

            override fun onStartTrackingTouch(seek: SeekBar) { isProgressTouched = true }

            override fun onStopTrackingTouch(seek: SeekBar) {
//                Log.d(TAG, "setupSeekbar: currentProgress = ${seek.progress}")
                viewModel.setSongProgress(seek.progress)
                isProgressTouched = false
            }
        }

        binding.controllerSeekBar.setOnSeekBarChangeListener(seekAdapter)
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