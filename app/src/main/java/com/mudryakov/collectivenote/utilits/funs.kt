package com.mudryakov.collectivenote.utilits


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
    INTERNET = true
    APP_ACTIVITY.startActivity(intent)
    APP_ACTIVITY.finish()
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
    val text =
        when (ex) {
            is FirebaseAuthInvalidCredentialsException -> APP_ACTIVITY.getString(R.string.exception_check_credentials)
            is FirebaseAuthInvalidUserException -> APP_ACTIVITY.getString(R.string.exception_login_user_invalid)
            else -> APP_ACTIVITY.getString(R.string.something_going_wrong)
        }
    showToast(text)
}

fun exceptionEmailRegistrationToast(exText: String) {
    val toastText =
        when {
            exText.contains("The email address is badly formatted") -> APP_ACTIVITY.getString(R.string.exception_check_email)
            exText.contains("The given password is invalid") -> APP_ACTIVITY.getString(R.string.exception_pass_must_be_six_letters)
            exText.contains("The email address is already in use by another account") -> APP_ACTIVITY.getString(
                R.string.exception_email_busy
            )
            else -> APP_ACTIVITY.getString(R.string.something_going_wrong)
        }
    showToast(toastText)
}


