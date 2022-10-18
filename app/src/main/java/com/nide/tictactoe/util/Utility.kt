package com.nide.tictactoe.util

import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest


fun <T> Fragment.collectFlow(flow: Flow<T>, callback: suspend (T) -> Unit) {
    lifecycleScope.launchWhenStarted {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collectLatest {
                callback(it)
            }
        }
    }
}


fun EditText.validateName(): Boolean {
    return if (this.text.toString().trim().isEmpty()) {
        error = "Please write a name "
        false
    } else {
        error = null
        true
    }
}

