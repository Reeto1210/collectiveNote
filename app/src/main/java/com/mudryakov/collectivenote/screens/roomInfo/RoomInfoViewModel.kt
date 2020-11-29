package com.mudryakov.collectivenote.screens.roomInfo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.mudryakov.collectivenote.database.firebase.REPOSITORY

class RoomInfoViewModel(application: Application) : AndroidViewModel(application) {
    val allPayments = REPOSITORY.allPayments
    val allMembers = REPOSITORY.groupMembers
    fun remindRoomPassword(onSuccess: (String) -> Unit) {
        REPOSITORY.remindPassword(onSuccess)
    }
}