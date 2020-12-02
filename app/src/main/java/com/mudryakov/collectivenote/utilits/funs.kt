package com.mudryakov.collectivenote.utilits

//import com.mudryakov.collectivenote.database.RoomDatabase.MyRoomDatabase
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.mudryakov.collectivenote.MainActivity
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.database.firebase.NODE_PAYMENT_IMAGES
import com.mudryakov.collectivenote.database.firebase.REF_DATABASE_STORAGE
import com.squareup.picasso.Picasso
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

fun ImageView.setImage(url: String) {
    val fileName = Uri.parse(url).lastPathSegment?.substringAfter("/").toString()
    val file = File(APP_ACTIVITY.filesDir, fileName)
    if (file.exists() && file.length() != 0L) {
        val uri = file.absolutePath.toUri()
        this.setImageURI(uri)
    } else {
        Picasso.get()
            .load(url)
            .error(R.drawable.sorry)
            .placeholder(R.drawable.loading_picasso)
            .fit()
            .into(this)
        file.delete()
        REF_DATABASE_STORAGE.child(NODE_PAYMENT_IMAGES).child(fileName).getFile(file)

    }


}

fun restartActivity() {
    val intent = Intent(APP_ACTIVITY, MainActivity::class.java)
    APP_ACTIVITY.startActivity(intent)
    APP_ACTIVITY.finish()
}

fun fastNavigate(id: Int) {
    APP_ACTIVITY.navConroller.navigate(id)
}

fun View.makeVisible() {
    this.visibility = View.VISIBLE
}

fun View.makeInvisible() {
    this.visibility = View.INVISIBLE
}

fun View.makeGone() {
    this.visibility = View.GONE
}


fun checkConnectity(): Boolean {
    val connectivityManager = APP_ACTIVITY.getSystemService(Context.CONNECTIVITY_SERVICE)
    return if (connectivityManager is ConnectivityManager)
    {
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo?.isConnected ?:false
    } else false

}
fun buildNoInternetDialog(onClickPositiveButtonFunction:()->Unit){
    val dialog = AlertDialog.Builder(APP_ACTIVITY)
      dialog
        .setCancelable(false)
          .setIcon(R.drawable.ic_no_internet)
          .setTitle(APP_ACTIVITY.getString(R.string.internet_alert_dialog_title))
        .setMessage(APP_ACTIVITY.getString(R.string.internet_alert_dialog_message))
        .setPositiveButton(APP_ACTIVITY.getString(R.string.ok)){ _: DialogInterface, _: Int -> onClickPositiveButtonFunction()}
        .show()
}

fun showExeptionToast(ex: Any) {
    val toastText =
        when (ex) {
            is FirebaseAuthInvalidCredentialsException -> "Неверный пароль"
            else -> "что то пошло не так"
        }

    showToast(toastText)
}


