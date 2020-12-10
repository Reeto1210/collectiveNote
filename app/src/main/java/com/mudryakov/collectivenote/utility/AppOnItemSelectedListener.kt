package com.mudryakov.collectivenote.utility

import android.view.View
import android.widget.AdapterView
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil.hideKeyboard

class AppOnItemSelectedListener(val funOnSelected:(position:Int)->Unit):AdapterView.OnItemSelectedListener {

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
       funOnSelected(p2)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

}