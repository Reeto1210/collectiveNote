package com.mudryakov.collectivenote.screens.roomChoose

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mudryakov.collectivenote.database.firebase.REPOSITORY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RoomChooseViewModel(application: Application) : AndroidViewModel(application) {

    fun createRoom(name: String, pass: String,currencySign:String, onFail:()->Unit,onSuccess: () -> Unit)= viewModelScope.launch(
        Dispatchers.IO
    ) {
        REPOSITORY.createNewRoom(name, pass,currencySign, onFail,onSuccess)
    }

    fun joinRoom(name: String, pass: String, onFail:()->Unit,onSuccess: () -> Unit )= viewModelScope.launch(
        Dispatchers.IO
    ) {
        REPOSITORY.joinRoom(name, pass, onFail,onSuccess)
    }
}