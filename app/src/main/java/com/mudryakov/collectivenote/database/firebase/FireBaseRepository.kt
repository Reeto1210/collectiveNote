package com.mudryakov.collectivenote.database.firebase

import android.net.Uri
import androidx.lifecycle.LiveData
import com.google.firebase.database.ServerValue
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.database.AppDatabaseRepository
import com.mudryakov.collectivenote.models.PaymentModel
import com.mudryakov.collectivenote.models.UserModel
import com.mudryakov.collectivenote.utilits.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class FireBaseRepository : AppDatabaseRepository {


    override var allPayments: LiveData<List<PaymentModel>> = allPaymentsFirebase()
    override val groupMembers: LiveData<List<UserModel>> = FirebaseGroupMembers()
    override fun connectToDatabase(type: String, onFail: () -> Unit, onSucces: () -> Unit) {
        logIn(type, onFail) { onSucces() }
    }

    override fun createNewRoom(
        roomName: String,
        roomPass: String,
        onFail: () -> Unit,
        onSucces: () -> Unit
    ) {
        REF_DATABASE_ROOT.child(NODE_ROOM_NAMES).addMySingleListener { DataSnapshot ->
            if (DataSnapshot.hasChild(roomName)) {
                showToast(APP_ACTIVITY.getString(R.string.this_namy_busy))
            } else {
                pushRoomToFirebase(roomName, roomPass, onFail) {
                    updateUserRoomId(it, onFail) {
                        onSucces()
                    }
                }
            }
        }


    }

    override fun joinRoom(
        roomName: String,
        roomPass: String,
        onFail: () -> Unit,
        onSucces: () -> Unit
    ) {
        lateinit var tryingId: String
        REF_DATABASE_ROOT.child(NODE_ROOM_NAMES).child(roomName)
            .addListenerForSingleValueEvent(AppValueEventListener {
                tryingId = it.value.toString()
                REF_DATABASE_ROOT.child(NODE_ROOM_DATA).child(tryingId).child(CHILD_PASS)
                    .addListenerForSingleValueEvent(AppValueEventListener { DataSnapshot ->
                        if (DataSnapshot.value == roomPass) {
                            REF_DATABASE_ROOT.child(NODE_UPDATE_HELPER).child(tryingId).setValue(
                                tryingId
                            ).addOnSuccessListener {
                                updateUserRoomId(tryingId, onFail) {
                                    onSucces()
                                }
                            }
                                .addOnFailureListener { ex ->
                                    onFail()
                                    showToast(ex.message.toString())
                                }
                        } else {
                            onFail()
                            showToast(APP_ACTIVITY.getString(R.string.check_room_acc))
                        }
                    }
                    )
            })
    }


    override fun addNewPayment(payment: PaymentModel, onSucces: () -> Unit) {
        val totalSumm = (USER.totalPayAtCurrentRoom.toLong() + payment.summ.toLong()).toString()
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
                                REF_DATABASE_ROOT.child(NODE_UPDATE_HELPER).child(CURRENT_ROOM_UID)
                                    .setValue(key)
                                appPreference.setTotalSumm(totalSumm)
                                USER.totalPayAtCurrentRoom = totalSumm
                                onSucces()
                            }
                            .addOnFailureListener {
                                REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)
                                    .child(CHILD_TOTAL_PAY).child(CURRENT_ROOM_UID).removeValue()

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
                CoroutineScope(IO).launch { updateAllUserPayments() }

                onSucces()
            }
            .addOnFailureListener { showToast(it.message.toString()) }
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

    override fun signOut() {
        AUTH.signOut()
    }

    override fun remindPassword(onSuccess: (String) -> Unit) {
        REF_DATABASE_ROOT.child(NODE_ROOM_DATA).child(CURRENT_ROOM_UID).child(CHILD_PASS)
            .addMySingleListener {
                onSuccess(it.value.toString())
            }
    }


}