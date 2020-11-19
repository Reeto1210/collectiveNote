package com.mudryakov.collectivenote.database.RoomDatabase

import androidx.lifecycle.LiveData
import com.mudryakov.collectivenote.models.PaymentModel

class AppRoomRepository(private val dao: myDao) {

    val roomAllNotes: LiveData<List<PaymentModel>> get() = dao.getallPayments()
    suspend fun deleteAllNotesFromRoom() = dao.deleteAll()
    suspend fun addAllNotesToRoom(list: List<PaymentModel>) = dao.insertAll(list)

}