package com.mudryakov.collectivenote.utilits

import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.core.view.ViewCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mudryakov.collectivenote.database.firebase.*
import com.mudryakov.collectivenote.models.User

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
fun initCommons() {
    appPreference.getPreference(APP_ACTIVITY)
    REPOSITORY = FireBaseRepository()
    AUTH = FirebaseAuth.getInstance()
    REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference

}