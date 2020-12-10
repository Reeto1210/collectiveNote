package com.mudryakov.collectivenote.screens.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.mudryakov.collectivenote.database.firebase.REPOSITORY


class HistoryFragmentViewModel(application: Application) : AndroidViewModel(application) {

    val paymentList = REPOSITORY.allPayments
}

