package com.mudryakov.collectivenote.screens.singleUserPayments

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.mudryakov.collectivenote.database.firebase.REPOSITORY
import com.mudryakov.collectivenote.database.firebase.ROOM_REPOSITORY
import com.mudryakov.collectivenote.models.PaymentModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class SingleUserPaymentViewModel(application: Application) : AndroidViewModel(application) {
    val singleUserPayments = REPOSITORY.allPayments

    fun deletePayment(payment: PaymentModel, onSuccess: () -> Unit) {
       CoroutineScope(IO).launch {
        ROOM_REPOSITORY.deleteCurrentPayment(payment)}
        REPOSITORY.deletePayment(payment) { onSuccess() }
    }

}