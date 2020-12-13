package com.mudryakov.collectivenote.utility


import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.mudryakov.collectivenote.MainActivity
import com.mudryakov.collectivenote.R
import com.mudryakov.collectivenote.database.firebase.FireBaseRepository
import com.mudryakov.collectivenote.database.firebase.NODE_PAYMENT_IMAGES
import com.mudryakov.collectivenote.database.firebase.REF_DATABASE_STORAGE
import com.mudryakov.collectivenote.database.firebase.REPOSITORY
import com.squareup.picasso.Picasso
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

fun showToast(id: Int) =
    Toast.makeText(APP_ACTIVITY, APP_ACTIVITY.getString(id), Toast.LENGTH_SHORT).show()

fun View.startRegisterAnimation(visible: Boolean) {
    if (visible) {
        this.visibility = View.VISIBLE
        ViewCompat.animate(this)
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setDuration(700).interpolator = AccelerateDecelerateInterpolator()
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
    APP_ACTIVITY.finish()
    APP_ACTIVITY.startActivity(intent)
    REPOSITORY = FireBaseRepository()
    INTERNET = false
}

fun fastNavigate(id: Int) {
    APP_ACTIVITY.navController.navigate(id)
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

fun exceptionEmailLoginToast(ex: Any) {
    val textId =
        when (ex) {
            is FirebaseAuthInvalidCredentialsException -> R.string.exception_check_credentials
            is FirebaseAuthInvalidUserException -> R.string.exception_login_user_invalid
            else -> R.string.something_going_wrong
        }
    showToast(textId)
}

fun exceptionEmailRegistrationToast(exText: String) {
    val toastTextId =
        when {
            exText.contains("The email address is badly formatted") -> R.string.exception_check_email
            exText.contains("The given password is invalid") -> R.string.exception_pass_must_be_six_letters
            exText.contains("The email address is already in use by another account") -> R.string.exception_email_busy

            else -> R.string.something_going_wrong
        }
    showToast(toastTextId)
}

fun calculateSum(firstString: String, secondString: String): String {
    val n1 = firstString.replace(".", "").toInt()
    val n2 = secondString.replace(".", "").toInt()

    return if (ROOM_CURRENCY == APP_ACTIVITY.getString(R.string.RUB)) (n1 + n2).toString()
    else {

        val result = (n1 + n2).toString()
              if (result == "0") return "0.00"
     val  i = result.dropLast(2) + "." + result.substring(result.lastIndex - 1
     )
        return i

    }

}

fun calculateMinus(firstString: String, secondString: String): String {
    val n1 = firstString.replace(".", "").toInt()
    val n2 = secondString.replace(".", "").toInt()
    return if (ROOM_CURRENCY == APP_ACTIVITY.getString(R.string.RUB)) (n1 - n2).toString()
    else {

        val result = (n1 - n2).toString()
        if (result == "0") return "0.00"
        return result.dropLast(2) + "." + result.substring(result.lastIndex - 1)

    }

}



fun initHomeUpFalse() {
    APP_ACTIVITY.actionBar?.setDisplayHomeAsUpEnabled(false)
    APP_ACTIVITY.back = false
}

fun buildRoomChooseDialog(click: (String) -> Unit) {
    val builder = AlertDialog.Builder(APP_ACTIVITY)
    builder.setTitle(APP_ACTIVITY.getString(R.string.choose_room_alert_dialog_title))
        .setMessage(APP_ACTIVITY.getString(R.string.choose_room_alert_dialog_text))
        .setPositiveButton(APP_ACTIVITY.getString(R.string.choose_room_alert_dialog_positive_button)) { _: DialogInterface, _: Int ->
            click(CREATOR)
        }
        .setNeutralButton(APP_ACTIVITY.getString(R.string.choose_room_alert_dialog_negative_button)) { _: DialogInterface, _: Int ->
            click(MEMBER)
        }
        .setCancelable(false)
        .show()
}

fun convertSum(sum: String): String {
    sum.toDouble()
    if (sum[0] == '0' && sum[1] != '.') throw Exception("")
    if (sum[0] == '-' || sum[0] == '.') throw Exception("")
    return if (ROOM_CURRENCY == APP_ACTIVITY.getString(R.string.RUB)) {
        sum.substringBefore(".")
    } else {
        if (!sum.contains('.')) {
            "$sum.00"
        } else {
            val dotIndex = sum.indexOf(".")
            try {
                sum.substring(0, dotIndex + 3)
            } catch (e: Exception) {
                sum.substring(0, dotIndex + 2) + "0"
            }
        }
    }
}