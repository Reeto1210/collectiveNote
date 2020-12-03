package com.mudryakov.collectivenote.screens.addNewPayment

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mudryakov.collectivenote.database.firebase.CURRENT_UID
import com.mudryakov.collectivenote.database.firebase.REPOSITORY
import com.mudryakov.collectivenote.models.PaymentModel
import com.mudryakov.collectivenote.utilits.AppPreference
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class AddNewPaymentViewModel(application: Application) : AndroidViewModel(application) {

    fun addNewPayment(sum: String, description: String, imageUri: Uri?, onSucces: () -> Unit) =
        viewModelScope.launch(IO) {
            val currentPayment =
                PaymentModel(summ = sum, description = description, fromId = CURRENT_UID,fromName = AppPreference.getUserName())
            if (imageUri == null) {
                REPOSITORY.addNewPayment(currentPayment) { onSucces() }
            } else {
                REPOSITORY.pushFileToBase(imageUri) {
                    currentPayment.imageUrl = it
                    REPOSITORY.addNewPayment(currentPayment) { onSucces()}
                }
            }
        }
}