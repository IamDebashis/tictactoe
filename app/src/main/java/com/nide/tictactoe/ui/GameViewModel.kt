package com.nide.tictactoe.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    private val playerCode = MutableStateFlow<Int>(1)
    val currentPlayer = playerCode.asStateFlow()

    fun setPlayer(player : Int) = viewModelScope.launch{
        delay(300)
        playerCode.emit(player)
    }


}