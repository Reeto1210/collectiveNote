package com.mudryakov.collectivenote.utility

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.core.view.ViewCompat
import com.mudryakov.collectivenote.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

fun buildNoInternetDialog(onClickPositiveButtonFunction: () -> Unit) {
    val dialog = AlertDialog.Builder(APP_ACTIVITY)
    dialog
        .setCancelable(false)
        .setIcon(R.drawable.ic_no_internet)
        .setTitle(APP_ACTIVITY.getString(R.string.internet_alert_dialog_title))
        .setMessage(APP_ACTIVITY.getString(R.string.internet_alert_dialog_message))
        .setPositiveButton(APP_ACTIVITY.getString(R.string.ok)) { _: DialogInterface, _: Int -> onClickPositiveButtonFunction() }
        .show()

}

fun showNoInternetToast() {
    showToast(R.string.no_internet_toast)
}

fun checkInternetAtAuth(onFail: () -> Unit, onSuccess: () -> Unit) {
    checkInternetConnection({
        buildNoInternetDialog { onFail() }
    }) { onSuccess() }
}

fun checkInternetConnection(onFail: () -> Unit, onSuccess: () -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val sock = Socket()
            sock.connect(InetSocketAddress("8.8.8.8", 53), 1500)
            sock.close()
            CoroutineScope(Dispatchers.Main).launch {
                onSuccess()
            }
        } catch (e: IOException) {
            CoroutineScope(Dispatchers.Main).launch {
                onFail()
            }
        }
    }
}

fun restartIfInternetChanged() {
    if (INTERNET) checkInternetConnection({ restartActivity() }) {}
}

fun initNoInternetBtn() {
    APP_ACTIVITY.mBinding.noInternetIndicatorBtn1.makeVisible()
    APP_ACTIVITY.mBinding.noInternetIndicatorBtn1.setOnClickListener {
        checkInternetConnection({ showNoInternetToast() }) {
            restartActivity()
        }
    }
    startNoInternetAnimation()
}

fun startNoInternetAnimation() {
    val btn = APP_ACTIVITY.mBinding.noInternetIndicatorBtn1
    ViewCompat.animate(btn)
        .alpha(1.0F)
        .setDuration(1000)
        .withEndAction {
            ViewCompat.animate(btn)
                .alpha(0.0f)
                .setDuration(1000)
                .withEndAction {
                    if (
                        !INTERNET) startNoInternetAnimation()
                }
        }
}