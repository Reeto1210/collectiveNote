package com.mudryakov.collectivenote.utilits

import android.annotation.SuppressLint
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior

class AppBottomSheetCallBack(val fab: View, val btnBottomSheet: View, val back: View) :

    BottomSheetBehavior.BottomSheetCallback() {
    var coefForRotation = 1

    @SuppressLint("SwitchIntDef")
    override fun onStateChanged(bottomSheet: View, newState: Int) {
        when (newState) {
            BottomSheetBehavior.STATE_COLLAPSED -> {
                back.visibility = View.INVISIBLE
                coefForRotation = 1
            }
            BottomSheetBehavior.STATE_EXPANDED -> coefForRotation = -1
            else -> back.visibility = View.VISIBLE
        }
    }

    override fun onSlide(bottomSheet: View, slideOffset: Float) {

        fab.animate().scaleX(1 - slideOffset).scaleY(1 - slideOffset).setDuration(0).start()
        back.animate().alpha(slideOffset).setDuration(0).start()
        btnBottomSheet.animate().rotation(slideOffset * coefForRotation * 180 - 90)
            .setDuration(0)  //rotate at xml 270
            .start()

    }
}