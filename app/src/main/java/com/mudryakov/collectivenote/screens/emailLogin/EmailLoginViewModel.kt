package com.mudryakov.collectivenote.screens.emailLogin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.mudryakov.collectivenote.database.firebase.EMAIL
import com.mudryakov.collectivenote.database.firebase.PASSWORD
import com.mudryakov.collectivenote.database.firebase.REPOSITORY
import com.mudryakov.collectivenote.utility.TYPE_EMAIL

class EmailLoginViewModel(application: Application) : AndroidViewModel(application) {
    fun loginEmail(email: String, pass: String, onFail: () -> Unit, onSuccess: () -> Unit) {
        EMAIL = email
        PASSWORD = pass
        REPOSITORY.login(TYPE_EMAIL,onFail, onSuccess)
    }
}