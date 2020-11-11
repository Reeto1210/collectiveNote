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
import com.mudryakov.collectivenote.database.AppDatabaseRepository
import com.mudryakov.collectivenote.models.User
import com.mudryakov.collectivenote.utilits.*

const val CHILD_NAME = "name"
const val CHILD_FIREBASE_ID = "firebaseId"
const val CHILD_ROOM_ID = "roomId"
var USERNAME:String = ""
lateinit var EMAIL:String
lateinit var PASSWORD:String


const val CHILD_PASS = "password"
const val NODE_USERS = "users"
const val NODE_ROOM_DATA = "groupsData"
const val CHILD_CREATOR = "creator"
const val NODE_ROOM_NAMES = "roomNames"


lateinit var REF_DATABASE_ROOT: DatabaseReference
lateinit var REPOSITORY: AppDatabaseRepository
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
    USER = User(CURRENT_UID, USERNAME)
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).setValue(USER)
        .addOnFailureListener { showToast(it.message.toString()) }
        .addOnSuccessListener {
       ON_REGISTRATION_COMPLETE()
        }
}
fun pushRoomToFirebase(roomName: String, roomPass: String, function: (String) -> Unit) {
    val mainHashMap = HashMap<String, Any>()
    val roomInfoHashMap = HashMap<String, Any>()
    val roomNameHashMap = HashMap<String, Any>()
    val roomkey = REF_DATABASE_ROOT.push().key.toString()
    roomInfoHashMap[CHILD_NAME] = roomName
    roomInfoHashMap[CHILD_ROOM_ID] = roomkey
    roomInfoHashMap[CHILD_PASS] = roomPass
    roomInfoHashMap[CHILD_CREATOR] = CURRENT_UID
    roomNameHashMap[roomName] = roomkey
    mainHashMap["$NODE_ROOM_DATA/$roomkey"] = roomInfoHashMap
    mainHashMap[NODE_ROOM_NAMES] = roomNameHashMap
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
            appPreference.setSignInRoom(true)
            appPreference.setRoomId(roomkey)
            function()}
}

fun DataSnapshot.getUserFromFirebase() = this.getValue(User::class.java) ?: User()