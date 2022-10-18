package com.nide.tictactoe.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.nide.tictactoe.R
import com.nide.tictactoe.databinding.FragmentHomeScreenBinding


class HomeScreenFragment : Fragment() {
    private var _binding :FragmentHomeScreenBinding?=null
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
        _binding = FragmentHomeScreenBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnPlay.setOnClickListener {
            findNavController().navigate(R.id.action_homeScreenFragment_to_playerNameFragment)
        }
    }
   
}