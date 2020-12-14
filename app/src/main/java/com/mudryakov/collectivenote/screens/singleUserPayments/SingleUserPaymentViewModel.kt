package com.mudryakov.collectivenote.screens.singleUserPayments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mudryakov.collectivenote.database.firebase.CURRENT_GROUP_UID
import com.mudryakov.collectivenote.database.firebase.CURRENT_UID
import com.mudryakov.collectivenote.database.firebase.REPOSITORY
import com.mudryakov.collectivenote.database.firebase.ROOM_REPOSITORY
import com.mudryakov.collectivenote.models.PaymentModel
import com.mudryakov.collectivenote.models.UserModel
import com.mudryakov.collectivenote.utility.AppPreference
import kotlinx.coroutines.launch

class SingleUserPaymentViewModel(application: Application) : AndroidViewModel(application) {
    val singleUserPayments = REPOSITORY.allPayments

    fun deletePayment(payment: PaymentModel, onSuccess: () -> Unit) {
        REPOSITORY.deletePayment(payment) {
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