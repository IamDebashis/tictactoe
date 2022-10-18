package com.nide.tictactoe.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.nide.tictactoe.R
import com.nide.tictactoe.databinding.FragmentPlayerNameBinding
import com.nide.tictactoe.util.validateName


class PlayerNameFragment : Fragment() {

    private var _binding: FragmentPlayerNameBinding? = null
    private val binding get() = _binding!!


    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPlayerNameBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnStartGame.setOnClickListener {
            if (binding.etPlayer1Name.validateName() && binding.etPlayer2Name.validateName()) {
                val player1 = binding.etPlayer1Name.text.toString()
                val player2 = binding.etPlayer2Name.text.toString()
                val action = PlayerNameFragmentDirections.actionPlayerNameFragmentToGameFragment(
                    player1,
                    player2
                )
                findNavController().navigate(action)
            }
        }

    }

}