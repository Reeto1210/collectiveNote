package com.mudryakov.collectivenote.screens.help

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.mudryakov.collectivenote.MyApplication
import com.mudryakov.collectivenote.utility.makeDonation
import javax.inject.Inject

class HelpFragmentViewModel @Inject constructor(application: Application):AndroidViewModel(application) {
    fun helpDonation(onSuccess:()->Unit){
        makeDonation(onSuccess)
    }
}