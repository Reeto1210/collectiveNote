package com.mudryakov.collectivenote.screens.registration

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mudryakov.collectivenote.database.firebase.EMAIL
import com.mudryakov.collectivenote.database.firebase.PASSWORD
import com.mudryakov.collectivenote.database.firebase.REPOSITORY
import com.mudryakov.collectivenote.utilits.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class RegistrationViewModel(application: Application) : AndroidViewModel(application) {

    fun connectToFirebase(
        type: String,
        email: String = "",
        password: String = "",
        onSucces: () -> Unit
    )  {
        when (type) {
            TYPE_GOOGLE_ACCOUNT -> REPOSITORY.connectToDatabase(TYPE_GOOGLE_ACCOUNT, onSucces)
            TYPE_EMAIL -> {
                EMAIL = email
                PASSWORD = password
                REPOSITORY.connectToDatabase(TYPE_EMAIL, onSucces)
            }
        }
    }



    fun changeName(name: String, onSucces: () -> Unit) {
        REPOSITORY.changeName(name) {
             onSucces()
        }

    }

}