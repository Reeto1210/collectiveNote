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

class FireBaseRepository : AppDatabaseRepository {


    override var allPayments: LiveData<List<PaymentModel>> = AllPaymentsFirebase()
    override val groupMembers: LiveData<List<UserModel>> = FirebaseGroupMembers()

    override fun deletePayment(payment: PaymentModel, onSuccess: () -> Unit) {
        val refCurrentUser = REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)

        REF_DATABASE_ROOT.child(NODE_GROUP_PAYMENTS).child(CURRENT_GROUP_UID)
            .child(payment.firebaseId).removeValue()
            .addOnFailureListener { showToast(R.string.something_going_wrong) }
            .addOnSuccessListener {
                val totalSum = calculateMinus(AppPreference.getTotalSumm(), payment.summ)

                refCurrentUser.child(CHILD_TOTAL_PAY).child(CURRENT_GROUP_UID).setValue(totalSum)
                    .addOnSuccessListener {
                        refCurrentUser.child(
                            CHILD_TOTAL_PAY_AT_CURRENT_GROUP
                        ).setValue(totalSum).addOnSuccessListener {
                            AppPreference.setTotalSumm(totalSum)
                            updateHelper(payment.firebaseId)
                            onSuccess()
                        }
                    }
            }
    }


    override fun login(type: String, onFail: () -> Unit, onSuccess: () -> Unit) {
        logIn(type, onFail) { onSuccess() }
    }

    override fun emailRegistration(onFail: () -> Unit, onSuccess: () -> Unit) {
        createNewEmailUser(onFail, onSuccess)
    }

    override fun createNewGroup(
        groupName: String,
        groupPass: String,
        currencySign: String,
        onFail: () -> Unit,
        onSuccess: () -> Unit
    ) {
        REF_DATABASE_ROOT.child(NODE_GROUP_NAMES).addMySingleListener { DataSnapshot ->
            if (DataSnapshot.hasChild(groupName)) {
                showToast(R.string.this_name_busy)
                onFail()
            } else {
                pushGroupToFirebase(groupName, groupPass, currencySign, onFail) {
                    updateUserGroupId(it, onFail) {
                        onSuccess()
                    }
                }
            }
        }
    }

    override fun joinGroup(
        groupName: String,
        groupPass: String,
        onFail: () -> Unit,
        onSuccess: () -> Unit
    ) {
        lateinit var tryingId: String
        REF_DATABASE_ROOT.child(NODE_GROUP_NAMES).child(groupName)
            .addMySingleListener {
                tryingId = it.value.toString()
                REF_DATABASE_ROOT.child(NODE_GROUP_DATA).child(tryingId).child(CHILD_PASS)
                    .addMySingleListener { DataSnapshot ->
                        if (DataSnapshot.value == groupPass) {
                            REF_DATABASE_ROOT.child(NODE_UPDATE_HELPER).child(tryingId).setValue(
                                tryingId
                            ).addOnSuccessListener {
                                updateUserGroupId(tryingId, onFail) {
                                    onSuccess()
                                }
                            }
                                .addOnFailureListener {
                                    onFail()
                                    showToast(R.string.something_going_wrong)
                                }
                        } else {
                            onFail()
                            showToast(R.string.check_room_acc)
                        }
                    }
            }
    }


    override fun addNewPayment(payment: PaymentModel, onSuccess: () -> Unit) {
        val totalSum = calculateSum(AppPreference.getTotalSumm(), payment.summ)
        val refCurrentRoom = REF_DATABASE_ROOT.child(NODE_GROUP_PAYMENTS).child(CURRENT_GROUP_UID)
        val refCurrentUser = REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)
        val key = refCurrentRoom.push().key.toString()
        payment.firebaseId = key
        payment.time = ServerValue.TIMESTAMP
        val paymentHash = transformModelToHash(payment)

        refCurrentRoom.child(key).setValue(paymentHash)
            .addOnSuccessListener {
                refCurrentUser.child(CHILD_TOTAL_PAY).child(
                    CURRENT_GROUP_UID
                )
                    .setValue(totalSum)
                    .addOnSuccessListener {
                        refCurrentUser.child(CHILD_TOTAL_PAY_AT_CURRENT_GROUP)
                            .setValue(totalSum)
                            .addOnSuccessListener {
                                REF_DATABASE_ROOT.child(NODE_UPDATE_HELPER).child(CURRENT_GROUP_UID)
                                    .setValue(key + "1")
                                AppPreference.setTotalSumm(totalSum)
                                onSuccess()
                            }
                    }
            }
            .addOnFailureListener { showToast(R.string.something_going_wrong) }
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
            .addOnFailureListener { showToast(R.string.something_going_wrong) }
    }


    override fun pushFileToBase(imageUri: Uri, onSuccess: (String) -> Unit) {
        val key = REF_DATABASE_ROOT.push().key.toString()
        val path = REF_DATABASE_STORAGE.child(NODE_PAYMENT_IMAGES).child(key)
        path.putFile(imageUri)
            .addOnFailureListener { showToast(R.string.something_going_wrong) }
            .addOnCompleteListener { _ ->
                path.downloadUrl
                    .addOnFailureListener { showToast(R.string.something_going_wrong) }
                    .addOnSuccessListener { onSuccess(it.toString()) }
            }
    }

    override fun signOut() {
        AUTH.signOut()
    }

    override fun remindPassword(onSuccess: (String) -> Unit) {
        REF_DATABASE_ROOT.child(NODE_GROUP_DATA).child(CURRENT_GROUP_UID).child(CHILD_PASS)
            .addMySingleListener {
                onSuccess(it.value.toString())
            }
    }
}