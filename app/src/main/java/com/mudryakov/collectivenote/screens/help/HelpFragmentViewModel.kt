package com.mudryakov.collectivenote.screens.help

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.mudryakov.collectivenote.utility.makeDonation

class HelpFragmentViewModel(application: Application) : AndroidViewModel(application) {
    fun helpDonation(onSuccess:()->Unit){
        makeDonation(onSuccess)
    }
}