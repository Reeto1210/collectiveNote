package com.mudryakov.collectivenote.utilits

import android.net.Uri
import android.os.Environment
import android.provider.Settings.Global.getString
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.database.RoomDatabase.AppRoomRepository
//import com.mudryakov.collectivenote.database.RoomDatabase.MyRoomDatabase
import com.mudryakov.collectivenote.database.RoomDatabase.myDao
import com.mudryakov.collectivenote.database.firebase.*
import com.mudryakov.collectivenote.models.PaymentModel
import com.mudryakov.collectivenote.models.UserModel
import com.mudryakov.collectivenote.screens.mainScreen.MainFragmentViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

fun showToast(text: String) = Toast.makeText(APP_ACTIVITY, text, Toast.LENGTH_SHORT).show()

fun View.startRegisterAnimation(visible: Boolean) {

    if (visible) {
        this.visibility = View.VISIBLE
        ViewCompat.animate(this)
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setDuration(700)
            .setInterpolator(AccelerateDecelerateInterpolator())
    } else {
        ViewCompat.animate(this)
            .scaleX(0.0F)
            .scaleY(0.0F)
            .alpha(0.0F)
            .translationY(translationY)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setDuration(600)
            .withEndAction { this.visibility = View.INVISIBLE }
    }
}

 fun String.transformToDate(): String {
 val date = Date(this.toLong())
    val timeFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return timeFormat.format(date)
}

fun getImage(url:String):Uri?{
    val fileName= Uri.parse(url).lastPathSegment?.substringAfter("/").toString()



  //  if(файл с таким fileName находится в internalStorage){
    //
    //  return uri
    //  }else{
    //  download
    //  returnUri
    //  }

    val file =File(APP_ACTIVITY.filesDir,fileName)
    showToast(fileName)
    try{
        REF_DATABASE_STORAGE.child(NODE_PAYMENT_IMAGES).child(fileName).getFile(file)
      return file.absolutePath.toUri()
    }catch (e:Exception){ showToast(APP_ACTIVITY.getString(R.string.eror_in_download))}
return null
}
