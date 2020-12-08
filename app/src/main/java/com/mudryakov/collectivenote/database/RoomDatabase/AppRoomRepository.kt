package com.mudryakov.collectivenote.database.RoomDatabase

import android.net.Uri
import androidx.lifecycle.LiveData
import com.mudryakov.collectivenote.database.AppDatabaseRepository
import com.mudryakov.collectivenote.models.PaymentModel
import com.mudryakov.collectivenote.models.UserModel

class AppRoomRepository(private val dao: myDao):AppDatabaseRepository {



    suspend fun updateAllPaymentsRoomDatabase(list: List<PaymentModel>) = dao.updateAllPayments(list)
    suspend fun updateAllUsersRoomDatabase(list: List<UserModel>) = dao.updateAllUsers(list)

    override val allPayments: LiveData<List<PaymentModel>>
        get() =dao.getAllPayments()

    override val groupMembers: LiveData<List<UserModel>>
        get() = dao.getAllUsers()

    override fun deletePayment(payment:PaymentModel, onSuccess: () -> Unit) {}

    override fun login(type: String, onFail: () -> Unit, onSuccess: () -> Unit) {}

    override fun emailRegistration(onFail: () -> Unit, onSuccess: () -> Unit) {
          }

    override fun createNewRoom(
        roomName: String,
        roomPass: String,
        currencySign: String,
        onFail: () -> Unit,
        onSuccess: () -> Unit
    ) {}

    override fun joinRoom(
        roomName: String,
        roomPass: String,
        onFail: () -> Unit,
        onSuccess: () -> Unit
    ) {}

    override fun addNewPayment(payment: PaymentModel, onSuccess: () -> Unit) {}

    override fun changeName(name: String, onSuccess: () -> Unit) {}

    override fun pushFileToBase(imageUri: Uri, onSuccess: (String) -> Unit) {}

    override fun signOut() {}

    override fun remindPassword(onSuccess: (String) -> Unit) { }

}