package com.mudryakov.collectivenote.screens.mainScreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.mudryakov.collectivenote.UserManager.AppUserManager
import com.mudryakov.collectivenote.database.AppDatabaseRepository
import com.mudryakov.collectivenote.database.RoomDatabase.AppRoomRepository
import com.mudryakov.collectivenote.database.firebase.*
import com.mudryakov.collectivenote.models.UserModel
import com.mudryakov.collectivenote.utility.*
import javax.inject.Inject

class MainFragmentViewModel @Inject constructor(
    application: Application,
    val userManager: AppUserManager,
    val roomRepository: AppRoomRepository
) : AndroidViewModel(application) {

    fun repository():AppDatabaseRepository = if (APP_ACTIVITY.internet) userManager.repository else roomRepository

    fun  allMembers(): LiveData<List<UserModel>> = repository().groupMembers

    fun getUpdateCurrency(){
      if (AppPreference.getCurrency() == FAIL)
         repository().getGroupCurrencyFromDB()
        }

   fun getCurrency() = AppPreference.getCurrency()

    fun updateGroupId() {
        CURRENT_GROUP_UID = AppPreference.getRoomId()
    }


}
