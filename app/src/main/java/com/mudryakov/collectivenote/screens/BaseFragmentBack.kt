package com.mudryakov.collectivenote.screens

import androidx.fragment.app.Fragment
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.utility.APP_ACTIVITY
import com.mudryakov.collectivenote.utility.DRAWER_ENABLED

open class BaseFragmentBack : Fragment() {

    override fun onStart() {
        super.onStart()
        DRAWER_ENABLED = false
        APP_ACTIVITY.back = true
        APP_ACTIVITY.actionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)


    }


}