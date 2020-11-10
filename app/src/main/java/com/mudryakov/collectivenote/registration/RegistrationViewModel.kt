package com.mudryakov.collectivenote.registration

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mudryakov.collectivenote.database.firebase.FireBaseRepository
import com.mudryakov.collectivenote.models.User
import com.mudryakov.collectivenote.utilits.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class RegistrationViewModel(application: Application) : AndroidViewModel(application) {

    fun connectToFirebase(
        type: String,
        email: String = "",
        password: String = "",
        onSucces: () -> Unit
    ) = viewModelScope.launch(IO) {
        when (type) {
            TYPE_GOOGLE_ACCOUNT -> REPOSITORY.connectToDatabase(TYPE_GOOGLE_ACCOUNT, onSucces)
            TYPE_EMAIL -> {
                EMAIL = email
                PASSWORD = password
                REPOSITORY.connectToDatabase(TYPE_EMAIL, onSucces)
            }
        }
    }

    fun initCommons() {
        appPreference.getPreference(APP_ACTIVITY)
        REPOSITORY = FireBaseRepository()
        CURRENT_UID = appPreference.getUserId()
        AUTH = FirebaseAuth.getInstance()
        DATABASE_REF = FirebaseDatabase.getInstance().reference
        USER = User(CURRENT_UID, appPreference.getUserName())
    }

    fun changeName(name: String, onSucces: () -> Unit) {
        REPOSITORY.changeName(name) {
            USER.name = name
            appPreference.setName(name)
            onSucces()
        }

    }

}