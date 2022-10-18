package com.nide.tictactoe.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.nide.tictactoe.util.SaveUtil.game_data


val Context.gameSaveData: DataStore<Preferences> by preferencesDataStore(name = game_data)

object SaveUtil {
    val game_data = "tic_tac_toe_game"

    private val saveGameData = "save_game_data"
    private val saveGameStatus = "save_game_status"
    val isGameSave_key = booleanPreferencesKey(saveGameStatus)
    val gameData_key = stringPreferencesKey(saveGameData)

}