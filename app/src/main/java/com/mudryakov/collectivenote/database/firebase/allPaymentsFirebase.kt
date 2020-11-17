package com.mudryakov.collectivenote.database.firebase

import androidx.lifecycle.LiveData
import com.mudryakov.collectivenote.models.PaymentModel
import com.mudryakov.collectivenote.utilits.AppValueEventListener

class allPaymentsFirebase: LiveData<List<PaymentModel>>() {

    val listener = AppValueEventListener{ DataSnapshot->
        value = DataSnapshot.children.map { it.getValue(PaymentModel::class.java)?:PaymentModel()
        }
    }

    override fun onActive() {
        super.onActive()
    REF_DATABASE_ROOT.child(NODE_ROOM_PAYMENTS).child(CURRENT_ROOM_UID).addValueEventListener(listener)
    }

    override fun onInactive() {
        super.onInactive()
        REF_DATABASE_ROOT.child(NODE_ROOM_PAYMENTS).child(CURRENT_ROOM_UID).removeEventListener(listener)
    }


}