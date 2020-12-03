package com.mudryakov.collectivenote.database.RoomDatabase

import android.net.Uri
import androidx.lifecycle.LiveData
import com.mudryakov.collectivenote.database.AppDatabaseRepository
import com.mudryakov.collectivenote.models.PaymentModel
import com.mudryakov.collectivenote.models.UserModel
import com.mudryakov.collectivenote.utilits.showToast

class AppRoomRepository(private val dao: myDao):AppDatabaseRepository {

    private val roomAllNotes: LiveData<List<PaymentModel>> get() = dao.getAllPayments()

    suspend fun deleteAllNotesFromRoom() = dao.deleteAll()
    suspend fun addAllNotesToRoom(list: List<PaymentModel>) = dao.insertAll(list)

    override val allPayments: LiveData<List<PaymentModel>>
        get() = roomAllNotes

    override val groupMembers: LiveData<List<UserModel>>
        get() = TODO("Not yet implemented")

    override fun login(type: String, onFail: () -> Unit, onSucces: () -> Unit) {
        showToast("no internet")
    }

    override fun emailRegistration(onFail: () -> Unit, onSucces: () -> Unit) {
        showToast("no internet")
    }

    override fun createNewRoom(
        roomName: String,
        roomPass: String,
        currencySign: String,
        onFail: () -> Unit,
        onSucces: () -> Unit
    ) {
        showToast("no internet")
    }

    override fun joinRoom(
        roomName: String,
        roomPass: String,
        onFail: () -> Unit,
        onSucces: () -> Unit
    ) {
        showToast("no internet")
    }

    override fun addNewPayment(payment: PaymentModel, onSucces: () -> Unit) {
        showToast("no internet")
    }

    override fun changeName(name: String, onSucces: () -> Unit) {
        showToast("no internet")
    }

    override fun pushFileToBase(imageUri: Uri, onSucces: (String) -> Unit) {
        showToast("no internet")
    }

    override fun signOut() {
        showToast("no internet")
    }

    override fun remindPassword(onSuccess: (String) -> Unit) {
       showToast("no internet")
    }

}