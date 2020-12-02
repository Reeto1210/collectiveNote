package com.mudryakov.collectivenote.screens.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.mudryakov.collectivenote.database.RoomDatabase.AppRoomRepository
import com.mudryakov.collectivenote.database.RoomDatabase.MyRoomDatabase
//import com.mudryakov.collectivenote.database.RoomDatabase.MyRoomDatabase
import com.mudryakov.collectivenote.database.firebase.*
import com.mudryakov.collectivenote.utilits.APP_ACTIVITY
import com.mudryakov.collectivenote.utilits.TYPE_EMAIL
import com.mudryakov.collectivenote.utilits.TYPE_GOOGLE_ACCOUNT
import com.mudryakov.collectivenote.utilits.appPreference

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    val mContext = application.applicationContext

    fun login(type: String, email: String = "", password: String = "", onFail:()->Unit, onSucces: () -> Unit
    ) {
        when (type) {
            TYPE_GOOGLE_ACCOUNT -> REPOSITORY.login(TYPE_GOOGLE_ACCOUNT, onFail,onSucces)
            TYPE_EMAIL -> {
                EMAIL = email
                PASSWORD = password
                REPOSITORY.login(TYPE_EMAIL, onFail, onSucces)
            }
        }
    }


    fun changeName(name: String, onSucces: () -> Unit) {
        REPOSITORY.changeName(name) {
            onSucces()
        }
    }

    fun initCommons() {
        appPreference.getPreference(APP_ACTIVITY)
        val dao = MyRoomDatabase.getDatabase(mContext).getDao()
        ROOM_REPOSITORY = AppRoomRepository(dao)
        REPOSITORY = FireBaseRepository()
        AUTH = FirebaseAuth.getInstance()
        REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
        REF_DATABASE_STORAGE = FirebaseStorage.getInstance().reference

    }

}