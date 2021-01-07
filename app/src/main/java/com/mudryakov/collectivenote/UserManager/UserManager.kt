package com.mudryakov.collectivenote.UserManager

import com.google.firebase.auth.FirebaseAuth
import com.mudryakov.collectivenote.database.AppDatabaseRepository
import com.mudryakov.collectivenote.database.firebase.createNewEmailUser
import com.mudryakov.collectivenote.database.firebase.logIn
import com.mudryakov.collectivenote.utility.AppPreference
import javax.inject.Inject

class AppUserManager @Inject constructor(
    val repository: AppDatabaseRepository,
    private val firebaseAuth: FirebaseAuth
) {

    fun getDoneRegistration(): Boolean = AppPreference.getDoneRegistration()
    fun getAuthSignIn() = firebaseAuth.currentUser != null
    fun getName(): String = AppPreference.getUserName()

    fun login(type: String, onFail: () -> Unit, onSuccess: () -> Unit) {
        logIn(type, onFail, firebaseAuth) { onSuccess() }
    }

    fun emailRegistration(onFail: () -> Unit, onSuccess: () -> Unit) {
        createNewEmailUser(firebaseAuth, onFail, onSuccess)
    }


    fun changeName(name: String, onSuccess: () -> Unit) {
        repository.changeName(name) { onSuccess() }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }


}