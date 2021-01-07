package com.mudryakov.collectivenote.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.mudryakov.collectivenote.MyApplication
import com.mudryakov.collectivenote.UserManager.AppUserManager
import com.mudryakov.collectivenote.database.firebase.FireBaseRepository
import javax.inject.Inject

class SettingsViewModel @Inject constructor(application: Application, val userManager: AppUserManager):AndroidViewModel(application) {
    fun signOut() {
        userManager.signOut()
    }

    fun changeName(name: String, onSuccess: () -> Unit) {
        userManager.changeName(name, onSuccess)

    }

}