@file:Suppress("DEPRECATION")

package com.mudryakov.collectivenote.utilits

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.database.AppDatabaseRepository
import com.mudryakov.collectivenote.models.User


lateinit var DATABASE_REF: DatabaseReference
lateinit var REPOSITORY: AppDatabaseRepository
lateinit var AUTH: FirebaseAuth
lateinit var CURRENT_UID: String
private lateinit var ON_COMPLETE: () -> Unit


fun logIn(type: String, onCompelete: () -> Unit) {
    ON_COMPLETE = onCompelete
    when (type) {
        TYPE_GOOGLE_ACCOUNT -> logInGogle()
        TYPE_EMAIL -> registerEmail()
    }



}
fun registerEmail() {

    AUTH.createUserWithEmailAndPassword(EMAIL, PASSWORD).addOnSuccessListener {
            val currentId = AUTH.currentUser?.uid.toString()
            pushUserToFirebase(id = currentId, name = "Unknown")
               }
            .addOnFailureListener { showToast(it.message.toString()) }
    }

fun logInGogle() {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .build()

        val mGogoglemSignClient = GoogleSignIn.getClient(APP_ACTIVITY, gso)
        val intent: Intent = mGogoglemSignClient.signInIntent
        APP_ACTIVITY.startActivityForResult(intent, SIGN_CODE_REQUEST)

}

fun initUser(onCompelete: () -> Unit) {
    val listener = AppValueEventListener {
        USER = it.getUserFromFirebase()
        onCompelete()
    }
    DATABASE_REF.child(CURRENT_UID).addListenerForSingleValueEvent(listener)
}

fun handleSignInresult(task: Task<GoogleSignInAccount>) {
    try {
        val account: GoogleSignInAccount = task.getResult(ApiException::class.java)!!
        val name = account.displayName.toString()
        val id = account.id.toString()
        pushUserToFirebase(name, id)

    } catch (e: ApiException) {
        showToast(e.message.toString())

    }
}

fun pushUserToFirebase(name: String, id: String) {
    USER = User(id, name)
    DATABASE_REF.child(id).setValue(USER)
        .addOnFailureListener { showToast(it.message.toString()) }
        .addOnSuccessListener {
            appPreference.setSignIn(true)
            appPreference.setName(USER.name)
            appPreference.setUserId(USER.firebaseId)
           ON_COMPLETE }

}

fun DataSnapshot.getUserFromFirebase() = this.getValue(User::class.java) ?: User()