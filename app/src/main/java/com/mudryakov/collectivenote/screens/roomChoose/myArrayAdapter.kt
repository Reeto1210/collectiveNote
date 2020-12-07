package com.mudryakov.collectivenote.screens.roomChoose

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.mudryakov.collectivenote.utility.makeGone


class MyArrayAdapter(context: Context, layoutId: Int, array: Array<String>) : ArrayAdapter<String>(
    context,
    layoutId,
    array
) {
  override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
      return  if (position == 0) {
          val tv:View = TextView(context)
                tv.makeGone()
                    tv
        } else {
          super.getDropDownView(position, null, parent);
       }
   }

}



