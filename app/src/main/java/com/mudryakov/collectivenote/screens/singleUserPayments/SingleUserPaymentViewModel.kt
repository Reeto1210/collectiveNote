package com.mudryakov.collectivenote.screens.singleUserPayments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.mudryakov.collectivenote.database.firebase.REPOSITORY

class SingleUserPaymentViewModel(application: Application): AndroidViewModel(application){
    val singleUserPayments = REPOSITORY.allPayments
}