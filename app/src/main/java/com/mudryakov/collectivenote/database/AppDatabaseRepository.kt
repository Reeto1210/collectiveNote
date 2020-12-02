package com.mudryakov.collectivenote.database

import android.net.Uri
import androidx.lifecycle.LiveData
import com.mudryakov.collectivenote.models.PaymentModel
import com.mudryakov.collectivenote.models.UserModel

interface AppDatabaseRepository {

    val allPayments:LiveData<List<PaymentModel>>
    val groupMembers:LiveData<List<UserModel>>
    fun login(type: String, onFail:()->Unit, onSucces: () -> Unit)
    fun emailRegistration(onFail: () -> Unit, onSucces: () -> Unit,)

    fun createNewRoom(roomName: String, roomPass: String, onFail: () -> Unit, onSucces: () -> Unit)
    fun joinRoom(roomName: String, roomPass: String, onFail: () -> Unit, onSucces: () -> Unit)
    fun addNewPayment(payment: PaymentModel, onSucces: () -> Unit)
    fun changeName(name: String, onSucces: () -> Unit)

    fun pushFileToBase(imageUri: Uri, onSucces: (String) -> Unit)
    fun signOut()
fun remindPassword(onSuccess: (String) -> Unit)

}

