package com.mudryakov.collectivenote.screens.groupChoose

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mudryakov.collectivenote.MyApplication
import com.mudryakov.collectivenote.UserManager.AppUserManager
import com.mudryakov.collectivenote.database.firebase.FireBaseRepository
import com.mudryakov.collectivenote.utility.AppPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class GroupChooseViewModel @Inject constructor(application: Application, private val userManager: AppUserManager):AndroidViewModel(application) {
private  val firebaseRepository = userManager.repository

   fun getName() =  userManager.getName()

    fun createGroup(name: String, pass: String, currencySign:String, onFail:()->Unit, onSuccess: () -> Unit)= viewModelScope.launch(
        Dispatchers.IO
    ) {
        firebaseRepository.createNewGroup(name, pass,currencySign, onFail,onSuccess)
    }

    fun joinGroup(name: String, pass: String, onFail:()->Unit, onSuccess: () -> Unit )= viewModelScope.launch(
        Dispatchers.IO
    ) {
        firebaseRepository.joinGroup(name, pass, onFail,onSuccess)
    }

}