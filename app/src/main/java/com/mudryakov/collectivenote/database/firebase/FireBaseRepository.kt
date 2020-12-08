package com.mudryakov.collectivenote.database.firebase

import android.net.Uri
import androidx.lifecycle.LiveData
import com.google.firebase.database.ServerValue
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.database.AppDatabaseRepository
import com.mudryakov.collectivenote.models.PaymentModel
import com.mudryakov.collectivenote.models.UserModel
import com.mudryakov.collectivenote.utility.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import net.objecthunter.exp4j.ExpressionBuilder

class FireBaseRepository : AppDatabaseRepository {


    override var allPayments: LiveData<List<PaymentModel>> = AllPaymentsFirebase()
    override val groupMembers: LiveData<List<UserModel>> = FirebaseGroupMembers()

    override fun deletePayment(payment: PaymentModel, onSuccess: () -> Unit) {
        REF_DATABASE_ROOT.child(NODE_ROOM_PAYMENTS).child(CURRENT_ROOM_UID)
            .child(payment.firebaseId).removeValue()
            .addOnFailureListener { showToast(APP_ACTIVITY.getString(R.string.something_going_wrong)) }
            .addOnSuccessListener {
                var totalSum = calculate(AppPreference.getTotalSumm(), payment.summ,"-")
                if (ROOM_CURRENCY == APP_ACTIVITY.getString(R.string.RUB)) {
                    totalSum = totalSum.substringBefore('.')
                }
                REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_TOTAL_PAY).child(
                    CURRENT_ROOM_UID
                ).setValue(totalSum).addOnSuccessListener {
                    REF_DATABASE_ROOT.child(NODE_UPDATE_HELPER).child(CURRENT_ROOM_UID)
                        .setValue(payment.firebaseId)
                    AppPreference.setTotalSumm(totalSum)
                    onSuccess()
                }

            }
    }


    override fun login(type: String, onFail: () -> Unit, onSuccess: () -> Unit) {
        logIn(type, onFail) { onSuccess() }
    }

    override fun emailRegistration(onFail: () -> Unit, onSuccess: () -> Unit) {
        createNewEmailUser(onFail, onSuccess)
    }

    override fun createNewRoom(
        roomName: String,
        roomPass: String,
        currencySign: String,
        onFail: () -> Unit,
        onSuccess: () -> Unit
    ) {
        REF_DATABASE_ROOT.child(NODE_ROOM_NAMES).addMySingleListener { DataSnapshot ->
            if (DataSnapshot.hasChild(roomName)) {
                showToast(APP_ACTIVITY.getString(R.string.this_name_busy))
            onFail()
            } else {
                pushRoomToFirebase(roomName, roomPass, currencySign, onFail) {
                    updateUserRoomId(it, onFail) {
                        onSuccess()
                    }
                }
            }
        }
    }

    override fun joinRoom(
        roomName: String,
        roomPass: String,
        onFail: () -> Unit,
        onSuccess: () -> Unit
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
                                    onSuccess()
                                }
                            }
                                .addOnFailureListener {
                                    onFail()
                                    showToast(APP_ACTIVITY.getString(R.string.something_going_wrong))
                                }
                        } else {
                            onFail()
                            showToast(APP_ACTIVITY.getString(R.string.check_room_acc))
                        }
                    }
                    )
            })
    }


    override fun addNewPayment(payment: PaymentModel, onSuccess: () -> Unit) {
        var totalSum = calculate(AppPreference.getTotalSumm(), payment.summ)
        if (ROOM_CURRENCY == APP_ACTIVITY.getString(R.string.RUB)) {
            totalSum = totalSum.substringBefore('.')
        }
        val currentRef = REF_DATABASE_ROOT.child(NODE_ROOM_PAYMENTS).child(CURRENT_ROOM_UID)
        val key = currentRef.push().key.toString()
        payment.firebaseId = key
        payment.time = ServerValue.TIMESTAMP
        currentRef.child(key).setValue(payment)

            .addOnSuccessListener {
                REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_TOTAL_PAY).child(
                    CURRENT_ROOM_UID
                )
                    .setValue(totalSum)
                    .addOnSuccessListener {
                        REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(
                            CHILD_TOTALPAY_AT_CURRENT_ROOM
                        ).setValue(totalSum)
                            .addOnSuccessListener {
                                REF_DATABASE_ROOT.child(NODE_UPDATE_HELPER).child(CURRENT_ROOM_UID)
                                    .setValue(key)
                                AppPreference.setTotalSumm(totalSum)
                                onSuccess()
                            }
                            .addOnFailureListener {
                                REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)
                                    .child(CHILD_TOTAL_PAY).child(CURRENT_ROOM_UID).removeValue()

                            }
                    }
            }
            .addOnFailureListener { showToast(APP_ACTIVITY.getString(R.string.something_going_wrong)) }
    }


    override fun changeName(name: String, onSuccess: () -> Unit) {
        CURRENT_UID = AppPreference.getUserId()
        REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_NAME).setValue(name)
            .addOnSuccessListener {
                AppPreference.setSignIn(true)
                AppPreference.setName(name)
                CoroutineScope(IO).launch { updateAllUserPayments() }
                onSuccess()
            }
            .addOnFailureListener { showToast(APP_ACTIVITY.getString(R.string.something_going_wrong)) }
    }


    override fun pushFileToBase(imageUri: Uri, onSuccess: (String) -> Unit) {
        val key = REF_DATABASE_ROOT.push().key.toString()
        val path = REF_DATABASE_STORAGE.child(NODE_PAYMENT_IMAGES).child(key)
        path.putFile(imageUri)
            .addOnFailureListener { showToast(it.message.toString()) }
            .addOnCompleteListener { _ ->
                path.downloadUrl
                    .addOnFailureListener { showToast(APP_ACTIVITY.getString(R.string.something_going_wrong)) }
                    .addOnSuccessListener { onSuccess(it.toString()) }
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