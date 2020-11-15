package com.mudryakov.collectivenote.database

import android.net.Uri
import androidx.lifecycle.LiveData
import com.mudryakov.collectivenote.models.PaymentModel

interface AppDatabaseRepository {
   val allPayments:LiveData<List<PaymentModel>>
    fun connectToDatabase(type: String, onSucces: () -> Unit)
    fun createNewRoom(roomName: String, roomPass: String, onSucces: () -> Unit)
    fun joinRoom(roomName: String, roomPass: String, onSucces: () -> Unit)

    fun addNewPayment(payment: PaymentModel, onSucces: () -> Unit)
    fun changeName(name: String, onSucces: () -> Unit)
    fun addnewQuest(onSucces: () -> Unit)
    fun getQuest(onSucces: () -> Unit)
    fun pushFileToBase(imageUri: Uri, onSucces: (String) -> Unit)


}

