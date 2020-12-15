package com.mudryakov.collectivenote.screens.registration

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.mudryakov.collectivenote.database.RoomDatabase.AppRoomRepository
import com.mudryakov.collectivenote.database.RoomDatabase.MyRoomDatabase
//import com.mudryakov.collectivenote.database.RoomDatabase.MyRoomDatabase
import com.mudryakov.collectivenote.database.firebase.*
import com.mudryakov.collectivenote.utility.APP_ACTIVITY
import com.mudryakov.collectivenote.utility.TYPE_EMAIL
import com.mudryakov.collectivenote.utility.TYPE_GOOGLE_ACCOUNT
import com.mudryakov.collectivenote.utility.AppPreference

class RegistrationViewModel(application: Application) : AndroidViewModel(application) {
    private val mContext: Context = application.applicationContext

    fun registration(type: String, email: String = "", password: String = "", onFail:()->Unit, onSucces: () -> Unit
    ) {
        when (type) {
            TYPE_GOOGLE_ACCOUNT -> REPOSITORY.login(TYPE_GOOGLE_ACCOUNT, onFail,onSucces)
            TYPE_EMAIL -> {
                EMAIL = email
                PASSWORD = password
                REPOSITORY.emailRegistration(onFail, onSucces)
            }
        }
    }


    fun changeName(name: String, onSuccess: () -> Unit) {
        REPOSITORY.changeName(name) {
            onSuccess()
        }
    }

    fun initCommons() {
        AppPreference.getPreference(APP_ACTIVITY)
        val dao = MyRoomDatabase.getDatabase(mContext).getDao()
        ROOM_REPOSITORY = AppRoomRepository(dao)
        REPOSITORY = FireBaseRepository()
        AUTH = FirebaseAuth.getInstance()
        REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
        REF_DATABASE_STORAGE = FirebaseStorage.getInstance().reference

    }

}