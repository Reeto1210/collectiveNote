package com.mudryakov.collectivenote.database

import android.net.Uri
import androidx.lifecycle.LiveData
import com.mudryakov.collectivenote.models.PaymentModel
import com.mudryakov.collectivenote.models.UserModel

interface AppDatabaseRepository {

    val allPayments: LiveData<List<PaymentModel>>
    val groupMembers: LiveData<List<UserModel>>

    fun deletePayment(payment: PaymentModel, onSuccess: () -> Unit)
fun changeName(name:String,onSuccess: () -> Unit)

    fun createNewGroup(
        groupName: String,
        groupPass: String,
        currencySign: String,
        onFail: () -> Unit,
        onSuccess: () -> Unit
    )

    fun joinGroup(groupName: String, groupPass: String, onFail: () -> Unit, onSuccess: () -> Unit)
    fun addNewPayment(payment: PaymentModel, onSuccess: () -> Unit)

    fun pushFileToBase(imageUri: Uri, onSuccess: (String) -> Unit)
    fun remindPassword(onSuccess: (String) -> Unit)
    fun getGroupCurrencyFromDB()
   }

