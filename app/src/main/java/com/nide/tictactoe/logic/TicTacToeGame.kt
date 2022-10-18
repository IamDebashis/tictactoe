package com.nide.tictactoe.logic

import android.util.Log

class TicTacToeGame {

    var gameBoard = Array(3) { IntArray(3) { 0 } }
    var player: Int = 1
    private var gameScore = IntArray(2) { 0 }
    var winner = -1
        private set
    var isGamePlay = true

    /**
     * determine the win type weather its a row win , or col win , or dig win or revDig win
     * inside this the 0 index represent the row
     * 1 index represent the col
     * 2 index represent the type of win
     * */
    var winType = IntArray(3) { -1 }


    var onWinCallBck: ((Int) -> Unit)? = null
    var onGameScoreUp: ((IntArray) -> Unit)? = null

    fun updateGame(row: Int, col: Int): Boolean {
        return if (gameBoard[row - 1][col - 1] == 0 && isGamePlay) {
            gameBoard[row - 1][col - 1] = player
            winner = checkWin(player, row, col)
            if (winner != -1) {
                onWinCallBck?.invoke(winner)
                false
            } else {
                true
            }

        } else {
            false
        }
    }


    /**
     *  Check if in the move is there any winner
     *  @param player is either 1 or 2
     *  @param row is move row index
     *  @param col is move col index
     *   @return return the player index of the winner 1 or 2, otherwise if draw return 0 else return -1
     * */

    private fun checkWin(player: Int, row: Int, col: Int): Int {
        var winRow = true
        var winCol = true
        var winDig = true
        var winRevDig = true
        for (i in gameBoard.indices) {

            if (gameBoard[row - 1][i] != player) {
                winRow = false
            }
            if (gameBoard[i][col - 1] != player) {
                winCol = false
            }
            if (gameBoard[i][i] != player) {
                winDig = false
            }

            if (gameBoard[i][gameBoard.size - 1 - i] != player) {
                winRevDig = false
            }
        }

        if (winRow || winCol || winDig || winRevDig) {
            gameScore[if (player == 1) 0 else 1]++
            winType =
                intArrayOf(row-1, col-1, if (winRow) 1 else if (winCol) 2 else if (winDig) 3 else 4)
            onGameScoreUp?.invoke(gameScore)
            return player
        } else {
            var draw =0
            for (i in gameBoard.indices) {
                for (j in 0 until 3) {
                    if (gameBoard[i][j] == 0) {
                        draw++
                        break
                    }
                }
            }
            Log.i("TicTac", "checkWin: $draw")
            return if (draw == 0 )
                0
            else -1
        }

    }

    fun resetGame() {
        gameBoard = Array(3) { IntArray(3) { 0 } }
        winType = IntArray(3) { -1 }
        isGamePlay = true
        winner =-1
        player = 1
    }


}