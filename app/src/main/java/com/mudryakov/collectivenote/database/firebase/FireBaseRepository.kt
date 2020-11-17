package com.mudryakov.collectivenote.database.firebase

import android.net.Uri
import androidx.lifecycle.LiveData
import com.google.firebase.database.ServerValue
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.database.AppDatabaseRepository
import com.mudryakov.collectivenote.models.PaymentModel
import com.mudryakov.collectivenote.models.UserModel
import com.mudryakov.collectivenote.utilits.*

class FireBaseRepository : AppDatabaseRepository {



    override var allPayments: LiveData<List<PaymentModel>> = allPaymentsFirebase()
    override val groupMembers: LiveData<List<UserModel>> = FirebaseRoomMembers()
    override fun connectToDatabase(type: String, onSucces: () -> Unit) {
        logIn(type) { onSucces() }
    }

    override fun createNewRoom(roomName: String, roomPass: String, onSucces: () -> Unit) {
        REF_DATABASE_ROOT.child(NODE_ROOM_NAMES).addMySingleListener { DataSnapshot ->
            if (DataSnapshot.hasChild(roomName)) {
                showToast(APP_ACTIVITY.getString(R.string.this_namy_busy))
            } else {
                pushRoomToFirebase(roomName, roomPass) {
                    updateUserRoomId(it) {
                        onSucces()
                    }
                }
            }
        }


    }

    override fun joinRoom(roomName: String, roomPass: String, onSucces: () -> Unit) {
        lateinit var tryingId: String
        REF_DATABASE_ROOT.child(NODE_ROOM_NAMES).child(roomName)
            .addListenerForSingleValueEvent(AppValueEventListener {
                tryingId = it.value.toString()
                REF_DATABASE_ROOT.child(NODE_ROOM_DATA).child(tryingId).child(CHILD_PASS)
                    .addListenerForSingleValueEvent(AppValueEventListener { DataSnapshot ->
                        if (DataSnapshot.value == roomPass) {
                            updateUserRoomId(tryingId) {
                                onSucces()
                            }
                        } else {
                            showToast(APP_ACTIVITY.getString(R.string.check_room_acc))
                        }
                    }
                    )
            })
    }


    override fun addNewPayment(payment: PaymentModel, onSucces: () -> Unit) {
            val totalSumm = (USER.totalPayAtCurrentRoom.toInt() + payment.summ.toInt()).toString()
        val currentRef = REF_DATABASE_ROOT.child(NODE_ROOM_PAYMENTS).child(CURRENT_ROOM_UID)
        val key = currentRef.push().key.toString()
        payment.firebaseId = key
        payment.time = ServerValue.TIMESTAMP
        currentRef.child(key).setValue(payment)
            .addOnSuccessListener {
                REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_TOTAL_PAY).child(
                    CURRENT_ROOM_UID
                )
                    .setValue(totalSumm)
                    .addOnSuccessListener {
                        REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(
                            CHILD_TOTALPAY_AT_CURRENT_ROOM
                        ).setValue(totalSumm)
                            .addOnSuccessListener {
                            appPreference.setTotalSumm(totalSumm)
                            USER.totalPayAtCurrentRoom = appPreference.getTotalSumm()
                            onSucces()
                        }
                            .addOnFailureListener {
                                REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_TOTAL_PAY).child(CURRENT_ROOM_UID).removeValue()

                            }
                    }
            }
            .addOnFailureListener { showToast(it.message.toString()) }
    }


    override fun changeName(name: String, onSucces: () -> Unit) {
        REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_NAME).setValue(name)
            .addOnSuccessListener {
                appPreference.setUserId(CURRENT_UID)
                appPreference.setSignIn(true)
                appPreference.setName(name)
                onSucces()
            }
            .addOnFailureListener { showToast(it.message.toString()) }
    }

    override fun addnewQuest(onSucces: () -> Unit) {
        TODO("Not yet implemented")
    }

    override fun getQuest(onSucces: () -> Unit) {
        TODO("Not yet implemented")
    }

    override fun pushFileToBase(imageUri: Uri, onSucces: (String) -> Unit) {
        val key = REF_DATABASE_ROOT.push().key.toString()
        val path = REF_DATABASE_STORAGE.child(NODE_PAYMENT_IMAGES).child(key)
        path.putFile(imageUri)
            .addOnFailureListener { showToast(it.message.toString()) }
            .addOnCompleteListener { _ ->
                path.downloadUrl
                    .addOnFailureListener { ex -> showToast(ex.message.toString()) }
                    .addOnSuccessListener { onSucces(it.toString()) }


            }


    }


}