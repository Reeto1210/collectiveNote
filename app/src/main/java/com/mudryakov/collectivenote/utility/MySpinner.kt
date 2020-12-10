package com.mudryakov.collectivenote.utility

import android.content.Context
import android.util.AttributeSet
import android.widget.Spinner
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil.hideKeyboard

class MySpinner(context: Context,attrs: AttributeSet): androidx.appcompat.widget.AppCompatSpinner(context,attrs) {
    
    override fun performClick(): Boolean {
        hideKeyboard(APP_ACTIVITY)
        return super.performClick()
    }
}