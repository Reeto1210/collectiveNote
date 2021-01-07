package com.mudryakov.collectivenote.screens.groupInfo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.mudryakov.collectivenote.MyApplication
import com.mudryakov.collectivenote.database.RoomDatabase.AppRoomRepository
import com.mudryakov.collectivenote.database.firebase.FireBaseRepository
import com.mudryakov.collectivenote.utility.APP_ACTIVITY
import javax.inject.Inject

class GroupInfoViewModel @Inject constructor(
    application: Application,
    private val fireBaseRepository: FireBaseRepository,
    roomRepository: AppRoomRepository
) : AndroidViewModel(application) {
    val allPayments = if (APP_ACTIVITY.internet) fireBaseRepository.allPayments else roomRepository.allPayments
    val allMembers = if (APP_ACTIVITY.internet) fireBaseRepository.groupMembers else roomRepository.groupMembers
    fun remindRoomPassword(onSuccess: (String) -> Unit) {
        fireBaseRepository.remindPassword(onSuccess)
    }
}