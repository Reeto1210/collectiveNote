@file:Suppress("DEPRECATION")

package com.mudryakov.collectivenote.database.firebase

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.database.AppDatabaseRepository
import com.mudryakov.collectivenote.database.RoomDatabase.AppRoomRepository
import com.mudryakov.collectivenote.models.PaymentModel
import com.mudryakov.collectivenote.models.UserModel
import com.mudryakov.collectivenote.utility.*

const val CHILD_NAME = "name"
const val CHILD_ROOM_ID = "roomId"
var USERNAME: String = ""
lateinit var EMAIL: String
lateinit var PASSWORD: String

const val NODE_UPDATE_HELPER = "updateHelper"
const val CHILD_PASS = "password"
const val CHILD_TOTALPAY_AT_CURRENT_ROOM = "totalPayAtCurrentRoom"
const val NODE_USERS = "users"
const val NODE_ROOM_DATA = "roomsData"
const val CHILD_CREATOR = "creator"
const val NODE_ROOM_NAMES = "roomNames"
const val NODE_ROOM_PAYMENTS = "roomsPayments"
const val NODE_PAYMENT_IMAGES = "paymentImages"
const val NODE_ROOM_MEMBERS = "rooms_members"
const val CHILD_TOTAL_PAY = "totalPay"
const val CHILD_FROM_NAME = "fromName"
const val CHILD_ROOM_CURRENCY = "roomCurrency"

lateinit var CURRENT_ROOM_UID: String
lateinit var REF_DATABASE_ROOT: DatabaseReference
lateinit var REF_DATABASE_STORAGE: StorageReference
lateinit var REPOSITORY: AppDatabaseRepository
lateinit var ROOM_REPOSITORY: AppRoomRepository

lateinit var AUTH: FirebaseAuth
lateinit var CURRENT_UID: String
private lateinit var ON_REGISTRATION_COMPLETE: () -> Unit
private lateinit var ON_REGISTRATION_FAIL: () -> Unit


fun logIn(type: String, onFail: () -> Unit, onCompelete: () -> Unit) {
    ON_REGISTRATION_COMPLETE = onCompelete
    ON_REGISTRATION_FAIL = onFail

    when (type) {
        TYPE_GOOGLE_ACCOUNT -> logInGoogle()
        TYPE_EMAIL -> logInEmail()
    }
}


fun logInEmail() {
    AUTH.signInWithEmailAndPassword(EMAIL, PASSWORD)
        .addOnSuccessListener {
            AppPreference.setUserId(AUTH.currentUser?.uid.toString())
            ON_REGISTRATION_COMPLETE()
        }.addOnFailureListener { ex ->
            ON_REGISTRATION_FAIL()
            exceptionEmailLoginToast(ex)
        }
}

fun createNewEmailUser(onFail: () -> Unit, onSuccess: () -> Unit) {
    ON_REGISTRATION_COMPLETE = onSuccess
    ON_REGISTRATION_FAIL = onFail
    AUTH.createUserWithEmailAndPassword(EMAIL, PASSWORD).addOnSuccessListener {
        AppPreference.setUserId(AUTH.currentUser?.uid.toString())
        pushUserToFirebase()
    }
        .addOnFailureListener { ex ->
            ON_REGISTRATION_FAIL()
            exceptionEmailRegistrationToast(ex.message.toString())
        }
}

fun logInGoogle() {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(APP_ACTIVITY.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    val mGoogleSingInClient = GoogleSignIn.getClient(APP_ACTIVITY, gso)
    val intent: Intent = mGoogleSingInClient.signInIntent
    APP_ACTIVITY.startActivityForResult(intent, SIGN_CODE_REQUEST)
}


fun handleSignInResult(task: Task<GoogleSignInAccount>) {
    try {
        val account: GoogleSignInAccount = task.getResult(ApiException::class.java)!!

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        AUTH.signInWithCredential(credential)
            .addOnSuccessListener {
                USERNAME = account.displayName.toString()
                AppPreference.setUserId(AUTH?.currentUser?.uid!!)
                pushUserToFirebase()
            }
            .addOnFailureListener {
                ON_REGISTRATION_FAIL()
                showToast(R.string.something_going_wrong)
            }


    } catch (e: ApiException) {
        ON_REGISTRATION_FAIL()
        showToast(R.string.something_going_wrong)
    }
}


fun pushUserToFirebase() {
    CURRENT_UID = AppPreference.getUserId()
    USER = UserModel(CURRENT_UID, USERNAME)
    REF_DATABASE_ROOT.child(NODE_USERS).addMySingleListener {
        if (!it.hasChild(CURRENT_UID)) {
            REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).setValue(USER)
                .addOnFailureListener {
                    showToast(R.string.something_going_wrong)
                    ON_REGISTRATION_FAIL()
                }
                .addOnSuccessListener {
                    ON_REGISTRATION_COMPLETE()
                }
        } else ON_REGISTRATION_COMPLETE()
    }
}

