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
import com.mudryakov.collectivenote.utility.*


var USERNAME: String = ""
lateinit var EMAIL: String
lateinit var PASSWORD: String


const val CHILD_IMAGE_URL = "imageUrl"
const val CHILD_SUM = "summ"
const val CHILD_TIME = "time"
const val CHILD_FROM_ID = "fromId"
const val CHILD_DESCRIPTION = "description"
const val CHILD_FIREBASE_ID = "firebaseId"
const val NODE_UPDATE_HELPER = "updateHelper"
const val CHILD_PASS = "password"
const val CHILD_TOTAL_PAY_AT_CURRENT_GROUP = "totalPayAtCurrentGroup"
const val NODE_USERS = "users"
const val NODE_GROUP_DATA = "groupsData"
const val CHILD_CREATOR = "creator"
const val NODE_GROUP_NAMES = "groupNames"
const val NODE_GROUP_PAYMENTS = "groupPayments"
const val NODE_PAYMENT_IMAGES = "paymentImages"
const val NODE_GROUP_MEMBERS = "groupMembers"
const val CHILD_TOTAL_PAY = "totalPay"
const val CHILD_FROM_NAME = "fromName"
const val CHILD_GROUP_CURRENCY = "groupCurrency"
const val CHILD_NAME = "name"
const val CHILD_GROUP_ID = "groupId"
lateinit var CURRENT_GROUP_UID: String
lateinit var REF_DATABASE_ROOT: DatabaseReference
lateinit var REF_DATABASE_STORAGE: StorageReference
lateinit var REPOSITORY: AppDatabaseRepository
lateinit var ROOM_REPOSITORY: AppRoomRepository

lateinit var AUTH: FirebaseAuth
lateinit var CURRENT_UID: String
private lateinit var ON_REGISTRATION_COMPLETE: () -> Unit
private lateinit var ON_REGISTRATION_FAIL: () -> Unit


fun logIn(type: String, onFail: () -> Unit, onComplete: () -> Unit) {
    ON_REGISTRATION_COMPLETE = onComplete
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
                AppPreference.setUserId(AUTH.currentUser?.uid!!)
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
    val userHashMap = hashMapOf<String, String>()
    userHashMap[CHILD_FIREBASE_ID] = CURRENT_UID
    userHashMap[CHILD_NAME] = USERNAME
    CURRENT_UID = AppPreference.getUserId()


    REF_DATABASE_ROOT.child(NODE_USERS).addMySingleListener {
        if (!it.hasChild(CURRENT_UID)) {
            REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).setValue(userHashMap)
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

fun pushGroupToFirebase(
    groupName: String,
    groupPass: String,
    currencySign: String,
    onFail: () -> Unit,
    function: (String) -> Unit
) {
    val mainHashMap = HashMap<String, Any>()
    val roomInfoHashMap = HashMap<String, Any>()
    val groupKey = REF_DATABASE_ROOT.push().key.toString()
    roomInfoHashMap[CHILD_GROUP_CURRENCY] = currencySign
    roomInfoHashMap[CHILD_NAME] = groupName
    roomInfoHashMap[CHILD_GROUP_ID] = groupKey
    roomInfoHashMap[CHILD_PASS] = groupPass
    roomInfoHashMap[CHILD_CREATOR] = CURRENT_UID
    mainHashMap["$NODE_GROUP_DATA/$groupKey"] = roomInfoHashMap
    mainHashMap["$NODE_GROUP_NAMES/$groupName"] = groupKey
    REF_DATABASE_ROOT.updateChildren(mainHashMap)
        .addOnSuccessListener {
            function(groupKey)
            REF_DATABASE_ROOT.child(NODE_UPDATE_HELPER).child(groupKey).setValue("Created")
        }
        .addOnFailureListener {
            onFail()
            showToast(R.string.something_going_wrong)
        }
}

fun updateUserGroupId(groupKey: String, onFail: () -> Unit, onSuccess: () -> Unit) {

    REF_DATABASE_ROOT.child(NODE_USERS).child(AppPreference.getUserId()).child(
        CHILD_GROUP_ID
    ).setValue(groupKey)
        .addOnFailureListener {
            onFail()
            showToast(R.string.something_going_wrong)
        }
        .addOnSuccessListener {
            REF_DATABASE_ROOT.child(NODE_GROUP_MEMBERS).child(groupKey).child(CURRENT_UID)
                .setValue(CURRENT_UID)
                .addOnSuccessListener {
                    AppPreference.setGroupId(groupKey)
                    CURRENT_GROUP_UID = groupKey
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
        .child(CURRENT_GROUP_UID).addMySingleListener {
            val totalPay = if (it.value.toString() == "null") "0.00" else it.value.toString()
            AppPreference.setTotalSumm(totalPay)
            REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)
                .child(CHILD_TOTAL_PAY_AT_CURRENT_GROUP)
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
        findAllUsersPayments(listOfId) { groupId, paymentId ->
            changePaymentFromName(groupId, paymentId)
        }
    }
}

fun changePaymentFromName(roomId: String, paymentId: String) {
    REF_DATABASE_ROOT.child(NODE_GROUP_PAYMENTS).child(roomId).child(paymentId)
        .child(CHILD_FROM_NAME).setValue(AppPreference.getUserName())
}

fun findAllUsersPayments(
    listOfId: List<String>,
    function: (groupId: String, paymentId: String) -> Unit
) {
    listOfId.forEach { id ->
        REF_DATABASE_ROOT.child(NODE_GROUP_PAYMENTS).child(id).addMySingleListener {
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
            val list = it.children.map { group -> group.key.toString() }
            onComplete(list)
        }
}

