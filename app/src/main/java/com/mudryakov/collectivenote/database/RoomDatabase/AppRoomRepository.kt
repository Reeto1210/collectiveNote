package com.mudryakov.collectivenote.database.RoomDatabase

import android.net.Uri
import androidx.lifecycle.LiveData
import com.mudryakov.collectivenote.database.AppDatabaseRepository
import com.mudryakov.collectivenote.models.PaymentModel
import com.mudryakov.collectivenote.models.UserModel
import java.lang.reflect.Member

class AppRoomRepository(private val dao: myDao):AppDatabaseRepository {



    suspend fun updateAllPaymentsRoomDatabase(list: List<PaymentModel>) = dao.updateAllPayments(list)
    suspend fun updateAllUsersRoomDatabase(list: List<UserModel>) = dao.updateAllUsers(list)

    suspend fun updateUser(user: UserModel) = dao.updateUser(user)

    override val allPayments: LiveData<List<PaymentModel>>
        get() =dao.getAllPayments()

    override val groupMembers: LiveData<List<UserModel>>
        get() = dao.getAllUsers()

    override fun deletePayment(payment:PaymentModel, onSuccess: () -> Unit) {}

    override fun login(type: String, onFail: () -> Unit, onSuccess: () -> Unit) {}

    override fun emailRegistration(onFail: () -> Unit, onSuccess: () -> Unit) {
          }

    override fun createNewGroup(
        groupName: String,
        groupPass: String,
        currencySign: String,
        onFail: () -> Unit,
        onSuccess: () -> Unit
    ) {}

    override fun joinGroup(
        groupName: String,
        groupPass: String,
        onFail: () -> Unit,
        onSuccess: () -> Unit
    ) {}

    override fun addNewPayment(payment: PaymentModel, onSuccess: () -> Unit) {}

    override fun changeName(name: String, onSuccess: () -> Unit) {}

    override fun pushFileToBase(imageUri: Uri, onSuccess: (String) -> Unit) {}

    override fun signOut() {}

    override fun remindPassword(onSuccess: (String) -> Unit) { }


}