package com.mudryakov.collectivenote.screens.emailLogin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.mudryakov.collectivenote.MyApplication
import com.mudryakov.collectivenote.UserManager.AppUserManager
import com.mudryakov.collectivenote.database.firebase.EMAIL
import com.mudryakov.collectivenote.database.firebase.FireBaseRepository
import com.mudryakov.collectivenote.database.firebase.PASSWORD
import com.mudryakov.collectivenote.utility.TYPE_EMAIL
import javax.inject.Inject

class EmailLoginViewModel @Inject constructor(application: Application, val userManager: AppUserManager):AndroidViewModel(application) {
    fun loginEmail(email: String, pass: String, onFail: () -> Unit, onSuccess: () -> Unit) {
        EMAIL = email
        PASSWORD = pass
       userManager.login(TYPE_EMAIL,onFail, onSuccess)
    }
}