fun pushRoomToFirebase(
    roomName: String,
    roomPass: String,
    currencySign: String,
    onFail: () -> Unit,
    function: (String) -> Unit
) {
    val mainHashMap = HashMap<String, Any>()
    val roomInfoHashMap = HashMap<String, Any>()
    val roomKey = REF_DATABASE_ROOT.push().key.toString()
    roomInfoHashMap[CHILD_ROOM_CURRENCY] = currencySign
    roomInfoHashMap[CHILD_NAME] = roomName
    roomInfoHashMap[CHILD_ROOM_ID] = roomKey
    roomInfoHashMap[CHILD_PASS] = roomPass
    roomInfoHashMap[CHILD_CREATOR] = CURRENT_UID
    mainHashMap["$NODE_ROOM_DATA/$roomKey"] = roomInfoHashMap
    mainHashMap["$NODE_ROOM_NAMES/$roomName"] = roomKey
    REF_DATABASE_ROOT.updateChildren(mainHashMap)
        .addOnSuccessListener {
            function(roomKey)
            REF_DATABASE_ROOT.child(NODE_UPDATE_HELPER).child(roomKey).setValue("Created")
        }
        .addOnFailureListener {
            onFail()
            showToast(R.string.something_going_wrong)
        }
}

fun updateUserRoomId(roomKey: String, onFail: () -> Unit, onSuccess: () -> Unit) {

    REF_DATABASE_ROOT.child(NODE_USERS).child(AppPreference.getUserId()).child(
        CHILD_ROOM_ID
    ).setValue(roomKey)
        .addOnFailureListener {
            onFail()
            showToast(R.string.something_going_wrong)
        }
        .addOnSuccessListener {
            REF_DATABASE_ROOT.child(NODE_ROOM_MEMBERS).child(roomKey).child(CURRENT_UID)
                .setValue(CURRENT_UID)
                .addOnSuccessListener {
                    AppPreference.setRoomId(roomKey)
                    CURRENT_ROOM_UID = roomKey
                    updatePreferenceCurrentPay { onSuccess() }
                }
                .addOnFailureListener {
                    showToast(R.string.something_going_wrong)
                    onFail()
                }
        }
}

fun updatePreferenceCurrentPay(function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_TOTAL_PAY)
        .child(CURRENT_ROOM_UID).addMySingleListener {
            val totalPay = if (it.value.toString() == "null") "0" else it.value.toString()
            AppPreference.setTotalSumm(totalPay)
            REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)
                .child(CHILD_TOTALPAY_AT_CURRENT_ROOM)
                .setValue(totalPay)
                .addOnSuccessListener { function() }
                .addOnFailureListener {
                    showToast(R.string.something_going_wrong)
                    ON_REGISTRATION_FAIL()
                }
        }

}

fun updateAllUserPayments() {
    findAllUsersRooms { listOfId ->
        findAllUsersPayments(listOfId) { roomId, paymentId ->
            changePaymentFromName(roomId, paymentId)
        }
    }
}

fun changePaymentFromName(roomId: String, paymentId: String) {
    REF_DATABASE_ROOT.child(NODE_ROOM_PAYMENTS).child(roomId).child(paymentId)
        .child(CHILD_FROM_NAME).setValue(AppPreference.getUserName())
}

fun findAllUsersPayments(
    listOfId: List<String>,
    function: (roomId: String, paymentId: String) -> Unit
) {
    listOfId.forEach { id ->
        REF_DATABASE_ROOT.child(NODE_ROOM_PAYMENTS).child(id).addMySingleListener {
            val list = it.children.map { payment ->
                payment.getValue(PaymentModel::class.java) ?: PaymentModel()
            }
            list.forEach { curPayment ->
                if (curPayment.fromId == CURRENT_UID)
                    function(id, curPayment.firebaseId)
            }
        }
    }
}


fun findAllUsersRooms(onComplete: (List<String>) -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_TOTAL_PAY)
        .addMySingleListener {
            val list = it.children.map { room -> room.key.toString() }
            onComplete(list)
        }
}

