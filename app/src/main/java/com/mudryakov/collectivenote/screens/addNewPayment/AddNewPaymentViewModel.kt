package com.mudryakov.collectivenote.screens.addNewPayment

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mudryakov.collectivenote.MainActivity
import com.mudryakov.collectivenote.database.firebase.CURRENT_UID
import com.mudryakov.collectivenote.database.firebase.FireBaseRepository

import com.mudryakov.collectivenote.models.PaymentModel
import com.mudryakov.collectivenote.utility.AppPreference
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddNewPaymentViewModel @Inject constructor(application: Application, private val fireBaseRepository: FireBaseRepository) : AndroidViewModel(application) {

    fun addNewPayment(sum: String, description: String, imageUri: Uri?, onSucces: () -> Unit) =
        viewModelScope.launch(IO) {
            val currentPayment =
                PaymentModel(summ = sum, description = description, fromId = CURRENT_UID,fromName = AppPreference.getUserName())
            if (imageUri == null) {
                fireBaseRepository.addNewPayment(currentPayment) { onSucces() }
            } else {
                fireBaseRepository.pushFileToBase(imageUri) {
                    currentPayment.imageUrl = it
                    fireBaseRepository.addNewPayment(currentPayment) { onSucces()}
                }
            }
        }
}