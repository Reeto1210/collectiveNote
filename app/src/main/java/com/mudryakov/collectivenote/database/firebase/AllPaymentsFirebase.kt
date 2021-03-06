package com.mudryakov.collectivenote.database.firebase

import androidx.lifecycle.LiveData
import com.mudryakov.collectivenote.database.RoomDatabase.AppRoomRepository
import com.mudryakov.collectivenote.models.PaymentModel
import com.mudryakov.collectivenote.utility.AppValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

class AllPaymentsFirebase @Inject constructor(val roomRepository: AppRoomRepository): LiveData<List<PaymentModel>>() {

    private val listener = AppValueEventListener { DataSnapshot ->
        val allPayments =
            DataSnapshot.children.map { it.getValue(PaymentModel::class.java) ?: PaymentModel() }
        CoroutineScope(IO).launch {
          roomRepository.updateAllPaymentsRoomDatabase(allPayments)
        }
        value = allPayments
    }

    override fun onActive() {
        super.onActive()
        REF_DATABASE_ROOT.child(NODE_GROUP_PAYMENTS).child(CURRENT_GROUP_UID).addValueEventListener(listener)

   }

    override fun onInactive() {
        super.onInactive()
        REF_DATABASE_ROOT.child(NODE_GROUP_PAYMENTS).child(CURRENT_GROUP_UID).removeEventListener(listener)
    }


}