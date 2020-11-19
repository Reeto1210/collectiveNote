@file:Suppress("DEPRECATION")

package com.mudryakov.collectivenote.database.firebase

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import com.mudryakov.collectivenote.database.AppDatabaseRepository
import com.mudryakov.collectivenote.database.RoomDatabase.AppRoomRepository
import com.mudryakov.collectivenote.models.PaymentModel
import com.mudryakov.collectivenote.models.UserModel
import com.mudryakov.collectivenote.utilits.*

const val CHILD_NAME = "name"
const val CHILD_FIREBASE_ID = "firebaseId"
const val CHILD_ROOM_ID = "roomId"
var USERNAME:String = ""
lateinit var EMAIL:String
lateinit var PASSWORD:String


const val CHILD_PASS = "password"
const val CHILD_TOTALPAY_AT_CURRENT_ROOM = "totalPayAtCurrentRoom"
const val NODE_USERS = "users"
const val NODE_ROOM_DATA = "roomsData"
const val CHILD_CREATOR = "creator"
const val NODE_ROOM_NAMES = "roomNames"
const val NODE_ROOM_PAYMENTS ="roomsPayments"
const val NODE_ROOMS_QUESTS ="roomsQuests"
const val NODE_PAYMENT_IMAGES = "paymentImages"
const val NODE_ROOM_MEMBERS = "rooms_members"
const val CHILD_TOTAL_PAY = "totalPay"


lateinit var CURRENT_ROOM_UID:String
lateinit var REF_DATABASE_ROOT: DatabaseReference
lateinit var REF_DATABASE_STORAGE: StorageReference
lateinit var REPOSITORY: AppDatabaseRepository
lateinit var ROOM_REPOSITORY:AppRoomRepository

lateinit var AUTH: FirebaseAuth
lateinit var CURRENT_UID: String
private lateinit var ON_REGISTRATION_COMPLETE: () -> Unit


fun logIn(type: String, onCompelete: () -> Unit) {
    ON_REGISTRATION_COMPLETE = onCompelete
    when (type) {
        TYPE_GOOGLE_ACCOUNT -> logInGogle()
        TYPE_EMAIL -> joinWithEmail()
    }
}

fun joinWithEmail() {
    tryLogInEmail { createNewEmailUser()}
}

fun tryLogInEmail(function: () -> Unit) {
    AUTH.signInWithEmailAndPassword(EMAIL, PASSWORD)
        .addOnSuccessListener {
        CURRENT_UID = AUTH.currentUser?.uid.toString()
        ON_REGISTRATION_COMPLETE()
    }.addOnFailureListener {
        if ( it is FirebaseAuthInvalidCredentialsException) showToast(it.message.toString())
        else{function()}
    }
}

fun createNewEmailUser() {
    AUTH.createUserWithEmailAndPassword(EMAIL, PASSWORD).addOnSuccessListener {
        CURRENT_UID = AUTH.currentUser?.uid.toString()
        pushUserToFirebase()
    }
        .addOnFailureListener {showToast(it.message.toString()) }
}


fun logInGogle() {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .build()
    val mGogoglemSignClient = GoogleSignIn.getClient(APP_ACTIVITY, gso)
    val intent: Intent = mGogoglemSignClient.signInIntent
    APP_ACTIVITY.startActivityForResult(intent, SIGN_CODE_REQUEST)

}


fun handleSignInresult(task: Task<GoogleSignInAccount>) {
    try {
        val account: GoogleSignInAccount = task.getResult(ApiException::class.java)!!
        USERNAME = account.displayName.toString()
        CURRENT_UID = account.id.toString()
        pushUserToFirebase()
    } catch (e: ApiException) {
        showToast(e.message.toString())
    }
}

fun pushUserToFirebase(){
    USER = UserModel(CURRENT_UID, USERNAME)
    REF_DATABASE_ROOT.child(NODE_USERS).addMySingleListener{
        if  (!it.hasChild(CURRENT_UID)) {
        REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).setValue(USER)
            .addOnFailureListener { showToast(it.message.toString()) }
            .addOnSuccessListener {
                ON_REGISTRATION_COMPLETE()
            }
    }else ON_REGISTRATION_COMPLETE()
    }


}
fun pushRoomToFirebase(roomName: String, roomPass: String, function: (String) -> Unit) {
    val mainHashMap = HashMap<String, Any>()
    val roomInfoHashMap = HashMap<String, Any>()
     val roomkey = REF_DATABASE_ROOT.push().key.toString()
    roomInfoHashMap[CHILD_NAME] = roomName
    roomInfoHashMap[CHILD_ROOM_ID] = roomkey
    roomInfoHashMap[CHILD_PASS] = roomPass
    roomInfoHashMap[CHILD_CREATOR] = CURRENT_UID
      mainHashMap["$NODE_ROOM_DATA/$roomkey"] = roomInfoHashMap
    mainHashMap["$NODE_ROOM_NAMES/$roomName"] = roomkey
    REF_DATABASE_ROOT.updateChildren(mainHashMap)
        .addOnSuccessListener {function(roomkey)
        }
        .addOnFailureListener { problem -> showToast(problem.message.toString()) }
}
fun updateUserRoomId(roomkey:String, function: () -> Unit) {

    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(
        CHILD_ROOM_ID
    ).setValue(roomkey)
        .addOnFailureListener { problem -> showToast(problem.message.toString()) }
        .addOnSuccessListener {
            REF_DATABASE_ROOT.child(NODE_ROOM_MEMBERS).child(roomkey).child(CURRENT_UID).setValue(CURRENT_UID)
                .addOnSuccessListener {appPreference.setSignInRoom(true)
                    appPreference.setSignInRoom(true)
                    appPreference.setRoomId(roomkey)
                CURRENT_ROOM_UID = roomkey
                    updatePreferenceCurrentPay()
                     function() }
                .addOnFailureListener { prob -> showToast(prob.message.toString()) }
          }
}

fun updatePreferenceCurrentPay() {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_TOTAL_PAY).child(CURRENT_ROOM_UID).addMySingleListener {
        appPreference.setTotalSumm(it.value.toString()) }

}
