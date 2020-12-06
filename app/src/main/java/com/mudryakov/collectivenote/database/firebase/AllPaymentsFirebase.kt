package com.mudryakov.collectivenote.database.firebase

import androidx.lifecycle.LiveData
import com.mudryakov.collectivenote.models.PaymentModel
import com.mudryakov.collectivenote.utilits.AppValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class AllPaymentsFirebase : LiveData<List<PaymentModel>>() {

    private val listener = AppValueEventListener { DataSnapshot ->
        val allPayments =
            DataSnapshot.children.map { it.getValue(PaymentModel::class.java) ?: PaymentModel() }
        CoroutineScope(IO).launch {
           ROOM_REPOSITORY.updateAllPaymentsRoomDatabase(allPayments)
        }
        value = allPayments
    }

    override fun onActive() {
        super.onActive()
        REF_DATABASE_ROOT.child(NODE_ROOM_PAYMENTS).child(CURRENT_ROOM_UID)
            .addValueEventListener(listener)
    }

    override fun onInactive() {
        super.onInactive()
        REF_DATABASE_ROOT.child(NODE_ROOM_PAYMENTS).child(CURRENT_ROOM_UID)
            .removeEventListener(listener)
    }


}