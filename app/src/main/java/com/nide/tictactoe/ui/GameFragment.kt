package com.nide.tictactoe.ui

import android.app.Dialog
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import com.nide.tictactoe.R
import com.nide.tictactoe.databinding.DrawDialougeLayoutBinding
import com.nide.tictactoe.databinding.FragmentGameBinding
import com.nide.tictactoe.util.collectFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.util.concurrent.TimeUnit


class GameFragment : Fragment() {
    private val TAG = javaClass.simpleName
    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<GameViewModel>()

    private val args: GameFragmentArgs by navArgs()
    val player = MediaPlayer()
    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        initViews()
        initClicks()
    }

    private fun initClicks() = binding.apply {
        btnRestart.setOnClickListener {
            ivStatus.setImageResource(R.drawable.clock)
            tvWinOrPlay.text = "Play"
            binding.ticTacToeBoard.resetGame()
        }

        btnLeave.setOnClickListener {
            lifecycleScope.launchWhenStarted {
                leaveDialog()
            }
        }

        ticTacToeBoard.registerOnWinCallBack {
            when (it) {
                0 -> {
                    val player = MediaPlayer.create(context, R.raw.draw_sound)
                    player.setVolume(0.3F, 0.3F)
                    player.start()
                    lifecycleScope.launchWhenStarted {
                        drawDialoug()
                    }
                }
                1 -> {
                    ivStatus.setImageResource(R.drawable.trophy)
                    tvWinOrPlay.text = "win"
                    tvCurrentPlayer.text = args.player1
                    lifecycleScope.launchWhenStarted {
                        winDialog(args.player1)
                    }

                    Party(
                        speed = 0f,
                        maxSpeed = 30f,
                        damping = 0.9f,
                        spread = 360,
                        colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                        emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
                        position = Position.Relative(0.5, 0.3)
                    ).apply {
                        konfettiView.start(this)
                    }

                }
                2 -> {
                    ivStatus.setImageResource(R.drawable.trophy)
                    tvWinOrPlay.text = "win"
                    tvCurrentPlayer.text = args.player2
                    lifecycleScope.launchWhenStarted {
                        winDialog(args.player2)
                    }
                    Party(
                        speed = -5f,
                        maxSpeed = 30f,
                        damping = 0.9f,
                        spread = 360,
                        colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                        emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
                        position = Position.Relative(0.5, 0.3)
                    ).apply {
                        konfettiView.start(this)
                    }

                }
            }

        }

        ticTacToeBoard.registerOnPlayerChangeListener {
            viewModel.setPlayer(it)
        }

    }

    private suspend fun drawDialoug() {
        delay(600)
        val dialouge = Dialog(requireContext(), R.style.dialoge)
        dialouge.setContentView(R.layout.draw_dialouge_layout)
        dialouge.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val btnHome = dialouge.findViewById<Button>(R.id.btn_home)
        val btn_rePlay = dialouge.findViewById<Button>(R.id.btn_play)
        dialouge.setCancelable(false)
        btnHome.setOnClickListener {
            dialouge.dismiss()
            findNavController().navigate(R.id.action_gameFragment_to_homeScreenFragment)
        }
        btn_rePlay.setOnClickListener {
            dialouge.dismiss()
            binding.btnRestart.callOnClick()
        }

        dialouge.show()

    }

    private suspend fun winDialog(name: String) {
        delay(600)
        val dialouge = Dialog(requireContext(), R.style.dialoge)
        dialouge.setContentView(R.layout.win_dialog_layout)
        dialouge.setCancelable(false)
        val btnHome = dialouge.findViewById<Button>(R.id.btn_home)
        val btn_rePlay = dialouge.findViewById<Button>(R.id.btn_play)
        val playerName = dialouge.findViewById<TextView>(R.id.tv_player_name)


        playerName.text = name
        btnHome.setOnClickListener {
            dialouge.dismiss()
            findNavController().navigate(R.id.action_gameFragment_to_homeScreenFragment)
        }
        btn_rePlay.setOnClickListener {
            dialouge.dismiss()
            binding.btnRestart.callOnClick()
        }

        dialouge.show()

    }

    private suspend fun leaveDialog() {
        delay(300)
        val dialouge = Dialog(requireContext(), R.style.dialoge)
        dialouge.setContentView(R.layout.leave_game_dialog_layout)
        dialouge.setCancelable(false)
        val btnHome = dialouge.findViewById<Button>(R.id.btn_home)
        val posetiveBtn = dialouge.findViewById<Button>(R.id.btn_yes)
        btnHome.setOnClickListener {
            dialouge.dismiss()
            findNavController().navigate(R.id.action_gameFragment_to_homeScreenFragment)
        }
        posetiveBtn.setOnClickListener {
            dialouge.dismiss()
            runBlocking {
                binding.ticTacToeBoard.saveGame(true)
            }
            findNavController().navigate(R.id.action_gameFragment_to_homeScreenFragment)
        }

        dialouge.show()
    }


    private fun initViews() = binding.apply {
        collectFlow(viewModel.currentPlayer) {
            if (it == 1) {
                tvCurrentPlayer.text = args.player1
            } else {
                tvCurrentPlayer.text = args.player2
            }
        }

        tvPlayer1Name.text = args.player1
        tvPlayer2Name.text = args.player2

        ticTacToeBoard.onGameScoreChangeListener {
            tvPlayer1Score.text = it[0].toString()
            tvPlayer2Score.text = it[1].toString()
        }

    }
    


}