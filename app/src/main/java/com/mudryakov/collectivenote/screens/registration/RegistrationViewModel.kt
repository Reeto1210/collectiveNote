package com.mudryakov.collectivenote.screens.registration

//import com.mudryakov.collectivenote.database.RoomDatabase.MyRoomDatabase
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.UserManager.AppUserManager
import com.mudryakov.collectivenote.database.firebase.EMAIL
import com.mudryakov.collectivenote.database.firebase.PASSWORD
import com.mudryakov.collectivenote.utility.TYPE_EMAIL
import com.mudryakov.collectivenote.utility.TYPE_GOOGLE_ACCOUNT
import com.mudryakov.collectivenote.utility.fastNavigate
import javax.inject.Inject

class RegistrationViewModel @Inject constructor(
    application: Application,
    private val userManager: AppUserManager
) : AndroidViewModel(application) {


    fun registration(
        type: String,
        email: String = "",
        password: String = "",
        onFail: () -> Unit,
        onSuccess: () -> Unit
    ) {
        when (type) {
            TYPE_GOOGLE_ACCOUNT -> userManager.login(TYPE_GOOGLE_ACCOUNT, onFail, onSuccess)
            TYPE_EMAIL -> {
                EMAIL = email
                PASSWORD = password
                userManager.emailRegistration(onFail, onSuccess)
            }
        }
    }


    fun changeName(name: String, onSuccess: () -> Unit) {
        userManager.changeName(name) {
            onSuccess()
        }
    }

    fun checkUserForRegistration():Boolean? =
        when{
            userManager.getDoneRegistration() -> true
            userManager.getAuthSignIn() -> false
            else -> null
          }


}