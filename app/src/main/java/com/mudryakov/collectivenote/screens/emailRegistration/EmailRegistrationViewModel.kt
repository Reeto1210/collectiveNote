package com.mudryakov.collectivenote.screens.emailRegistration

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.mudryakov.collectivenote.database.firebase.EMAIL
import com.mudryakov.collectivenote.database.firebase.PASSWORD
import com.mudryakov.collectivenote.database.firebase.REPOSITORY

class EmailRegistrationViewModel(application: Application) : AndroidViewModel(application) {
    fun createEmailAccount(email: String, pass: String, onFail: () -> Unit, onSuccess: () -> Unit) {
        EMAIL = email
        PASSWORD = pass
        REPOSITORY.emailRegistration(onFail, onSuccess)
    }
}