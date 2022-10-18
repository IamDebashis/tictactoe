package com.nide.tictactoe.ui

import android.app.Dialog
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nide.tictactoe.R
import com.nide.tictactoe.databinding.FragmentGameBinding
import com.nide.tictactoe.util.collectFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit


class GameFragment : Fragment() {
    private val TAG = javaClass.simpleName
    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<GameViewModel>()

    private val args: GameFragmentArgs by navArgs()
    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                lifecycleScope.launchWhenStarted {
                    leaveDialog()
                }
            }
        })

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
        val dialog = Dialog(requireContext(), R.style.dialoge)
        dialog.setContentView(R.layout.draw_dialouge_layout)
        val btnHome = dialog.findViewById<Button>(R.id.btn_home)
        val btnReplay = dialog.findViewById<Button>(R.id.btn_play)
        dialog.setCancelable(false)
        btnHome.setOnClickListener {
            dialog.dismiss()
            findNavController().navigate(R.id.action_gameFragment_to_homeScreenFragment)
        }
        btnReplay.setOnClickListener {
            dialog.dismiss()
            binding.btnRestart.callOnClick()
        }

        dialog.show()

    }

    private suspend fun winDialog(name: String) {
        delay(600)
        val dialog = Dialog(requireContext(), R.style.dialoge)
        dialog.setContentView(R.layout.win_dialog_layout)
        dialog.setCancelable(false)
        val btnHome = dialog.findViewById<Button>(R.id.btn_home)
        val btnReplay = dialog.findViewById<Button>(R.id.btn_play)
        val playerName = dialog.findViewById<TextView>(R.id.tv_player_name)

        playerName.text = name
        btnHome.setOnClickListener {
            dialog.dismiss()
            findNavController().navigate(R.id.action_gameFragment_to_homeScreenFragment)
        }
        btnReplay.setOnClickListener {
            dialog.dismiss()
            binding.btnRestart.callOnClick()
        }
        dialog.show()
    }

    private suspend fun leaveDialog() {
        delay(300)
        val dialog = Dialog(requireContext(), R.style.dialoge)
        dialog.setContentView(R.layout.leave_game_dialog_layout)
        dialog.setCancelable(false)
        val btnHome = dialog.findViewById<Button>(R.id.btn_home)
        val posetiveBtn = dialog.findViewById<Button>(R.id.btn_yes)
        val closeBtn = dialog.findViewById<ImageView>(R.id.iv_close)
        btnHome.setOnClickListener {
            findNavController().navigate(R.id.action_gameFragment_to_homeScreenFragment)
            dialog.dismiss()
        }
        posetiveBtn.setOnClickListener {
            runBlocking {
                binding.ticTacToeBoard.saveGame(true, args.player1, args.player2)
            }
            findNavController().navigate(R.id.action_gameFragment_to_homeScreenFragment)
            dialog.dismiss()
        }

        closeBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
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