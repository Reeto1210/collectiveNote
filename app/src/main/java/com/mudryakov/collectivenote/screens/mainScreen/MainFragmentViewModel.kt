package com.mudryakov.collectivenote.screens.mainScreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

import com.mudryakov.collectivenote.database.firebase.REPOSITORY

class MainFragmentViewModel(application: Application):AndroidViewModel(application) {
    val allMembers = REPOSITORY.groupMembers
fun pushallPaymentsToRoom(){
    viewModelScope.launch{}
}
}
