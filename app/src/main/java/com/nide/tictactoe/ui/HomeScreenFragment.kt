package com.nide.tictactoe.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.nide.tictactoe.R
import com.nide.tictactoe.databinding.FragmentHomeScreenBinding
import com.nide.tictactoe.util.SaveUtil
import com.nide.tictactoe.util.gameSaveData
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map


class HomeScreenFragment : Fragment() {
    private var _binding: FragmentHomeScreenBinding? = null
    private val binding get() = _binding!!


    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeScreenBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnPlay.setOnClickListener {
           checkSaveGame()
        }
    }

    fun checkSaveGame() {
        lifecycleScope.launchWhenStarted {
            try {
                val load = requireContext().gameSaveData.data.map { pref ->
                    pref[SaveUtil.isGameSave_key] ?: false
                }
                load.collectLatest {
                    if (it) {
                        val player1 = requireContext().gameSaveData.data.map { pref ->
                            pref[SaveUtil.player1_key] ?: "unknown"
                        }
                        val player2 = requireContext().gameSaveData.data.map { pref ->
                            pref[SaveUtil.player2_key] ?: "unknown"
                        }

                        player1.combine(player2){ p1, p2 ->
                            val action =
                                HomeScreenFragmentDirections.actionHomeScreenFragmentToGameFragment2(
                                    p1,
                                    p2
                                )
                            findNavController().navigate(action)
                        }.collect()

                    } else {
                        findNavController().navigate(R.id.action_homeScreenFragment_to_playerNameFragment)
                    }
                }


            } catch (e: Exception) {
                findNavController().navigate(R.id.action_homeScreenFragment_to_playerNameFragment)
            }
        }
    }


}