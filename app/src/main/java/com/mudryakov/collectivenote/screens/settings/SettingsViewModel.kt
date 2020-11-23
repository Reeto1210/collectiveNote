package com.mudryakov.collectivenote.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.mudryakov.collectivenote.database.firebase.REPOSITORY

class SettingsViewModel(application: Application):AndroidViewModel(application){
    fun signOut(){
        REPOSITORY.signOut()
    }
    val allpayments = REPOSITORY.allPayments
    val allMembers = REPOSITORY.groupMembers


}