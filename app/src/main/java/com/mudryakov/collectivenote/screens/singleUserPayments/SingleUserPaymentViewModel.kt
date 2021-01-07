package com.mudryakov.collectivenote.screens.singleUserPayments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mudryakov.collectivenote.database.RoomDatabase.AppRoomRepository
import com.mudryakov.collectivenote.database.firebase.CURRENT_GROUP_UID
import com.mudryakov.collectivenote.database.firebase.CURRENT_UID
import com.mudryakov.collectivenote.database.firebase.FireBaseRepository
import com.mudryakov.collectivenote.database.firebase.ROOM_REPOSITORY
import com.mudryakov.collectivenote.models.PaymentModel
import com.mudryakov.collectivenote.models.UserModel
import com.mudryakov.collectivenote.utility.APP_ACTIVITY
import com.mudryakov.collectivenote.utility.AppPreference
import kotlinx.coroutines.launch
import javax.inject.Inject

class SingleUserPaymentViewModel @Inject constructor(application: Application, private val fireBaseRepository: FireBaseRepository,
roomRepository: AppRoomRepository):AndroidViewModel(application) {


    val singleUserPayments = if (APP_ACTIVITY.internet) fireBaseRepository.allPayments else roomRepository.allPayments

    fun deletePayment(payment: PaymentModel, onSuccess: () -> Unit) {
        fireBaseRepository.deletePayment(payment) {
            onSuccess()
            updateUserInRoom()
        }
    }

    private fun updateUserInRoom() = viewModelScope.launch {
        val curUser = UserModel(
            CURRENT_UID,
            AppPreference.getUserName(),
            CURRENT_GROUP_UID,
            AppPreference.getTotalSumm()
        )
        ROOM_REPOSITORY.updateUser(curUser)
    }

}