package com.mudryakov.collectivenote.screens.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.mudryakov.collectivenote.MyApplication
import com.mudryakov.collectivenote.database.RoomDatabase.AppRoomRepository
import com.mudryakov.collectivenote.database.firebase.FireBaseRepository
import com.mudryakov.collectivenote.utility.APP_ACTIVITY
import javax.inject.Inject


class HistoryFragmentViewModel
@Inject constructor(application: Application,
                     fireBaseRepository: FireBaseRepository,
                     roomRepository: AppRoomRepository):AndroidViewModel(application) {

    val paymentList = if (APP_ACTIVITY.internet) fireBaseRepository.allPayments else roomRepository.allPayments

}

