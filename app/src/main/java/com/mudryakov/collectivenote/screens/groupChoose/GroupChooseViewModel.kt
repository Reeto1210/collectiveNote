package com.mudryakov.collectivenote.screens.groupChoose

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mudryakov.collectivenote.database.firebase.REPOSITORY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GroupChooseViewModel(application: Application) : AndroidViewModel(application) {

    fun createGroup(name: String, pass: String, currencySign:String, onFail:()->Unit, onSuccess: () -> Unit)= viewModelScope.launch(
        Dispatchers.IO
    ) {
        REPOSITORY.createNewGroup(name, pass,currencySign, onFail,onSuccess)
    }

    fun joinGroup(name: String, pass: String, onFail:()->Unit, onSuccess: () -> Unit )= viewModelScope.launch(
        Dispatchers.IO
    ) {
        REPOSITORY.joinGroup(name, pass, onFail,onSuccess)
    }
}