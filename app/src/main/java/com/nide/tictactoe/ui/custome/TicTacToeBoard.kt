package com.nide.tictactoe.ui.custome

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.media.MediaPlayer
import android.os.Parcelable
import android.text.BoringLayout
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.nide.tictactoe.R
import com.nide.tictactoe.logic.TicTacToeGame
import com.nide.tictactoe.util.SaveUtil
import com.nide.tictactoe.util.gameSaveData
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.ceil

class TicTacToeBoard @JvmOverloads constructor(
    ctx: Context,
    attr: AttributeSet,
    defStyle: Int = 0
) : View(ctx, attr, defStyle) {

    private val TAG = javaClass.simpleName

    private var boardColor: Int
    private var xColor: Int
    private var oColor: Int
    private var crossLineColor: Int
    private val fillPaint = Paint()
    private val strokePaint = Paint()
    private var cellSize = width / 3
    private var cellGape = 50F
    private var cornerRadius = 50F
    private var game = TicTacToeGame()
    private var onPlayerChangeListener: ((Int) -> Unit)? = null
    private var player = MediaPlayer()

    init {
        val attset = ctx.theme.obtainStyledAttributes(attr, R.styleable.TicTacToeBoard, 0, 0)
        xColor = 2
        boardColor = 0
        oColor = 0
        crossLineColor = 0
        try {
            xColor = attset.getInteger(R.styleable.TicTacToeBoard_xColor, 0)
            boardColor = attset.getInteger(R.styleable.TicTacToeBoard_boardColor, 0)
            oColor = attset.getInteger(R.styleable.TicTacToeBoard_oColor, 0)
            crossLineColor = attset.getInteger(R.styleable.TicTacToeBoard_crossLineColor, 0)
            cellGape = attset.getDimension(R.styleable.TicTacToeBoard_celeGape, 50F)
            cornerRadius = attset.getDimension(R.styleable.TicTacToeBoard_cornerRadius, 50F)
        } finally {
            attset.recycle()
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val dimension = measuredHeight.coerceAtMost(measuredWidth)
        cellSize = dimension / 3
        setMeasuredDimension(dimension, dimension)
        findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
            loadData()
        }
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        fillPaint.style = Paint.Style.FILL
        fillPaint.isAntiAlias = true
        strokePaint.style = Paint.Style.STROKE
        strokePaint.isAntiAlias = true
        strokePaint.strokeWidth = cellGape

        if (canvas != null) {
            test()
            drawBoard(canvas)
            drawMarkers(canvas)
            if (game.winner == 1 || game.winner == 2) {
                drawCrossLine(canvas)
                game.isGamePlay = false
                player.reset()
                player = MediaPlayer.create(context, R.raw.win_sound)
                player.setVolume(0.3F, 0.3F)
                player.start()
            }
        }
    }

    private fun drawMarkers(canvas: Canvas) {

        for (r in 0 until game.gameBoard.size) {
            for (c in 0 until 3) {
                if (game.gameBoard[r][c] != 0) {
                    if (game.gameBoard[r][c] == 1) {
                        drawX(canvas, r, c)
                    } else {
                        drawO(canvas, r, c)
                    }
                }
            }
        }


    }

    private fun drawBoard(canvas: Canvas) {
        fillPaint.color = boardColor
        fillPaint.strokeWidth = 16F


        for (col in 0..2) {

            for (row in 0..2) {
                canvas.drawRoundRect(
                    (cellSize * row).toFloat() + cellGape,
                    (cellSize * col).toFloat() + cellGape,
                    (cellSize * (row + 1)).toFloat() - cellGape,
                    (cellSize * (col + 1)).toFloat() - cellGape,
                    cornerRadius,
                    cornerRadius,
                    fillPaint
                )
            }


        }


    }

    private fun drawX(canvas: Canvas, row: Int, col: Int) {
        strokePaint.color = xColor

        canvas.drawLine(
            ((col + 1) * cellSize - cellGape * 3),
            (row * cellSize + cellGape * 3),
            (col * cellSize + cellGape * 3),
            ((row + 1) * cellSize - cellGape * 3),
            strokePaint
        )

        canvas.drawLine(
            (col * cellSize + cellGape * 3),
            (row * cellSize + cellGape * 3),
            ((col + 1) * cellSize - cellGape * 3),
            ((row + 1) * cellSize - cellGape * 3),
            strokePaint
        )

    }

    private fun drawO(canvas: Canvas, row: Int, col: Int) {
        strokePaint.color = oColor

        canvas.drawOval(
            (col * cellSize + cellGape * 3),
            (row * cellSize + cellGape * 3),
            ((col * cellSize + cellSize) - cellGape * 3),
            ((row * cellSize + cellSize) - cellGape * 3),
            strokePaint
        )


    }

    private fun drawHorizontalLine(canvas: Canvas, row: Int, col: Int) {
        strokePaint.color = crossLineColor
        canvas.drawLine(
            col.toFloat(),
            (row * cellSize + cellSize / 2).toFloat(),
            (cellSize * 3).toFloat(),
            (row * cellSize + cellSize / 2).toFloat(),
            strokePaint
        )
    }

    private fun drawVerticalLine(canvas: Canvas, row: Int, col: Int) {
        strokePaint.color = crossLineColor
        canvas.drawLine(
            (col * cellSize + cellSize / 2).toFloat(),
            row.toFloat(),
            (col * cellSize + cellSize / 2).toFloat(),
            (cellSize * 3).toFloat(),
            strokePaint
        )
    }

    private fun drawDig(canvas: Canvas) {
        strokePaint.color = crossLineColor
        canvas.drawLine(
            0F,
            (cellSize * 3).toFloat(),
            (cellSize * 3).toFloat(),
            0F,
            strokePaint
        )
    }

    private fun drawRevDig(canvas: Canvas) {
        strokePaint.color = crossLineColor
        canvas.drawLine(
            0F,
            0F,
            (cellSize * 3).toFloat(),
            (cellSize * 3).toFloat(),
            strokePaint
        )
    }

    private fun drawCrossLine(canvas: Canvas) {
        val row = game.winType[0]
        val col = game.winType[1]
        when (game.winType[2]) {
            1 -> drawHorizontalLine(canvas, row, col)
            2 -> drawVerticalLine(canvas, row, col)
            3 -> drawRevDig(canvas)
            4 -> drawDig(canvas)
        }
    }

    fun resetGame() {
        game.resetGame()
        player.reset()
        player = MediaPlayer.create(context, R.raw.sound_reset)
        player.setVolume(0.3F, 0.3F)
        player.start()
        onPlayerChangeListener?.invoke(game.player)
        invalidate()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            val x = it.x
            val y = it.y

            val action = it.action

            if (action == MotionEvent.ACTION_DOWN) {
                val row = ceil((y / cellSize).toDouble())
                val col = ceil((x / cellSize).toDouble())

                if (game.updateGame(row.toInt(), col.toInt())) {
                    player.reset()
                    player = MediaPlayer.create(context, R.raw.sound_move)
                    player.setVolume(0.3F, 0.3F)
                    player.start()
                    invalidate()
                    if (game.player % 2 == 0) {
                        game.player--
                    } else {
                        game.player++
                    }
                    onPlayerChangeListener?.invoke(game.player)
                } else {
                    player.reset()
                    player = MediaPlayer.create(context, R.raw.sound_error)
                    player.setVolume(0.3F, 0.3F)
                    player.start()
                }

                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)

    }

    fun registerOnWinCallBack(callback: (Int) -> Unit) {
        game.onWinCallBck = callback
    }

    fun registerOnPlayerChangeListener(callback: (Int) -> Unit) {
        onPlayerChangeListener = callback
    }

    fun onGameScoreChangeListener(callback: (IntArray) -> Unit) {
        game.onGameScoreUp = callback
    }


    fun test() {
    /*    game.gameBoard.forEach {
            Log.i(TAG, "test: ${it.contentToString()}")
        }
        Log.i(TAG, "test: full ${game.gameBoard.contentDeepToString()}")

        val g = Gson()
        var s = g.toJson(game.gameBoard)

        Log.i(TAG, "test: gson $s")
        val x = g.fromJson(s, Array(3) { IntArray(3) }.javaClass)
        Log.i(TAG, "test: retive ${x.contentDeepToString()}")*/
    }

    private suspend fun loadData() {
        val isGameSave = context.gameSaveData.data.map { pref ->
            pref[SaveUtil.isGameSave_key] ?: false
        }

        isGameSave.collect{
            Log.i(TAG, "loadData: state $it")
            if (it) {
                val data = context.gameSaveData.data.map { pref ->
                    pref[SaveUtil.gameData_key] ?: ""
                }
                data.collectLatest { matrix ->
                    if (matrix.isNotEmpty()) {
                        val gson = Gson()
                        try {
                            val boardData =
                                gson.fromJson(matrix, Array(3) { IntArray(3) }.javaClass)
                            game.gameBoard = boardData
                        } catch (e: Exception) {
                            Log.e(TAG, "loadData: $e")
                        }finally {
                            saveGame(false)
                        }
                    }
                }
            }
        }

    }

    suspend fun saveGame(state: Boolean) {
        context.gameSaveData.edit { setting ->
            setting[SaveUtil.isGameSave_key] = state
            val gson = Gson()
            val data = gson.toJson(game.gameBoard)
            Log.i(TAG, "saveGame: $data")
            setting[SaveUtil.gameData_key] = data
        }
    }


}










