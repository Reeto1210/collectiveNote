package com.mudryakov.collectivenote.screens.roomChoose

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mudryakov.collectivenote.database.firebase.REPOSITORY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RoomChooseViewModel(application: Application) : AndroidViewModel(application) {

    fun createRoom(name: String, pass: String, function: () -> Unit)= viewModelScope.launch(
        Dispatchers.IO
    ) {
        REPOSITORY.createNewRoom(name, pass, function)
    }

    fun joinRoom(name: String, pass: String, function: () -> Unit)= viewModelScope.launch(
        Dispatchers.IO
    ) {
        REPOSITORY.joinRoom(name, pass, function)
    }
}