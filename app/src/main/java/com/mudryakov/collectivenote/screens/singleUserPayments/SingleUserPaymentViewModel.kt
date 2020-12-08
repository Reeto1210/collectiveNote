package com.mudryakov.collectivenote.screens.singleUserPayments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.mudryakov.collectivenote.database.firebase.REPOSITORY
import com.mudryakov.collectivenote.models.PaymentModel

class SingleUserPaymentViewModel(application: Application) : AndroidViewModel(application) {
    val singleUserPayments = REPOSITORY.allPayments

    fun deletePayment(payment: PaymentModel, onSuccess: () -> Unit) {
        REPOSITORY.deletePayment(payment) { onSuccess() }
    }
}