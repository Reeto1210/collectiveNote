package com.mudryakov.collectivenote.screens.mainScreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

import com.mudryakov.collectivenote.database.firebase.REPOSITORY
import com.mudryakov.collectivenote.database.firebase.ROOM_REPOSITORY
import com.mudryakov.collectivenote.models.PaymentModel
import kotlinx.coroutines.launch

class MainFragmentViewModel(application: Application) : AndroidViewModel(application) {
    val allMembers = REPOSITORY.groupMembers

}
